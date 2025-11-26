package com.ee309.detectivegame.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the current phase of the game
 */
@Serializable
enum class GamePhase {
    START, // Initial state
    TUTORIAL, // Tutorial phase
    INTRODUCTION, // Background, event, characters introduction
    INVESTIGATION, // Main gameplay
    GAME_OVER, // Game ended
    WIN, // Player won
    LOSE // Player lost
}

