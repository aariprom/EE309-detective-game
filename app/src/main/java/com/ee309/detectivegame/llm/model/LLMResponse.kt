package com.ee309.detectivegame.llm.model

import com.ee309.detectivegame.domain.model.Clue
import com.ee309.detectivegame.domain.model.GameState
import com.ee309.detectivegame.domain.model.Place
import com.ee309.detectivegame.domain.model.Player
import com.ee309.detectivegame.domain.model.Timeline
import com.ee309.detectivegame.domain.model.TimelineEvent
import com.ee309.detectivegame.domain.model.GamePhase
import com.ee309.detectivegame.domain.model.GameTime
import com.ee309.detectivegame.domain.model.Character
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class PlayerResponse(
    val currentLocation: String,
    val tools: List<String>
) {
    fun toPlayer(): Player {
        return Player(
            currentLocation = currentLocation,
            tools = tools,
        )
    }
}

@InternalSerializationApi
@Serializable
data class CharacterResponse(
    val id: String,
    val name: String,
    val traits: List<String>,
    val initialLocation: String,
    val isCriminal: Boolean,
    val unlockConditions: List<String>
) {
    fun toCharacter(): Character {
        return Character(
            id = id,
            name = name,
            traits = traits,
            currentLocation = initialLocation,
            isCriminal = isCriminal,
            unlockConditions = unlockConditions,
        )
    }
}

@InternalSerializationApi
@Serializable
data class PlaceResponse(
    val id: String,
    val name: String,
    val description: String,
    val availableClues: List<String>,
    val unlockConditions: List<String>,
    val connections: List<String>
) {
    fun toPlace(): Place {
        return Place(
            id = id,
            name = name,
            description = description,
            availableClues = availableClues,
            unlockConditions = unlockConditions,
            connectedPlaces = connections,
        )
    }
}

@InternalSerializationApi
@Serializable
data class ClueResponse(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val unlockConditions: List<String>
) {
    fun toClue(): Clue {
        return Clue(
            id = id,
            name = name,
            description = description,
            location = location,
            unlockConditions = unlockConditions,
        )
    }
}

@InternalSerializationApi
@Serializable
data class TimeResponse(
    val minutes: Int
) {
    fun toGameTime(): GameTime {
        return GameTime(minutes)
    }
}

@InternalSerializationApi
@Serializable
data class TimelineResponse(
    val baseTime: TimeResponse,
    val startTime: TimeResponse,
    val endTime: TimeResponse,
    val events: List<TimelineEventResponse>
) {
    fun toTimeline(): Timeline {
        return Timeline(
            baseTime = baseTime.toGameTime(),
            startTime = startTime.toGameTime(),
            endTime = endTime.toGameTime(),
            events = events.map { it.toTimelineEvent() }
        )
    }
}

@InternalSerializationApi
@Serializable
data class TimelineEventResponse(
    val id: String,
    val time: TimeResponse,
    val eventType: String,
    val description: String,
    val characterId: String? = null,
    val placeId: String? = null
) {
    fun toTimelineEvent(): TimelineEvent {
        return TimelineEvent(
            id = id,
            time = time.toGameTime(),
            eventType = TimelineEvent.EventType.valueOf(eventType),
            description = description,
            characterId = characterId,
            placeId = placeId
        )
    }
}

@InternalSerializationApi
@Serializable
data class FlagResponse(
    val id: String,
    val value: Boolean
)

@InternalSerializationApi
@Serializable
data class LLMResponse(
    val title: String,
    val description: String,
    val phase: String,
    val currentTime: Int = 0,
    val player: PlayerResponse,
    val characters: List<CharacterResponse>,
    val places: List<PlaceResponse>,
    val clues: List<ClueResponse>,
    val timeline: TimelineResponse,
    val flags: List<FlagResponse>
    ) {

    fun toGameState(): GameState {
        val flagMap = flags.associate { it.id to it.value }

        return GameState(
            title = title,
            description = description,
            phase = GamePhase.valueOf(phase),
            currentTime = GameTime(currentTime),
            player = player.toPlayer(),
            characters = characters.map { it.toCharacter() },
            places = places.map { it.toPlace() },
            clues = clues.map { it.toClue() },
            timeline = timeline.toTimeline(),
            flags = flagMap,
        )
    }
}