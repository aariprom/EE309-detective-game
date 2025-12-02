package com.ee309.detectivegame.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ee309.detectivegame.domain.model.GameState
import com.ee309.detectivegame.presentation.state.GameUiState
import com.ee309.detectivegame.domain.model.GameAction
import com.ee309.detectivegame.domain.model.GamePhase
import com.ee309.detectivegame.llm.config.LLMTask
import com.ee309.detectivegame.llm.config.LLMResponseProcessor
import com.ee309.detectivegame.llm.data.LLMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import com.ee309.detectivegame.ui.compose.ConversationMessage
import com.ee309.detectivegame.ui.compose.ConversationMessageType
import com.ee309.detectivegame.domain.model.ActionTimeCosts
import com.ee309.detectivegame.domain.model.GameTime
import com.ee309.detectivegame.llm.model.DialogueRequest
import com.ee309.detectivegame.llm.model.toDialogueRequest
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val llmRepository: LLMRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
    
    private val _conversationHistory = MutableStateFlow<Map<String, List<ConversationMessage>>>(emptyMap())
    val conversationHistory: StateFlow<Map<String, List<ConversationMessage>>> = _conversationHistory.asStateFlow()
    
    private val _dialogueLoading = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val dialogueLoading: StateFlow<Map<String, Boolean>> = _dialogueLoading.asStateFlow()
    
    private val _introText = MutableStateFlow<String?>(null)
    val introText: StateFlow<String?> = _introText.asStateFlow()
    
    private val _introShown = MutableStateFlow<Boolean>(false)
    val introShown: StateFlow<Boolean> = _introShown.asStateFlow()
    
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }
    
    init {
        // GameUIState Error with empty message indicates initial state
        // TODO: Make a distinct UI state for very first initialization
        _uiState.value = GameUiState.Error("")
    }
    
    fun startNewGame(keywords: String) {
        viewModelScope.launch {
            _uiState.value = GameUiState.Loading
            _conversationHistory.value = emptyMap() // Clear conversation history
            _introText.value = null // Clear intro text
            _introShown.value = false // Reset intro shown flag
            try {
                // Step 1: Call LLM 1 to generate initial game content
                val userContent = keywords.ifBlank { "Generate a detective mystery game scenario" }
                val gameStateResponse = llmRepository.callUpstage(
                    task = LLMTask.GameInitializer,
                    userContent = userContent,
                    maxTokens = 10000
                )
                
                // Check for empty response
                if (gameStateResponse.isBlank()) {
                    _uiState.value = GameUiState.Error("Received empty response from LLM. Please try again.")
                    return@launch
                }
                
                // Process the LLM 1 response: parse, validate, and convert to GameState
                val gameStateResult = LLMResponseProcessor.GameInitializer.process(gameStateResponse)
                
                val gameState = when (gameStateResult) {
                    is LLMResponseProcessor.ProcessingResult.Success -> {
                        gameStateResult.data
                    }
                    is LLMResponseProcessor.ProcessingResult.Failure -> {
                        _uiState.value = GameUiState.Error(
                            "Failed to generate game: ${gameStateResult.error.message}"
                        )
                        return@launch
                    }
                }
                
                // Store game state (but don't show main game yet - need intro first)
                _gameState.value = gameState
                
                // Step 2: Call LLM 2 to generate intro text
                val introRequest = gameState.toIntroRequest()
                val introRequestJson = json.encodeToString(
                    com.ee309.detectivegame.llm.model.IntroRequest.serializer(),
                    introRequest
                )
                
                val introResponse = llmRepository.callUpstage(
                    task = LLMTask.IntroGenerator,
                    userContent = introRequestJson,
                    maxTokens = 2000
                )
                
                // Check for empty response
                if (introResponse.isBlank()) {
                    // If intro generation fails, still allow game to start without intro
                    _introShown.value = true
                    _uiState.value = GameUiState.Success(gameState)
                    return@launch
                }
                
                // Process the LLM 2 response: parse and validate intro text
                val introResult = LLMResponseProcessor.IntroGenerator.process(introResponse)
                
                when (introResult) {
                    is LLMResponseProcessor.ProcessingResult.Success -> {
                        _introText.value = introResult.data
                        // Keep UI in Loading state - IntroScreen will handle display
                        // The game state is set, but intro needs to be shown first
                    }
                    is LLMResponseProcessor.ProcessingResult.Failure -> {
                        // If intro generation fails, still allow game to start
                        // but log the error
                        _uiState.value = GameUiState.Error(
                            "Failed to generate intro: ${introResult.error.message}. Starting game without intro."
                        )
                        // Mark intro as shown so game can start
                        _introShown.value = true
                        _uiState.value = GameUiState.Success(gameState)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error(
                    "Failed to generate game: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }
    
    /**
     * Called when user finishes reading the intro and wants to start the game
     */
    fun onIntroComplete() {
        _introShown.value = true
        val currentGameState = _gameState.value
        if (currentGameState != null) {
            _uiState.value = GameUiState.Success(currentGameState)
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
                is GameAction.Question -> handleQuestioning(action.characterId, action.question, currentState)
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
        // currentTime is relative to startTime, endTime is absolute
        val currentAbsolute = state.timeline.startTime.minutes + state.currentTime.minutes
        if (currentAbsolute >= state.timeline.endTime.minutes) {
            // TODO: how to show that this is lose due to time limit?
            transitionToPhase(GamePhase.LOSE)
            return
        }
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

        // Get available clues at this location (already filtered by unlock conditions)
        val availableClues = state.getAvailableCluesAtLocation(placeId)
        
        // Filter out clues the player already has
        val newClueIds = availableClues
            .map { it.id }
            .filter { !state.player.collectedClues.contains(it) }
        
        // Start with time advancement
        // Time cost: 15 minutes (investigation time)
        val newTime = state.currentTime.addMinutes(15)
        var newState = state.copy(currentTime = newTime)
        
        // Add new clues to player's collected clues
        if (newClueIds.isNotEmpty()) {
            val updatedCollectedClues = (state.player.collectedClues + newClueIds).distinct()
            val updatedPlayer = state.player.copy(collectedClues = updatedCollectedClues)
            newState = newState.copy(player = updatedPlayer)
        }
        
        // Set investigation flag for this place (useful for future LLM 4 integration)
        val investigationFlag = "investigated_${placeId}"
        val updatedFlags = newState.flags + (investigationFlag to true)
        newState = newState.copy(flags = updatedFlags)
        
        return newState
    }

    @OptIn(InternalSerializationApi::class)
    private suspend fun handleQuestioning(characterId: String, question: String?, state: GameState): GameState {
        val character = state.getCharacter(characterId) ?: return state

        // sanity check: is character in this place?
        if (character.currentLocation != state.player.currentLocation) {
            return state
        }
        
        // Return early if no question provided (no initial greeting)
        if (question == null || question.isBlank()) {
            return state
        }

        // Store previous state for change detection
        val previousCollectedClues = state.player.collectedClues.toSet()
        val previousMentalState = character.mentalState
        
        // Add player's question to conversation history immediately
        addConversationMessage(characterId, ConversationMessage(
            text = question,
            isFromPlayer = true,
            timestamp = state.currentTime,
            type = ConversationMessageType.NORMAL
        ))

        // Set loading state
        _dialogueLoading.value = _dialogueLoading.value + (characterId to true)

        // Get conversation history for this character (includes the question we just added)
        val history = getConversationHistory(characterId)
        
        // Build dialogue request
        val dialogueRequest = try {
            state.toDialogueRequest(
                characterId = characterId,
                playerQuestion = question,
                conversationHistory = history
            )
        } catch (e: Exception) {
            _dialogueLoading.value = _dialogueLoading.value + (characterId to false)
            // Add error message to conversation
            addConversationMessage(characterId, ConversationMessage(
                text = "Error: Failed to build dialogue request. ${e.message ?: "Unknown error"}",
                isFromPlayer = false,
                timestamp = state.currentTime,
                type = ConversationMessageType.NORMAL
            ))
            return state
        }
        
        // Serialize request to JSON
        val requestJson = json.encodeToString(
            DialogueRequest.serializer(),
            dialogueRequest
        )
        
        // Call LLM 3: Dialogue Generator
        val dialogueResponse: String
        try {
            dialogueResponse = llmRepository.callUpstage(
                task = LLMTask.DialogueGenerator,
                userContent = requestJson,
                maxTokens = 2000
            )
        } catch (e: Exception) {
            _dialogueLoading.value = _dialogueLoading.value + (characterId to false)
            // Add error message to conversation instead of just setting UI state
            val errorMessage = when {
                e.message?.contains("timeout", ignoreCase = true) == true -> 
                    "Error: Request timed out. The character is taking too long to respond. Please try again."
                e.message?.contains("Network error", ignoreCase = true) == true -> 
                    "Error: Network connection failed. Please check your internet connection and try again."
                e.message?.contains("HTTP", ignoreCase = true) == true -> 
                    "Error: Server error occurred. ${e.message}"
                else -> 
                    "Error: Failed to generate dialogue. ${e.message ?: "Unknown error occurred. Please try again."}"
            }
            addConversationMessage(characterId, ConversationMessage(
                text = errorMessage,
                isFromPlayer = false,
                timestamp = state.currentTime,
                type = ConversationMessageType.NORMAL
            ))
            return state
        }
        
        // Check for empty response
        if (dialogueResponse.isBlank()) {
            _dialogueLoading.value = _dialogueLoading.value + (characterId to false)
            addConversationMessage(characterId, ConversationMessage(
                text = "Error: Received empty response from server. Please try again.",
                isFromPlayer = false,
                timestamp = state.currentTime,
                type = ConversationMessageType.NORMAL
            ))
            return state
        }
        
        // Process the LLM 3 response
        val dialogueResult = LLMResponseProcessor.DialogueGenerator.process(dialogueResponse)
        
        val dialogueData = when (dialogueResult) {
            is LLMResponseProcessor.ProcessingResult.Success -> {
                dialogueResult.data
            }
            is LLMResponseProcessor.ProcessingResult.Failure -> {
                _dialogueLoading.value = _dialogueLoading.value + (characterId to false)
                // Add error message to conversation
                addConversationMessage(characterId, ConversationMessage(
                    text = "Error: Failed to process dialogue response. ${dialogueResult.error.message}",
                    isFromPlayer = false,
                    timestamp = state.currentTime,
                    type = ConversationMessageType.NORMAL
                ))
                return state
            }
        }
        
        // Clear loading state
        _dialogueLoading.value = _dialogueLoading.value + (characterId to false)
        
        // Advance time by questioning time cost (before adding character response)
        val timeAfterQuestion = state.currentTime.addMinutes(ActionTimeCosts.QUESTIONING_TIME)
        
        // Add character's response to conversation history
        addConversationMessage(characterId, ConversationMessage(
            text = dialogueData.dialogue,
            isFromPlayer = false,
            timestamp = timeAfterQuestion,
            type = ConversationMessageType.NORMAL
        ))
        
        // Update game state
        var newState = state
        
        // Validate and filter new clues if any were revealed
        val validNewClues = if (dialogueData.newClues != null && dialogueData.newClues.isNotEmpty()) {
            // Validate all clues
            val clueValidationErrors = LLMResponseProcessor.DialogueGenerator.validateClueIds(
                dialogueData,
                state,
                characterId
            )
            
            // Log validation errors for debugging (but don't block execution)
            if (clueValidationErrors.isNotEmpty()) {
                // Filter out invalid clues based on error types
                val invalidClueIds = clueValidationErrors.mapNotNull { error ->
                    when (error) {
                        is LLMResponseProcessor.DialogueGenerator.ValidationError.InvalidClueId -> error.clueId
                        is LLMResponseProcessor.DialogueGenerator.ValidationError.ClueNotInCharacterKnowledge -> error.clueId
                        is LLMResponseProcessor.DialogueGenerator.ValidationError.ClueNotUnlocked -> error.clueId
                        is LLMResponseProcessor.DialogueGenerator.ValidationError.ClueAlreadyCollected -> error.clueId
                        else -> null
                    }
                }.toSet()
                
                // Filter out invalid clues
                dialogueData.newClues.filter { clueId ->
                    !invalidClueIds.contains(clueId)
                }
            } else {
                // All clues are valid
                dialogueData.newClues
            }
        } else {
            emptyList()
        }
        
        // Add valid new clues to player's collected clues
        if (validNewClues.isNotEmpty()) {
            val updatedCollectedClues = (state.player.collectedClues + validNewClues).distinct()
            val updatedPlayer = state.player.copy(collectedClues = updatedCollectedClues)
            newState = newState.copy(player = updatedPlayer)
            
            // Detect newly collected clues (not in previous state)
            val newlyCollectedClues = validNewClues.filter { !previousCollectedClues.contains(it) }
            if (newlyCollectedClues.isNotEmpty()) {
                // Get clue names for display
                val clueNames = newlyCollectedClues.mapNotNull { clueId ->
                    state.getClue(clueId)?.name
                }
                
                val clueText = if (clueNames.size == 1) {
                    "You found 1 clue: ${clueNames.first()}"
                } else {
                    "You found ${clueNames.size} clues: ${clueNames.joinToString(", ")}"
                }
                
                // Add system message for clue collection
                addSystemMessage(characterId, clueText, timeAfterQuestion)
            }
        }
        
        // Update character's mental state if changed
        if (dialogueData.mentalStateUpdate != null && previousMentalState != dialogueData.mentalStateUpdate) {
            val updatedCharacters = newState.characters.map { char ->
                if (char.id == characterId) {
                    char.copy(mentalState = dialogueData.mentalStateUpdate)
                } else {
                    char
                }
            }
            newState = newState.copy(characters = updatedCharacters)
            
            // Add system message for mental state change
            addSystemMessage(
                characterId,
                "${character.name}'s mood changed to ${dialogueData.mentalStateUpdate}",
                timeAfterQuestion
            )
        }
        
        // Update time (already advanced above)
        newState = newState.copy(currentTime = timeAfterQuestion)
        
        return newState
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
    
    fun addConversationMessage(characterId: String, message: ConversationMessage) {
        val currentHistory = _conversationHistory.value
        val characterHistory = currentHistory[characterId] ?: emptyList()
        _conversationHistory.value = currentHistory + (characterId to (characterHistory + message))
    }
    
    /**
     * Adds a system message to the conversation history.
     * System messages are used to notify players about game state changes.
     */
    fun addSystemMessage(characterId: String, text: String, timestamp: GameTime) {
        addConversationMessage(
            characterId,
            ConversationMessage(
                text = text,
                isFromPlayer = false,
                timestamp = timestamp,
                type = ConversationMessageType.SYSTEM
            )
        )
    }
    
    fun getConversationHistory(characterId: String): List<ConversationMessage> {
        return _conversationHistory.value[characterId] ?: emptyList()
    }
}

