package com.ee309.detectivegame.domain.model

/**
 * Represents a game action that the player can perform.
 * Sealed class ensures type safety and exhaustive when expressions.
 */
sealed class GameAction {
    /**
     * Investigate a place to find clues and descriptions.
     * @param placeId The ID of the place to investigate
     */
    data class Investigate(val placeId: String) : GameAction()
    
    /**
     * Question/interrogate a character to get information.
     * @param characterId The ID of the character to question
     * @param question Optional question text (for future LLM integration)
     */
    data class Question(val characterId: String, val question: String? = null) : GameAction()
    
    /**
     * Move to a different location.
     * @param placeId The ID of the place to move to
     */
    data class Move(val placeId: String) : GameAction()
    
    /**
     * Accuse a character of being the criminal.
     * @param characterId The ID of the character being accused
     * @param evidence Optional list of clue IDs as evidence (for future validation)
     */
    data class Accuse(val characterId: String, val evidence: List<String> = emptyList()) : GameAction()
}