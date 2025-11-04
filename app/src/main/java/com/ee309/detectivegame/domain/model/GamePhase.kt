package com.ee309.detectivegame.domain.model

/**
 * Represents the current phase of the game
 */
enum class GamePhase {
    START, // Initial state
    TUTORIAL, // Tutorial phase
    INTRODUCTION, // Background, event, characters introduction
    INVESTIGATION, // Main gameplay
    GAME_OVER, // Game ended
    WIN, // Player won
    LOSE // Player lost
}

