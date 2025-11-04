package com.ee309.detectivegame.presentation.state

import com.ee309.detectivegame.domain.model.GameState

/**
 * UI state for the game screen
 */
sealed class GameUiState {
    data object Loading : GameUiState()
    data class Success(val gameState: GameState) : GameUiState()
    data class Error(val message: String) : GameUiState()
}

