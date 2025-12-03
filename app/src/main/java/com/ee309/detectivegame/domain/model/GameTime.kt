package com.ee309.detectivegame.domain.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Represents game time with 5-minute minimum unit
 */
@Serializable
@OptIn(InternalSerializationApi::class)
data class GameTime(
    val minutes: Int = 0
) {
    val hours: Int get() = minutes / 60
    val minutesOfHour: Int get() = minutes % 60
    
    fun addMinutes(amount: Int): GameTime {
        return GameTime(minutes + amount)
    }
    
    fun isAfter(other: GameTime): Boolean {
        return minutes > other.minutes
    }
    
    fun isBefore(other: GameTime): Boolean {
        return minutes < other.minutes
    }
    
    fun format(): String {
        return String.format("%02d:%02d", hours, minutesOfHour)
    }
    
    /**
     * Formats time with a start time.
     * Used to display game time as a realistic time of day.
     * 
     * @param startTime The absolute time when the game starts (e.g., 1080 = 18:00).
     * @return Formatted time string as HH:MM representing startTime + this.
     * 
     * Example:
     * - startTime = 18:00 (1080 minutes)
     * - this = 30 minutes (relative to startTime)
     * - Result: (1080 + 30) = 1110 minutes = 18:30
     */
    fun formatWithStart(startTime: GameTime): String {
        val absoluteMinutes = startTime.minutes + this.minutes
        val absoluteTime = GameTime(absoluteMinutes)
        return absoluteTime.format()
    }
}

