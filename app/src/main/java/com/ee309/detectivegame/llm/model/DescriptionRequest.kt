package com.ee309.detectivegame.llm.model

import com.ee309.detectivegame.domain.model.Clue
import com.ee309.detectivegame.domain.model.GameState
import com.ee309.detectivegame.domain.model.Place
import com.ee309.detectivegame.domain.model.TimelineEvent
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class DescriptionRequest(
    val place: PlaceInfo,
    val charactersHere: List<CharacterBrief>,
    val player: PlayerInfo,
    val timelineEvents: List<EventBrief>,
    val language: String = "en"
)

@Serializable
data class PlaceInfo(
    val id: String,
    val name: String,
    val description: String,
    val connections: List<String>,
    val availableClues: List<String>
)

@Serializable
data class CharacterBrief(
    val name: String,
    val roleOrTrait: String
)

@Serializable
data class PlayerInfo(
    val collectedClues: List<String>,
    val currentTimeMinutes: Int
)

@Serializable
data class EventBrief(
    val timeMinutes: Int,
    val description: String
)

@Serializable
data class DescriptionResponse(
    val text: String
)

@OptIn(InternalSerializationApi::class)
fun GameState.toDescriptionRequest(language: String = "en"): DescriptionRequest? {
    val lang = if (language.isBlank()) "en" else language
    val place: Place = getCurrentLocation() ?: return null
    val availableClueNames: List<String> = place.availableClues.mapNotNull { clueId ->
        getClue(clueId)
    }.map(Clue::name)

    val visibleCharacters = getCharactersAtLocation(place.id)
        .filter { !it.hidden }
        .map { CharacterBrief(name = it.name, roleOrTrait = it.traits.firstOrNull() ?: "Unknown role") }

    val placeEvents = timeline.events
        .filter { it.placeId == place.id }
        .sortedBy { it.time.minutes }
        .takeLast(5)
        .map { EventBrief(timeMinutes = it.time.minutes, description = it.description) }

    return DescriptionRequest(
        place = PlaceInfo(
            id = place.id,
            name = place.name,
            description = place.description,
            connections = place.connectedPlaces,
            availableClues = availableClueNames
        ),
        charactersHere = visibleCharacters,
        player = PlayerInfo(
            collectedClues = player.collectedClues.mapNotNull { getClue(it)?.name },
            currentTimeMinutes = timeline.startTime.minutes + currentTime.minutes
        ),
        timelineEvents = placeEvents,
        language = lang
    )
}
