package com.ee309.detectivegame.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the reason why the player lost the game.
 * Only relevant when phase is LOSE.
 */
@Serializable
enum class LossReason {
    TIMEOUT,           // Time limit exceeded
    FALSE_ACCUSATION  // Player accused the wrong person
}

