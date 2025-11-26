package com.ee309.detectivegame.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the player character
 */
@Serializable
data class Player(
    val name: String = "Detective",
    val currentLocation: String = "",
    val collectedClues: List<String> = emptyList(),
    val tools: List<String> = emptyList(),
    val flags: Map<String, Boolean> = emptyMap()
) {
    fun hasClue(clueId: String): Boolean {
        return collectedClues.contains(clueId)
    }
    
    fun hasTool(tool: String): Boolean {
        return tools.contains(tool)
    }
    
    fun addClue(clueId: String): Player {
        return copy(collectedClues = collectedClues + clueId)
    }
    
    fun addTool(tool: String): Player {
        return copy(tools = tools + tool)
    }
}

