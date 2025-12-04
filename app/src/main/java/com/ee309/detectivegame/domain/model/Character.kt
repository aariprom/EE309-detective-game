package com.ee309.detectivegame.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a character in the game
 */
@Serializable
data class Character(
    val id: String,
    val name: String,
    val traits: List<String> = emptyList(),
    val isCriminal: Boolean = false,
    val isVictim: Boolean = false,
    val knownClues: List<String> = emptyList(),
    val mentalState: String = "Normal",
    val hidden: Boolean = false,
    val unlockConditions: List<String> = emptyList(),
    val items: List<String> = emptyList(),
    val currentLocation: String = "",
    val alibi: String = "" // Pre-game alibi text (time ranges and actions up to game start)
) {
    // NOTE: how about we manage list? hash table? of unlocked places?
    fun isUnlocked(flags: Map<String, Boolean>): Boolean {
        if (unlockConditions.isEmpty()) return true
        return unlockConditions.all { flags[it] == true }
    }
    
    fun isAtLocation(locationId: String): Boolean {
        return currentLocation == locationId
    }
}

