package com.ee309.detectivegame.domain.generator

import com.ee309.detectivegame.domain.model.*;
import kotlinx.serialization.InternalSerializationApi

object MockGameData {
    @OptIn(InternalSerializationApi::class)
    fun createInitialGameState(): GameState {
        val characters = createMockCharacters()
        val places = createMockPlaces()
        val clues = createMockClues()
        val timeline = createMockTimeline()

        val player = Player(
            currentLocation = "place_lobby",
            collectedClues = emptyList()
        )

        return GameState(
            title = "Mock Game Title",
            description = "Mock Game Description",
            phase = GamePhase.START,
            currentTime = GameTime(0),
            player = player,
            characters = characters,
            places = places,
            clues = clues,
            timeline = timeline,
            flags = mapOf("game_started" to true)
        )
    }

    private fun createMockCharacters(): List<Character> {
        return listOf(
            Character(
                id = "char_alice",
                name = "Alice",
                traits = listOf("Suspicious", "Nervous"),
                isCriminal = true,
                currentLocation = "place_lobby",
                unlockConditions = emptyList()
            ),
            Character(
                id = "char_bob",
                name = "Bob",
                traits = listOf("Calm", "Cooperative"),
                currentLocation = "place_office",
                unlockConditions = emptyList()
            ),
            Character(
                id = "char_charlie",
                name = "Charlie",
                traits = listOf("Helpful", "Observant"),
                currentLocation = "place_lobby",
                unlockConditions = emptyList()
            ),
            Character(
                id = "char_victim",
                name = "Victim",
                traits = emptyList(),
                hidden = true,
                currentLocation = "place_office",
                unlockConditions = listOf("found_clue_1")
            )
        )
    }

    @OptIn(InternalSerializationApi::class)
    private fun createMockPlaces(): List<Place> {
        return listOf(
            Place(
                id = "place_office",
                name = "CEO Office",
                description = "The crime scene",
                availableClues = listOf("clue_1", "clue_2", "clue_5"),
                unlockConditions = emptyList(),
                connectedPlaces = listOf("place_lobby")
            ),
            Place(
                id = "place_lobby",
                name = "Building Lobby",
                description = "The starting location",
                availableClues = listOf("clue_3", "clue_4"),
                unlockConditions = emptyList(),
                connectedPlaces = listOf("place_office", "place_parking")
            ),
            Place(
                id = "place_parking",
                name = "Parking Lot",
                description = "An outdoor area",
                unlockConditions = emptyList(),
                connectedPlaces = listOf("place_lobby")
            )
        )
    }

    @OptIn(InternalSerializationApi::class)
    private fun createMockClues(): List<Clue> {
        return listOf(
            Clue(
                id = "clue_1",
                name = "Bloodstain",
                description = "A suspicious bloodstain on the carpet.",
                location = "place_office",
                unlockConditions = emptyList()
            ),
            Clue(
                id = "clue_2",
                name = "Broken Window",
                description = "A window in the office has been shattered.",
                location = "place_office",
                unlockConditions = emptyList()
            ),
            Clue(
                id = "clue_3",
                name = "Security Footage",
                description = "The lobby security camera footage.",
                location = "place_lobby",
                unlockConditions = emptyList()
            ),
            Clue(
                id = "clue_4",
                name = "Security Guard Testimony",
                description = "The security guard's account of the events.",
                location = "char_charlie",
                unlockConditions = listOf("talked_to_charlie")
            ),
            Clue(
                id = "clue_5",
                name = "Fingerprints",
                description = "Unidentified fingerprints on the victim's desk.",
                location = "place_office",
                unlockConditions = listOf("found_clue_1", "investigated_office")
            )
        )
    }

    private fun createMockTimeline(): Timeline {
        return Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = listOf(
                TimelineEvent(
                    id = "event_alice_moves_to_office",
                    time = GameTime(120),
                    eventType = TimelineEvent.EventType.CHARACTER_MOVEMENT,
                    description = "Alice moves to the CEO Office.",
                    characterId = "char_alice",
                    placeId = "place_office"
                ),
                TimelineEvent(
                    id = "event_power_outage",
                    time = GameTime(180),
                    eventType = TimelineEvent.EventType.PLACE_CHANGE,
                    description = "The power goes out in the office.",
                    characterId = null,
                    placeId = "place_office"
                ),
                TimelineEvent(
                    id = "event_custom_scream",
                    time = GameTime(240),
                    eventType = TimelineEvent.EventType.CUSTOM,
                    description = "A scream is heard from the parking lot.",
                    characterId = null,
                    placeId = "place_parking"
                )
            )
        )
    }
}
