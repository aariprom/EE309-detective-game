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

class GameViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
    
    init {
        // GameUIState Error with empty message indicates initial state
        // TODO: Make a distinct UI state for very first initialization
        _uiState.value = GameUiState.Error("")
    }
    
    fun startNewGame(keywords: String) {
        viewModelScope.launch {
            _uiState.value = GameUiState.Loading
            try {
                // TODO: Call LLM 1 to generate initial game content
                // For now, we use mocking game data for testing
                // This can be possibly used as TUTORIAL as well
                val initialState = createInitialGameState()
                _gameState.value = initialState
                _uiState.value = GameUiState.Success(initialState)
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error(e.message ?: "Unknown error")
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
        if (state.currentTime.isAfter(state.timeline.endTime)) {
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
}

