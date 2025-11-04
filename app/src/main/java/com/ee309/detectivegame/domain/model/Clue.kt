package com.ee309.detectivegame.domain.model

/**
 * Represents a clue in the game
 */
data class Clue(
    val id: String,
    val who: String? = null, // Who is involved
    val whom: String? = null, // Who is affected
    val time: GameTime? = null, // When it happened
    val place: String? = null, // Where it happened
    val content: String, // What happened / description
    val unlockConditions: List<String> = emptyList()
) {
    fun isUnlocked(flags: Map<String, Boolean>): Boolean {
        if (unlockConditions.isEmpty()) return true
        return unlockConditions.all { flags[it] == true }
    }
}

