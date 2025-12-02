package com.ee309.detectivegame.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the game timeline with past and future events
 * 
 * All times are stored as absolute times (minutes from midnight).
 * 
 * @param baseTime The absolute time reference point (e.g., 960 = 16:00). This is the earliest point in the timeline.
 * @param startTime The absolute time when the game starts (e.g., 1080 = 18:00). Must be >= baseTime.
 * @param endTime The absolute time when the game ends (e.g., 1440 = 24:00). Must be > startTime.
 * @param events List of timeline events. Events are stored as absolute times.
 *               Crime events typically occur between baseTime and startTime.
 */
@Serializable
data class Timeline(
    val baseTime: GameTime,
    val startTime: GameTime,
    val endTime: GameTime,
    val events: List<TimelineEvent> = emptyList()
) {
    /**
     * Gets past events relative to the current game time.
     * currentTime is relative to startTime, so we convert to absolute time for comparison.
     * 
     * @param currentTime The current game time (relative to startTime, starts at 0).
     * @return List of events that occurred before or at the current absolute time.
     */
    fun getPastEvents(currentTime: GameTime): List<TimelineEvent> {
        val absoluteTime = startTime.minutes + currentTime.minutes
        return events.filter { it.time.minutes <= absoluteTime }
            .sortedBy { it.time.minutes }
    }
    
    /**
     * Gets future events relative to the current game time.
     * currentTime is relative to startTime, so we convert to absolute time for comparison.
     * 
     * @param currentTime The current game time (relative to startTime, starts at 0).
     * @return List of events that will occur after the current absolute time.
     */
    fun getFutureEvents(currentTime: GameTime): List<TimelineEvent> {
        val absoluteTime = startTime.minutes + currentTime.minutes
        return events.filter { it.time.minutes > absoluteTime }
            .sortedBy { it.time.minutes }
    }
    
    /**
     * Gets events at a specific absolute time.
     * 
     * @param time The absolute time to check.
     * @return List of events that occur exactly at the specified time.
     */
    fun getEventsAtTime(time: GameTime): List<TimelineEvent> {
        return events.filter { it.time.minutes == time.minutes }
    }
    
    /**
     * Gets crime events (events with eventType = CRIME).
     * These should occur between baseTime and startTime (all absolute times).
     * 
     * @return List of crime events.
     */
    fun getCrimeEvents(): List<TimelineEvent> {
        return events.filter { it.eventType == TimelineEvent.EventType.CRIME }
            .sortedBy { it.time.minutes }
    }
    
    fun getUpcomingEvents(currentTime: GameTime, limit: Int = 5): List<TimelineEvent> {
        return getFutureEvents(currentTime).take(limit)
    }
}

