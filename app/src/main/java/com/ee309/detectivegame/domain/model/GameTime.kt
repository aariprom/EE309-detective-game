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
}

