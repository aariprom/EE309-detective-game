package com.ee309.detectivegame.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ee309.detectivegame.domain.model.GameState
import com.ee309.detectivegame.presentation.state.GameUiState
import com.ee309.detectivegame.domain.generator.MockGameData.createInitialGameState
import com.ee309.detectivegame.domain.model.GamePhase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
    
    init {
        // TODO: Initialize game state
        _uiState.value = GameUiState.Loading
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
    
    fun performAction(action: String) {
        viewModelScope.launch {
            // TODO: Handle game actions
        }
    }

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
}

