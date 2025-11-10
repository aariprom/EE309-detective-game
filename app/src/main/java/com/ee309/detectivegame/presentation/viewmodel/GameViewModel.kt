package com.ee309.detectivegame.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ee309.detectivegame.domain.model.GameState
import com.ee309.detectivegame.presentation.state.GameUiState
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
                // For now, create empty game state
                val initialState = GameState()
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
}

