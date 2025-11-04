package com.ee309.detectivegame.domain.model

/**
 * Represents a character in the game
 */
data class Character(
    val id: String,
    val name: String,
    val traits: List<String> = emptyList(),
    val isCriminal: Boolean = false,
    val knownClues: List<String> = emptyList(),
    val mentalState: String = "Normal",
    val hidden: Boolean = false,
    val unlockConditions: List<String> = emptyList(),
    val items: List<String> = emptyList(),
    val currentLocation: String = ""
) {
    fun isUnlocked(flags: Map<String, Boolean>): Boolean {
        if (unlockConditions.isEmpty()) return true
        return unlockConditions.all { flags[it] == true }
    }
    
    fun isAtLocation(locationId: String): Boolean {
        return currentLocation == locationId
    }
}

