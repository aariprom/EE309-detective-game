package com.ee309.detectivegame.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the game timeline with past and future events
 */
@Serializable
data class Timeline(
    val startTime: GameTime,
    val endTime: GameTime,
    val events: List<TimelineEvent> = emptyList()
) {
    fun getPastEvents(currentTime: GameTime): List<TimelineEvent> {
        return events.filter { it.time.isBefore(currentTime) || it.time.minutes == currentTime.minutes }
            .sortedBy { it.time.minutes }
    }
    
    fun getFutureEvents(currentTime: GameTime): List<TimelineEvent> {
        return events.filter { it.time.isAfter(currentTime) }
            .sortedBy { it.time.minutes }
    }
    
    fun getEventsAtTime(time: GameTime): List<TimelineEvent> {
        return events.filter { it.time.minutes == time.minutes }
    }
    
    fun getUpcomingEvents(currentTime: GameTime, limit: Int = 5): List<TimelineEvent> {
        return getFutureEvents(currentTime).take(limit)
    }
}

