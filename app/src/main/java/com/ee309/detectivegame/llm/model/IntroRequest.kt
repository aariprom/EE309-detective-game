package com.ee309.detectivegame.llm.model

import com.ee309.detectivegame.domain.model.Character
import com.ee309.detectivegame.domain.model.GameTime
import com.ee309.detectivegame.domain.model.Place
import kotlinx.serialization.Serializable

/**
 * Request data for LLM 2: Intro Generator
 * Contains only public information from GameState (no spoilers)
 */
@Serializable
data class IntroRequest(
    val title: String,
    val description: String,
    val characters: List<PublicCharacterInfo>,
    val places: List<PublicPlaceInfo>,
    val timeline: PublicTimelineInfo
)

/**
 * Public character information (no spoilers like isCriminal)
 */
@Serializable
data class PublicCharacterInfo(
    val name: String,
    val traits: List<String>,
    val currentLocation: String
)

/**
 * Public place information
 */
@Serializable
data class PublicPlaceInfo(
    val name: String,
    val description: String
)

/**
 * Public timeline information
 */
@Serializable
data class PublicTimelineInfo(
    val startTime: TimeInfo,
    val endTime: TimeInfo
)

/**
 * Time information for timeline
 */
@Serializable
data class TimeInfo(
    val minutes: Int
) {
    fun toGameTime(): com.ee309.detectivegame.domain.model.GameTime {
        return com.ee309.detectivegame.domain.model.GameTime(minutes)
    }
}

/**
 * Extension function to convert GameTime to TimeInfo
 */
fun com.ee309.detectivegame.domain.model.GameTime.toTimeInfo(): TimeInfo {
    return TimeInfo(minutes)
}

