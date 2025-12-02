package com.ee309.detectivegame.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents an event in the timeline
 */
@Serializable
data class TimelineEvent(
    val id: String,
    val time: GameTime,
    val eventType: EventType,
    val description: String,

    // id of character doing action/moving for CHARACTER_ACTION, CHARACTER_MOVEMENT
    val characterId: String?,

    // id of place for PLACE_CHANGE
    val placeId: String?,
) {
    @Serializable
    enum class EventType {
        PLACE_CHANGE, // Place state changes
        CHARACTER_MOVEMENT, // Character moves
        CRIME, // Crime event (occurs before game starts, between baseTime and startTime)
        CUSTOM // Custom event
    }
}

