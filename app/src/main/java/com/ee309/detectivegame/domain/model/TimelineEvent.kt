package com.ee309.detectivegame.domain.model

/**
 * Represents an event in the timeline
 */
data class TimelineEvent(
    val id: String,
    val time: GameTime,
    val eventType: EventType,
    val characterId: String? = null,
    val placeId: String? = null,
    val description: String,
    val action: String? = null, // What action happens (e.g., "destroy_evidence")
    val affectedComponents: List<String> = emptyList() // IDs of affected components
) {
    enum class EventType {
        CHARACTER_ACTION, // Character does something
        PLACE_CHANGE, // Place state changes
        CLUE_AVAILABILITY, // Clue becomes available/unavailable
        EVIDENCE_DESTRUCTION, // Evidence is destroyed
        CHARACTER_MOVEMENT, // Character moves
        CUSTOM // Custom event
    }
}

