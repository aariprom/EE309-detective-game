package com.ee309.detectivegame.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ee309.detectivegame.domain.model.GameState
import com.ee309.detectivegame.presentation.state.GameUiState
import com.ee309.detectivegame.domain.generator.MockGameData.createInitialGameState
import com.ee309.detectivegame.domain.model.GameAction
import com.ee309.detectivegame.domain.model.GamePhase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import com.ee309.detectivegame.ui.compose.ConversationMessage

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.ee309.detectivegame.llm.client.OpenAiApiService
import com.ee309.detectivegame.llm.client.ChatMessage
import com.ee309.detectivegame.llm.client.ChatRequest

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*


@OptIn(InternalSerializationApi::class)
@HiltViewModel
class GameViewModel @Inject constructor(
    private val openAiApi: OpenAiApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    }
    
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
    
    private val _conversationHistory = MutableStateFlow<Map<String, List<ConversationMessage>>>(emptyMap())
    val conversationHistory: StateFlow<Map<String, List<ConversationMessage>>> = _conversationHistory.asStateFlow()
    
    init {
        // GameUIState Error with empty message indicates initial state
        // TODO: Make a distinct UI state for very first initialization
        _uiState.value = GameUiState.Error("")
    }
    
    @OptIn(InternalSerializationApi::class)
    private suspend fun generateInitialGameStateWithAi(keywords: String): GameState {
        val systemPrompt = """
            You MUST output a valid JSON object matching the GameState Kotlin model.

            General rules:
            - Output ONLY a single JSON object. No markdown, no ``` fences, no explanations.
            - All required fields for each type must be present.
            - All GameTime values are in minutes (Int).

            Time rules:
            - The investigation starts at a random time.
            - currentTime.minutes represents the time of day in minutes from 00:00 (0 = 00:00, 60 = 01:00, 225 = 03:45, 1095 = 18:15).
            - currentTime.minutes must be a multiple of 5
            - timeline.startTime.minutes must be equal to currentTime.minutes.
            - Let remainingMinutes = timeline.endTime.minutes - currentTime.minutes.
            - remainingMinutes must be between 600 and 2880 (10 to 48 hours).
            - timeline.endTime.minutes must be currentTime.minutes + remainingMinutes.
            - All other GameTime.minutes values should also be multiples of 5.

            Phase rules:
            - phase must be exactly "START" in the initial state.

            Size rules:
            - The characters list must contain between 6 and 10 Character objects.
            - Exactly one character must be the victim (for example mentalState = "Deceased" or a clear victim trait).
            - Exactly one character must be the criminal (isCriminal = true).
            - The remaining characters are witnesses or related people (isCriminal = false, not deceased).
            - The places list must contain between 3 and 5 Place objects.

            Naming rules:
            - Character names must be human-readable person names.
            - Place names must be human-readable location names, e.g. "CEO Office", "Building Lobby", "Parking Lot".

            Types and fields:

            GameState {
            phase: one of ["START","INVESTIGATION","CONVERSATION","ACCUSATION","WIN","LOSE"],
            currentTime: { minutes: Int },
            player: Player,
            characters: List<Character>,
            places: List<Place>,
            clues: List<Clue>,
            timeline: Timeline,
            flags: Map<String, Boolean>
            }

            Player {
            name: String,
            currentLocation: String,
            collectedClues: List<String>,
            tools: List<String>
            }

            Character {
            id: String,
            name: String,
            traits: List<String>,
            isCriminal: Boolean,
            knownClues: List<String>,
            mentalState: String,
            hidden: Boolean,
            unlockConditions: List<String>,
            items: List<String>,
            currentLocation: String
            }

            Place {
            id: String,
            name: String,
            traits: List<String>,
            availableClues: List<String>,
            hidden: Boolean,
            unlockConditions: List<String>,
            items: List<String>,
            currentCharacters: List<String>,
            connectedPlaces: List<String>
            }

            Clue {
            id: String,
            who: String?,
            whom: String?,
            time: { minutes: Int }?,
            place: String,
            content: String,
            unlockConditions: List<String>
            }

            Timeline {
            startTime: { minutes: Int },
            endTime: { minutes: Int },
            events: List<TimelineEvent>
            }

            TimelineEvent {
            id: String,
            time: { minutes: Int },
            eventType: one of ["EVIDENCE_DESTRUCTION","CHARACTER_MOVEMENT","CLUE_AVAILABILITY"],
            characterId: String?,
            placeId: String?,
            description: String,
            action: String,
            affectedComponents: List<String>
            }

            IDs:
            - All ids (for characters, places, clues, events) must be short strings like "char_1", "place_1", "clue_1", "event_1".
        """.trimIndent()

        val userPrompt = if (keywords.isBlank()) {
            "Create a small detective scenario with one victim, one criminal, two witnesses, three locations, five clues and an 8-hour investigation timeline."
        } else {
            "Create a small detective scenario based on these keywords: $keywords. Use one victim, one criminal, two witnesses, three locations, five clues and an 8-hour investigation timeline."
        }

        val request = ChatRequest(
            model = "gpt-4o",
            messages = listOf(
                ChatMessage(role = "system", content = systemPrompt),
                ChatMessage(role = "user", content = userPrompt)
            ),
            max_tokens = 3000,
            temperature = 0.7
        )

        val response = openAiApi.chatCompletion(request)
        val content = response.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException("Empty LLM response")

        val start = content.indexOf('{')
        val end = content.lastIndexOf('}')
        if (start == -1 || end == -1 || end <= start) {
            throw IllegalStateException("LLM response does not contain valid JSON object")
        }
        val rawJson = content.substring(start, end + 1)

        val json = Json {
            ignoreUnknownKeys = true
        }

        val element = json.parseToJsonElement(rawJson)
        val root = element.jsonObject

        val phaseRaw = root["phase"]?.jsonPrimitive?.content ?: "START"
        val normalizedPhase = when (phaseRaw.trim().lowercase()) {
            "start" -> "START"
            "investigation" -> "INVESTIGATION"
            "accusation" -> "ACCUSATION"
            "win" -> "WIN"
            "lose", "fail", "failed" -> "LOSE"
            else -> "START"
        }

        val fixedRoot: JsonObject = JsonObject(
            root.toMutableMap().apply {
                this["phase"] = JsonPrimitive(normalizedPhase)
            }
        )

        return json.decodeFromJsonElement<GameState>(fixedRoot)
    }


    fun startNewGame(keywords: String) {
        viewModelScope.launch {
            _uiState.value = GameUiState.Loading
            _conversationHistory.value = emptyMap()
            try {
                val initialState = generateInitialGameStateWithAi(keywords)
                _gameState.value = initialState
                _uiState.value = GameUiState.Success(initialState)
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error(e.message ?: "AI init failed")
            }
        }
    }
    
    fun executeAction(action: GameAction) {
        viewModelScope.launch {
            val currentState = _gameState.value ?: run {
                _uiState.value = GameUiState.Error("Game state is null")
                return@launch
            }

            // 1. Validate action (basic validation)
            if (!isActionValid(action, currentState)) {
                _uiState.value = GameUiState.Error("Action is invalid due to locked or non-existent resources")
                return@launch
            }

            // 2. Execute action (call appropriate handler)
            val newState = when (action) {
                is GameAction.Investigate -> handleInvestigation(action.placeId, currentState)
                is GameAction.Question -> applyAiTransitionForQuestion(action, currentState)
                is GameAction.Move -> handleMovement(action.placeId, currentState)
                is GameAction.Accuse -> handleAccusation(action.characterId, action.evidence, currentState)
            }

            // 3. Update state
            updateGameState(newState)

            // 4. Check win/lose conditions
            checkWinConditions(newState)
        }
    }

    @OptIn(InternalSerializationApi::class)
    fun transitionToPhase(phase: GamePhase) {
        try {
            val currentState = _gameState.value?: throw Exception("Game state is null")
            val currentPhase = currentState.phase

            // sanity check
            if (currentPhase == phase) {
                throw Exception("Phase is already $phase")
            }

            // actual transition logic
            val newState = currentState.copy(phase = phase)
            _gameState.value = newState
            _uiState.value = GameUiState.Success(newState)

        } catch (e: Exception) {
            _uiState.value = GameUiState.Error(e.message ?: "Unknown error")
        }
    }

    private fun checkWinConditions(state: GameState) {
        if (state.phase == GamePhase.WIN || state.phase == GamePhase.LOSE) {
            return
        }

        // Check time limit
        if (state.currentTime.isAfter(state.timeline.endTime)) {
            // TODO: how to show that this is lose due to time limit?
            transitionToPhase(GamePhase.LOSE)
            return
        }
    }

    private fun serializeState(state: GameState): String {
        return json.encodeToString(state)
    }

    private fun deserializeState(text: String): GameState {
        return json.decodeFromString(text)
    }

    private fun extractJsonBlock(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1)
        }
        return text.trim()
    }

    private fun describeQuestionAction(action: GameAction.Question, state: GameState): String {
        val character = state.getCharacter(action.characterId)
        val location = state.getPlace(state.player.currentLocation)
        val locationName = location?.name ?: "Unknown location"
        val characterName = character?.name ?: action.characterId
        val questionText = action.question ?: ""
        return "The player, as a detective, is at $locationName and is questioning $characterName with the question: \"$questionText\"."
    }

    @OptIn(InternalSerializationApi::class)
    private fun isActionValid(action: GameAction, state: GameState): Boolean {
        return when (action) {
            is GameAction.Investigate -> {
                // Check if place exists and is unlocked
                val place = state.getPlace(action.placeId)
                place != null && place.isUnlocked(state.flags)
            }
            is GameAction.Question -> {
                // Check if character exists and is unlocked
                val character = state.getCharacter(action.characterId)
                character != null && character.isUnlocked(state.flags)
            }
            is GameAction.Move -> {
                // Check if place exists and is unlocked
                val place = state.getPlace(action.placeId)
                place != null && place.isUnlocked(state.flags)
            }
            is GameAction.Accuse -> {
                // Check if character exists
                state.getCharacter(action.characterId) != null
            }
        }
    }

    private fun updateGameState(newState: GameState) {
        _gameState.value = newState
        _uiState.value = GameUiState.Success(newState)
    }

    @OptIn(InternalSerializationApi::class)
    private fun handleInvestigation(placeId: String, state: GameState): GameState {
        val place = state.getPlace(placeId) ?: return state

        // Placeholder: Return description text
        // TODO: Replace with LLM 3 (Description Generator) later

        // For now, just return state with time advanced
        // Time cost: 15 minutes (investigation time)
        val newTime = state.currentTime.addMinutes(15)
        return state.copy(currentTime = newTime)
    }

    @OptIn(InternalSerializationApi::class)
    private fun handleQuestioning(characterId: String, question: String?, state: GameState): GameState {
        val character = state.getCharacter(characterId) ?: return state

        // sanity check: is character in this place?
        if (character.currentLocation !== state.player.currentLocation) {
            return state
        }

        // Placeholder: Return dialogue text
        // TODO: Replace with LLM 2 (Dialogue Generator) later

        // For now, just return state with time advanced
        // Time cost: 20 minutes (questioning time)
        val newTime = state.currentTime.addMinutes(20)
        return state.copy(currentTime = newTime)
    }

    @OptIn(InternalSerializationApi::class)
    private fun handleMovement(placeId: String, state: GameState): GameState {
        val place = state.getPlace(placeId) ?: return state
        val currentPlace = state.getPlace(state.player.currentLocation) ?: return state

        // sanity check
        if (placeId == state.player.currentLocation) {
            return state
        }

        // Calculate distance (simple: 1 if connected, 2 if not)
        val distance = currentPlace.getDistanceTo(place)

        // Time cost: base time + (distance * distance time)
        // Base: 5 minutes, Distance: 5 minutes per unit
        val timeCost = 5 + (distance * 5)

        // Update player location and time
        val newTime = state.currentTime.addMinutes(timeCost)
        val newPlayer = state.player.copy(currentLocation = placeId)

        return state.copy(
            currentTime = newTime,
            player = newPlayer
        )
    }

    @OptIn(InternalSerializationApi::class)
    private suspend fun applyAiTransitionForQuestion(action: GameAction.Question, state: GameState): GameState {
        val stateJson = serializeState(state)
        val actionDescription = describeQuestionAction(action, state)

        val systemPrompt = """
            You are an engine that updates the state of a detective game.
            You receive the current GameState as JSON and a description of the player's Question action.
            You must return the next GameState as JSON only, with no explanations or extra text.
            Field names and types must match the Kotlin data classes:
            GameState, Player, Character, Place, Clue, Timeline, TimelineEvent, GameTime.
            GameTime is represented as an object with integer field "minutes".
            You may update:
            - player.collectedClues
            - characters' fields (mentalState, hidden, currentLocation, etc.)
            - flags map
            - currentTime (advance time for the questioning)
            - timeline, if needed
            Keep IDs and overall structure consistent with the input state.
            Do not invent entirely new structures that do not exist in the input JSON.
        """.trimIndent()

        val userPrompt = """
            Current GameState JSON:
            $stateJson

            Player Question Action:
            $actionDescription

            Return the next GameState as JSON only.
        """.trimIndent()

        val request = ChatRequest(
            model = "gpt-4o-mini",
            messages = listOf(
                ChatMessage(role = "system", content = systemPrompt),
                ChatMessage(role = "user", content = userPrompt)
            ),
            max_tokens = 2000,
            temperature = 0.7
        )

        return try {
            val response = openAiApi.chatCompletion(request)
            val content = response.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("Empty AI response")
            val jsonText = extractJsonBlock(content)
            deserializeState(jsonText)
        } catch (e: Exception) {
            handleQuestioning(action.characterId, action.question, state)
        }
    }

    @OptIn(InternalSerializationApi::class)
    private fun handleAccusation(characterId: String, evidence: List<String>, state: GameState): GameState {
        val character = state.getCharacter(characterId) ?: return state

        // Placeholder: Check if character is criminal
        // TODO: Replace with proper evidence validation later

        val isCorrect = character.isCriminal

        // TODO: Even if the accusation is correct,
        //  player can not win with all evidences needed
        //  maybe target can defend themselves against it (using LLM)

        // Time cost: 5 minutes (accusation time)
        val newTime = state.currentTime.addMinutes(5)
        var newState = state.copy(currentTime = newTime)

        // Transition to win/lose phase
        if (isCorrect) {
            newState = newState.copy(phase = GamePhase.WIN)
        } else {
            // TODO: Maybe give more chances for wrong accusation
            newState = newState.copy(phase = GamePhase.LOSE)
        }

        return newState
    }
    
    fun sendQuestionToCharacter(characterId: String, question: String) {
        viewModelScope.launch {
            val state = _gameState.value ?: return@launch
            val character = state.getCharacter(characterId) ?: return@launch

            addConversationMessage(
                characterId,
                ConversationMessage(
                    text = question,
                    isFromPlayer = true
                )
            )

            val systemPrompt = buildString {
                appendLine("You are an NPC in a detective game.")
                appendLine("Speak as the character in first person.")
                appendLine("Character name: ${character.name}")
                appendLine("Traits: ${character.traits.joinToString()}")
                appendLine("Mental state: ${character.mentalState}")
                appendLine("Answer briefly in a few sentences.")
            }

            val request = ChatRequest(
                model = "gpt-4o-mini",
                messages = listOf(
                    ChatMessage("system", systemPrompt),
                    ChatMessage("user", question)
                ),
                max_tokens = 256,
                temperature = 0.7
            )

            val replyText = try {
                val response = openAiApi.chatCompletion(request)
                response.choices.firstOrNull()?.message?.content ?: "..."
            } catch (e: Exception) {
                "대답을 불러오지 못했습니다: ${e.message ?: "알 수 없는 오류"}"
            }

            addConversationMessage(
                characterId,
                ConversationMessage(
                    text = replyText,
                    isFromPlayer = false
                )
            )

            executeAction(GameAction.Question(characterId, question))
        }
    }


    fun addConversationMessage(characterId: String, message: ConversationMessage) {
        val currentHistory = _conversationHistory.value
        val characterHistory = currentHistory[characterId] ?: emptyList()
        _conversationHistory.value = currentHistory + (characterId to (characterHistory + message))
    }
    
    fun getConversationHistory(characterId: String): List<ConversationMessage> {
        return _conversationHistory.value[characterId] ?: emptyList()
    }
}

