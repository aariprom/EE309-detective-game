package com.ee309.detectivegame.domain.generator

import com.ee309.detectivegame.domain.model.*

/**
 * Mock game data generator for testing and development.
 * 
 * Creates a complete GameState with hardcoded test data for a simple detective scenario.
 * This can be replaced later with LLM-generated content.
 */
object MockGameData {
    
    /**
     * Creates an initial game state with mock data for a detective game scenario.
     * 
     * Scenario: Office building murder mystery
     * - Setting: CEO office murder
     * - Characters: 4 characters (1 victim, 1 criminal, 2 witnesses)
     * - Places: 3 locations (office, lobby, parking lot)
     * - Clues: 5 clues scattered across locations
     * - Timeline: 3 events over 8 hours
     * 
     * @return Complete GameState ready for gameplay
     */
    fun createInitialGameState(): GameState {
        // Create characters
        val characters = createMockCharacters()
        
        // Create places
        val places = createMockPlaces()
        
        // Create clues
        val clues = createMockClues()
        
        // Create timeline
        val timeline = createMockTimeline()
        
        // Create player
        val player = Player(
            name = "Detective",
            currentLocation = "place_lobby", // Start in lobby
            collectedClues = emptyList(),
            tools = listOf("Notebook", "Flashlight")
        )
        
        // Create initial game state
        return GameState(
            phase = GamePhase.START,
            currentTime = GameTime(0), // Start at 00:00
            player = player,
            characters = characters,
            places = places,
            clues = clues,
            timeline = timeline,
            flags = mapOf(
                "game_started" to true,
                "introduction_complete" to true
            )
        )
    }
    
    /**
     * Creates mock characters for the game.
     */
    private fun createMockCharacters(): List<Character> {
        return listOf(
            // Character 1: The Criminal
            Character(
                id = "char_alice",
                name = "Alice Johnson",
                traits = listOf("Suspicious", "Nervous", "Secretive"),
                isCriminal = true,
                knownClues = listOf("clue_1", "clue_2", "clue_5"),
                mentalState = "Anxious",
                hidden = false,
                unlockConditions = emptyList(),
                items = listOf("Key Card"),
                currentLocation = "place_lobby"
            ),
            
            // Character 2: Witness
            Character(
                id = "char_bob",
                name = "Bob Williams",
                traits = listOf("Helpful", "Observant", "Honest"),
                isCriminal = false,
                knownClues = listOf("clue_3"),
                mentalState = "Cooperative",
                hidden = false,
                unlockConditions = emptyList(),
                items = emptyList(),
                currentLocation = "place_office"
            ),
            
            // Character 3: Security Guard
            Character(
                id = "char_charlie",
                name = "Charlie Brown",
                traits = listOf("Professional", "Alert", "Diligent"),
                isCriminal = false,
                knownClues = listOf("clue_4"),
                mentalState = "Normal",
                hidden = false,
                unlockConditions = emptyList(),
                items = listOf("Security Badge"),
                currentLocation = "place_lobby"
            ),
            
            // Character 4: Victim (appears in timeline/backstory)
            Character(
                id = "char_victim",
                name = "John Smith",
                traits = listOf("Victim", "CEO", "Wealthy"),
                isCriminal = false,
                knownClues = emptyList(),
                mentalState = "Deceased",
                hidden = true, // Hidden as victim is dead
                unlockConditions = listOf("found_clue_1"), // Unlocks when crime scene is investigated
                items = emptyList(),
                currentLocation = "place_office"
            )
        )
    }
    
    /**
     * Creates mock places for the game.
     */
    private fun createMockPlaces(): List<Place> {
        return listOf(
            // Place 1: Crime Scene
            Place(
                id = "place_office",
                name = "CEO Office",
                traits = listOf("Crime Scene", "Locked", "Private"),
                availableClues = listOf("clue_1", "clue_2", "clue_5"),
                hidden = false,
                unlockConditions = emptyList(), // Accessible from start
                items = listOf("Bloodstain", "Broken Window", "Desk"),
                currentCharacters = listOf("char_bob"), // Bob is at the office
                connectedPlaces = listOf("place_lobby")
            ),
            
            // Place 2: Lobby (Starting location)
            Place(
                id = "place_lobby",
                name = "Building Lobby",
                traits = listOf("Public", "Busy", "Well-lit"),
                availableClues = listOf("clue_3", "clue_4"),
                hidden = false,
                unlockConditions = emptyList(),
                items = listOf("Reception Desk", "Security Camera"),
                currentCharacters = listOf("char_alice", "char_charlie"), // Alice and Charlie are in lobby
                connectedPlaces = listOf("place_office", "place_parking")
            ),
            
            // Place 3: Parking Lot
            Place(
                id = "place_parking",
                name = "Parking Lot",
                traits = listOf("Outdoor", "Dark", "Isolated"),
                availableClues = listOf("clue_5"),
                hidden = false,
                unlockConditions = emptyList(),
                items = listOf("Cars", "Street Lights"),
                currentCharacters = emptyList(),
                connectedPlaces = listOf("place_lobby")
            )
        )
    }
    
    /**
     * Creates mock clues for the game.
     */
    private fun createMockClues(): List<Clue> {
        return listOf(
            // Clue 1: Bloodstain at crime scene
            Clue(
                id = "clue_1",
                who = "char_alice",
                whom = "char_victim",
                time = null, // Murder happened in the past, before investigation
                place = "place_office",
                content = "Fresh bloodstain on the floor near the desk. The blood appears to be recent.",
                unlockConditions = emptyList() // Available immediately
            ),
            
            // Clue 2: Broken window
            Clue(
                id = "clue_2",
                who = null,
                whom = null,
                time = null, // Happened in the past
                place = "place_office",
                content = "The window is broken from the inside. Glass fragments are scattered on the floor.",
                unlockConditions = emptyList()
            ),
            
            // Clue 3: Security footage
            Clue(
                id = "clue_3",
                who = "char_alice",
                whom = null,
                time = null, // Happened in the past
                place = "place_lobby",
                content = "Security camera footage shows Alice Johnson entering the building just before the incident.",
                unlockConditions = emptyList()
            ),
            
            // Clue 4: Security guard testimony
            Clue(
                id = "clue_4",
                who = "char_charlie",
                whom = null,
                time = null, // Happened in the past
                place = "place_lobby",
                content = "Security guard Charlie Brown reports seeing Alice Johnson looking nervous and leaving in a hurry.",
                unlockConditions = listOf("talked_to_charlie") // Requires talking to Charlie first
            ),
            
            // Clue 5: Fingerprints (locked clue)
            Clue(
                id = "clue_5",
                who = "char_alice",
                whom = "char_victim",
                time = null, // Happened in the past
                place = "place_office",
                content = "Fingerprints on the desk match Alice Johnson. The prints are fresh and clearly visible.",
                unlockConditions = listOf("found_clue_1", "investigated_office") // Requires finding clue 1 and investigating office
            )
        )
    }
    
    /**
     * Creates mock timeline with events.
     * 
     * Note: The murder happened in the past (before game start).
     * Timeline events represent things that will happen during the investigation.
     */
    private fun createMockTimeline(): Timeline {
        return Timeline(
            startTime = GameTime(0), // 00:00 (start of investigation)
            endTime = GameTime(480), // 08:00 (8 hours later - end of investigation)
            events = listOf(
                // Event 1: Evidence destruction attempt (happens during investigation)
                TimelineEvent(
                    id = "event_evidence_destruction",
                    time = GameTime(120), // 02:00 (2 hours after investigation starts)
                    eventType = TimelineEvent.EventType.EVIDENCE_DESTRUCTION,
                    characterId = "char_alice",
                    placeId = "place_office",
                    description = "Alice attempts to destroy evidence in the office.",
                    action = "destroy_evidence",
                    affectedComponents = listOf("clue_2") // Broken window clue might be affected
                ),
                
                // Event 2: Character movement (happens during investigation)
                TimelineEvent(
                    id = "event_alice_movement",
                    time = GameTime(180), // 03:00 (3 hours after investigation starts)
                    eventType = TimelineEvent.EventType.CHARACTER_MOVEMENT,
                    characterId = "char_alice",
                    placeId = "place_lobby",
                    description = "Alice moves from the office to the lobby.",
                    action = "move",
                    affectedComponents = listOf("char_alice")
                ),
                
                // Event 3: New clue becomes available (happens during investigation)
                TimelineEvent(
                    id = "event_clue_available",
                    time = GameTime(240), // 04:00 (4 hours after investigation starts)
                    eventType = TimelineEvent.EventType.CLUE_AVAILABILITY,
                    characterId = null,
                    placeId = "place_parking",
                    description = "New evidence is discovered in the parking lot.",
                    action = "reveal_clue",
                    affectedComponents = listOf("clue_5")
                )
            )
        )
    }
}

