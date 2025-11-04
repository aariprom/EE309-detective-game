package com.ee309.detectivegame.domain.model

/**
 * Represents game time with 5-minute minimum unit
 */
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
}

