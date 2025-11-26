package com.ee309.detectivegame.domain.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable


/**
 * Represents a location/place in the game
 */
@InternalSerializationApi
@Serializable
data class Place(
    val id: String,
    val name: String,
    val description: String,
    val availableClues: List<String> = emptyList(),
    val hidden: Boolean = false,
    val unlockConditions: List<String> = emptyList(),
    val items: List<String> = emptyList(),
    // todo: This must be initialized when game starts
    // LLM 1 does not set this; fill this by iterating on characters
    // refer to character.currentLocation
    val currentCharacters: List<String> = emptyList(),
    val connectedPlaces: List<String> = emptyList()
) {
    fun isUnlocked(flags: Map<String, Boolean>): Boolean {
        if (unlockConditions.isEmpty()) return true
        return unlockConditions.all { flags[it] == true }
    }
    
    fun getDistanceTo(other: Place): Int {
        // Simple distance calculation - can be enhanced
        return if (connectedPlaces.contains(other.id)) 1 else 2
    }
}

