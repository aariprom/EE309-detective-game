package com.ee309.detectivegame.domain.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Represents a clue in the game
 */
@InternalSerializationApi
@Serializable
data class Clue(
    val id: String,
    val name: String = "Default Clue Name",
    val description: String = "Default Clue Description",
    // unique ID of which this clue can be found by INVESTIGATION
    val location: String = "",
    val unlockConditions: List<String> = emptyList()
) {
    fun isUnlocked(flags: Map<String, Boolean>): Boolean {
        if (unlockConditions.isEmpty()) return true
        return unlockConditions.all { flags[it] == true }
    }
}

