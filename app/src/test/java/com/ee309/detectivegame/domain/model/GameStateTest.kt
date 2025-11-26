package com.ee309.detectivegame.domain.model

import kotlinx.serialization.InternalSerializationApi
import org.junit.Test
import org.junit.Assert.*

class GameStateTest {
    
    // Helper methods to create test data
    private fun createTestCharacter(id: String, name: String, location: String = ""): Character {
        return Character(
            id = id,
            name = name,
            currentLocation = location
        )
    }
    
    @OptIn(InternalSerializationApi::class)
    private fun createTestPlace(id: String, name: String, connectedPlaces: List<String> = emptyList(), availableClues: List<String> = emptyList()): Place {
        return Place(
            id = id,
            name = name,
            connectedPlaces = connectedPlaces,
            availableClues = availableClues
        )
    }
    
    @OptIn(InternalSerializationApi::class)
    private fun createTestClue(id: String, content: String): Clue {
        return Clue(
            id = id,
            content = content
        )
    }
    
    // ========== Query Methods Tests ==========
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getCharacter returns character when found`() {
        val character = createTestCharacter("char1", "John")
        val gameState = GameState(characters = listOf(character))
        
        val result = gameState.getCharacter("char1")
        
        assertNotNull(result)
        assertEquals("John", result?.name)
        assertEquals("char1", result?.id)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getCharacter returns null when not found`() {
        val gameState = GameState()
        
        val result = gameState.getCharacter("nonexistent")
        
        assertNull(result)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getPlace returns place when found`() {
        val place = createTestPlace("place1", "Library")
        val gameState = GameState(places = listOf(place))
        
        val result = gameState.getPlace("place1")
        
        assertNotNull(result)
        assertEquals("Library", result?.name)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getPlace returns null when not found`() {
        val gameState = GameState()
        
        val result = gameState.getPlace("nonexistent")
        
        assertNull(result)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getClue returns clue when found`() {
        val clue = createTestClue("clue1", "Fingerprints on the door")
        val gameState = GameState(clues = listOf(clue))
        
        val result = gameState.getClue("clue1")
        
        assertNotNull(result)
        assertEquals("Fingerprints on the door", result?.content)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getClue returns null when not found`() {
        val gameState = GameState()
        
        val result = gameState.getClue("nonexistent")
        
        assertNull(result)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getCharactersAtLocation returns characters at location`() {
        val char1 = createTestCharacter("char1", "John", "place1")
        val char2 = createTestCharacter("char2", "Jane", "place1")
        val char3 = createTestCharacter("char3", "Bob", "place2")
        val gameState = GameState(characters = listOf(char1, char2, char3))
        
        val result = gameState.getCharactersAtLocation("place1")
        
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == "char1" })
        assertTrue(result.any { it.id == "char2" })
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getCharactersAtLocation returns empty list when no characters at location`() {
        val char1 = createTestCharacter("char1", "John", "place1")
        val gameState = GameState(characters = listOf(char1))
        
        val result = gameState.getCharactersAtLocation("place2")
        
        assertTrue(result.isEmpty())
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getAvailableCluesAtLocation returns unlocked clues at place`() {
        val clue1 = createTestClue("clue1", "Clue 1")
        val clue2 = createTestClue("clue2", "Clue 2")
        val clue3 = createTestClue("clue3", "Clue 3")
        val place = createTestPlace("place1", "Library", availableClues = listOf("clue1", "clue2", "clue3"))
        val gameState = GameState(
            places = listOf(place),
            clues = listOf(clue1, clue2, clue3),
            flags = emptyMap() // All clues unlocked (no unlock conditions)
        )
        
        val result = gameState.getAvailableCluesAtLocation("place1")
        
        assertEquals(3, result.size)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getAvailableCluesAtLocation filters locked clues`() {
        val clue1 = createTestClue("clue1", "Clue 1")
        val clue2 = createTestClue("clue2", "Clue 2")
        val clue3 = createTestClue("clue3", "Clue 3")
        clue3.copy(unlockConditions = listOf("flag1"))
        val place = createTestPlace("place1", "Library", availableClues = listOf("clue1", "clue2", "clue3"))
        val gameState = GameState(
            places = listOf(place),
            clues = listOf(
                clue1,
                clue2,
                clue3.copy(unlockConditions = listOf("flag1"))
            ),
            flags = emptyMap() // flag1 not set, so clue3 is locked
        )
        
        val result = gameState.getAvailableCluesAtLocation("place1")
        
        assertEquals(2, result.size) // Only clue1 and clue2
        assertTrue(result.all { it.id != "clue3" })
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getAvailableCluesAtLocation returns empty when place not found`() {
        val gameState = GameState()
        
        val result = gameState.getAvailableCluesAtLocation("nonexistent")
        
        assertTrue(result.isEmpty())
    }
    
    // ========== Update Methods Tests ==========
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `updateFlag adds new flag`() {
        val gameState = GameState()
        
        val updated = gameState.updateFlag("flag1", true)
        
        assertEquals(true, updated.flags["flag1"])
        assertTrue(updated.flags.containsKey("flag1"))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `updateFlag overwrites existing flag`() {
        val gameState = GameState(flags = mapOf("flag1" to false))
        
        val updated = gameState.updateFlag("flag1", true)
        
        assertEquals(true, updated.flags["flag1"])
        assertEquals(1, updated.flags.size)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `updateFlag maintains immutability`() {
        val gameState = GameState()
        val updated = gameState.updateFlag("flag1", true)
        
        // Original state unchanged
        assertTrue(gameState.flags.isEmpty())
        // New state has flag
        assertTrue(updated.flags.containsKey("flag1"))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `updateTime updates time correctly`() {
        val gameState = GameState(currentTime = GameTime(30))
        val newTime = GameTime(60)
        
        val updated = gameState.updateTime(newTime)
        
        assertEquals(60, updated.currentTime.minutes)
        assertEquals(30, gameState.currentTime.minutes) // Original unchanged
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `updatePlayer updates player correctly`() {
        val oldPlayer = Player(name = "Old Name", currentLocation = "place1")
        val newPlayer = Player(name = "New Name", currentLocation = "place2")
        val gameState = GameState(player = oldPlayer)
        
        val updated = gameState.updatePlayer(newPlayer)
        
        assertEquals("New Name", updated.player.name)
        assertEquals("place2", updated.player.currentLocation)
        assertEquals("Old Name", gameState.player.name) // Original unchanged
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `updatePhase updates phase correctly`() {
        val gameState = GameState(phase = GamePhase.START)
        
        val updated = gameState.updatePhase(GamePhase.INVESTIGATION)
        
        assertEquals(GamePhase.INVESTIGATION, updated.phase)
        assertEquals(GamePhase.START, gameState.phase) // Original unchanged
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `addCharacter adds character to state`() {
        val gameState = GameState()
        val character = createTestCharacter("char1", "John")
        
        val updated = gameState.addCharacter(character)
        
        assertEquals(1, updated.characters.size)
        assertEquals("John", updated.characters.first().name)
        assertTrue(gameState.characters.isEmpty()) // Original unchanged
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `updateCharacter updates existing character`() {
        val character = createTestCharacter("char1", "John", "place1")
        val gameState = GameState(characters = listOf(character))
        val updatedCharacter = character.copy(name = "Johnny", mentalState = "Suspicious")
        
        val updated = gameState.updateCharacter(updatedCharacter)
        
        assertEquals("Johnny", updated.characters.first().name)
        assertEquals("Suspicious", updated.characters.first().mentalState)
        assertEquals("John", gameState.characters.first().name) // Original unchanged
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `updateCharacter returns unchanged state when character not found`() {
        val character = createTestCharacter("char1", "John")
        val gameState = GameState(characters = listOf(character))
        val nonExistent = createTestCharacter("char2", "Jane")
        
        val updated = gameState.updateCharacter(nonExistent)
        
        // Should return unchanged state (same reference or equal state)
        assertEquals(1, updated.characters.size)
        assertEquals("John", updated.characters.first().name)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `removeCharacter removes character from state`() {
        val char1 = createTestCharacter("char1", "John")
        val char2 = createTestCharacter("char2", "Jane")
        val gameState = GameState(characters = listOf(char1, char2))
        
        val updated = gameState.removeCharacter("char1")
        
        assertEquals(1, updated.characters.size)
        assertEquals("char2", updated.characters.first().id)
        assertEquals(2, gameState.characters.size) // Original unchanged
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `addPlace adds place to state`() {
        val gameState = GameState()
        val place = createTestPlace("place1", "Library")
        
        val updated = gameState.addPlace(place)
        
        assertEquals(1, updated.places.size)
        assertEquals("Library", updated.places.first().name)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `updatePlace updates existing place`() {
        val place = createTestPlace("place1", "Library")
        val gameState = GameState(places = listOf(place))
        val updatedPlace = place.copy(name = "Grand Library")
        
        val updated = gameState.updatePlace(updatedPlace)
        
        assertEquals("Grand Library", updated.places.first().name)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `removePlace removes place from state`() {
        val place1 = createTestPlace("place1", "Library")
        val place2 = createTestPlace("place2", "Garden")
        val gameState = GameState(places = listOf(place1, place2))
        
        val updated = gameState.removePlace("place1")
        
        assertEquals(1, updated.places.size)
        assertEquals("place2", updated.places.first().id)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `addClue adds clue to state`() {
        val gameState = GameState()
        val clue = createTestClue("clue1", "Fingerprints")
        
        val updated = gameState.addClue(clue)
        
        assertEquals(1, updated.clues.size)
        assertEquals("Fingerprints", updated.clues.first().content)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `updateClue updates existing clue`() {
        val clue = createTestClue("clue1", "Old content")
        val gameState = GameState(clues = listOf(clue))
        val updatedClue = clue.copy(content = "New content")
        
        val updated = gameState.updateClue(updatedClue)
        
        assertEquals("New content", updated.clues.first().content)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `removeClue removes clue from state`() {
        val clue1 = createTestClue("clue1", "Clue 1")
        val clue2 = createTestClue("clue2", "Clue 2")
        val gameState = GameState(clues = listOf(clue1, clue2))
        
        val updated = gameState.removeClue("clue1")
        
        assertEquals(1, updated.clues.size)
        assertEquals("clue2", updated.clues.first().id)
    }
    
    // ========== Time Management Tests ==========
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `advanceTime advances time correctly`() {
        val gameState = GameState(currentTime = GameTime(30))
        
        val updated = gameState.advanceTime(15)
        
        assertEquals(45, updated.currentTime.minutes)
        assertEquals(30, gameState.currentTime.minutes) // Original unchanged
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `advanceTime caps at timeline endTime`() {
        val timeline = Timeline(GameTime(0), GameTime(60))
        val gameState = GameState(currentTime = GameTime(50), timeline = timeline)
        
        val updated = gameState.advanceTime(20) // Would go to 70, but capped at 60
        
        assertEquals(60, updated.currentTime.minutes)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getRemainingTime calculates correctly`() {
        val timeline = Timeline(GameTime(0), GameTime(480))
        val gameState = GameState(currentTime = GameTime(120), timeline = timeline)
        
        val remaining = gameState.getRemainingTime()
        
        assertEquals(360, remaining.minutes)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getRemainingTime returns zero when time limit exceeded`() {
        val timeline = Timeline(GameTime(0), GameTime(480))
        val gameState = GameState(currentTime = GameTime(500), timeline = timeline)
        
        val remaining = gameState.getRemainingTime()
        
        assertEquals(0, remaining.minutes)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getTimeProgress calculates correctly`() {
        val timeline = Timeline(GameTime(0), GameTime(480))
        val gameState = GameState(currentTime = GameTime(240), timeline = timeline)
        
        val progress = gameState.getTimeProgress()
        
        assertEquals(0.5, progress, 0.01) // 50% elapsed
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getTimeProgress returns 1_0 when time limit exceeded`() {
        val timeline = Timeline(GameTime(0), GameTime(480))
        val gameState = GameState(currentTime = GameTime(500), timeline = timeline)
        
        val progress = gameState.getTimeProgress()
        
        assertEquals(1.0, progress, 0.01)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `isTimeLimitExceeded returns true when exceeded`() {
        val timeline = Timeline(GameTime(0), GameTime(480))
        val gameState = GameState(currentTime = GameTime(500), timeline = timeline)
        
        assertTrue(gameState.isTimeLimitExceeded())
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `isTimeLimitExceeded returns false when not exceeded`() {
        val timeline = Timeline(GameTime(0), GameTime(480))
        val gameState = GameState(currentTime = GameTime(240), timeline = timeline)
        
        assertFalse(gameState.isTimeLimitExceeded())
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `advanceTimeWithEvents detects triggered events`() {
        val event = TimelineEvent(
            id = "event1",
            time = GameTime(45),
            eventType = TimelineEvent.EventType.CHARACTER_MOVEMENT,
            description = "Character moves",
            characterId = "char1",
            placeId = "place2"
        )
        val timeline = Timeline(GameTime(0), GameTime(480), events = listOf(event))
        val gameState = GameState(currentTime = GameTime(30), timeline = timeline)
        
        val result = gameState.advanceTimeWithEvents(20) // Advances to 50, event at 45 should trigger
        
        assertEquals(50, result.newState.currentTime.minutes)
        assertEquals(1, result.triggeredEvents.size)
        assertEquals("event1", result.triggeredEvents.first().id)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getTriggeredEventsBetween finds events in time range`() {
        val event1 = TimelineEvent(
            id = "event1",
            time = GameTime(30),
            eventType = TimelineEvent.EventType.CHARACTER_ACTION,
            description = "Event 1"
        )
        val event2 = TimelineEvent(
            id = "event2",
            time = GameTime(45),
            eventType = TimelineEvent.EventType.CHARACTER_ACTION,
            description = "Event 2"
        )
        val event3 = TimelineEvent(
            id = "event3",
            time = GameTime(60),
            eventType = TimelineEvent.EventType.CHARACTER_ACTION,
            description = "Event 3"
        )
        val timeline = Timeline(GameTime(0), GameTime(480), events = listOf(event1, event2, event3))
        val gameState = GameState(timeline = timeline)
        
        val events = gameState.getTriggeredEventsBetween(GameTime(25), GameTime(50))
        
        assertEquals(2, events.size) // event1 at 30, event2 at 45 (event3 at 60 is excluded)
        assertTrue(events.any { it.id == "event1" })
        assertTrue(events.any { it.id == "event2" })
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `performActionWithTime advances time with action cost`() {
        val gameState = GameState(currentTime = GameTime(30))
        
        val result = gameState.performActionWithTime(ActionTimeCosts.ActionType.INVESTIGATION)
        
        // INVESTIGATION_TIME = 15, so 30 + 15 = 45
        assertEquals(45, result.newState.currentTime.minutes)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `performMovementWithTime calculates distance-based time`() {
        val place1 = createTestPlace("place1", "Library", connectedPlaces = listOf("place2"))
        val place2 = createTestPlace("place2", "Garden", connectedPlaces = listOf("place1"))
        val gameState = GameState(
            places = listOf(place1, place2),
            currentTime = GameTime(30)
        )
        
        val result = gameState.performMovementWithTime(place1, place2)
        
        // Connected places have distance 1, so base (5) + (1 * 5) = 10
        // 30 + 10 = 40
        assertEquals(40, result.newState.currentTime.minutes)
    }
    
    // ========== Event Processing Tests ==========
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `processTimelineEvents processes character movement event`() {
        val character = createTestCharacter("char1", "John", "place1")
        val event = TimelineEvent(
            id = "event1",
            time = GameTime(30),
            eventType = TimelineEvent.EventType.CHARACTER_MOVEMENT,
            description = "Character moves",
            characterId = "char1",
            placeId = "place2"
        )
        val gameState = GameState(characters = listOf(character))
        
        val updated = gameState.processTimelineEvents(listOf(event))
        
        val updatedChar = updated.getCharacter("char1")
        assertNotNull(updatedChar)
        assertEquals("place2", updatedChar?.currentLocation)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `processTimelineEvents processes evidence destruction event`() {
        val clue1 = createTestClue("clue1", "Clue 1")
        val clue2 = createTestClue("clue2", "Clue 2")
        val gameState = GameState(clues = listOf(clue1, clue2))
        val event = TimelineEvent(
            id = "event1",
            time = GameTime(30),
            eventType = TimelineEvent.EventType.CLUE_DESTRUCTION,
            description = "Evidence destroyed",
            affectedComponents = listOf("clue1")
        )
        
        val updated = gameState.processTimelineEvents(listOf(event))
        
        assertEquals(1, updated.clues.size)
        assertNull(updated.getClue("clue1"))
        assertNotNull(updated.getClue("clue2"))
        assertTrue(updated.flags.containsKey("evidence_destroyed_event1"))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `processTimelineEvents processes multiple events`() {
        val character = createTestCharacter("char1", "John", "place1")
        val clue1 = createTestClue("clue1", "Clue 1")
        val gameState = GameState(
            characters = listOf(character),
            clues = listOf(clue1)
        )
        val event1 = TimelineEvent(
            id = "event1",
            time = GameTime(30),
            eventType = TimelineEvent.EventType.CHARACTER_MOVEMENT,
            description = "Character moves",
            characterId = "char1",
            placeId = "place2"
        )
        val event2 = TimelineEvent(
            id = "event2",
            time = GameTime(45),
            eventType = TimelineEvent.EventType.CLUE_DESTRUCTION,
            description = "Evidence destroyed",
            affectedComponents = listOf("clue1")
        )
        
        val updated = gameState.processTimelineEvents(listOf(event1, event2))
        
        // Both events processed
        assertEquals("place2", updated.getCharacter("char1")?.currentLocation)
        assertNull(updated.getClue("clue1"))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `advanceTimeAndProcessEvents combines time advancement and event processing`() {
        val character = createTestCharacter("char1", "John", "place1")
        val event = TimelineEvent(
            id = "event1",
            time = GameTime(45),
            eventType = TimelineEvent.EventType.CHARACTER_MOVEMENT,
            description = "Character moves",
            characterId = "char1",
            placeId = "place2"
        )
        val timeline = Timeline(GameTime(0), GameTime(480), events = listOf(event))
        val gameState = GameState(
            currentTime = GameTime(30),
            characters = listOf(character),
            timeline = timeline
        )
        
        val updated = gameState.advanceTimeAndProcessEvents(20) // Advances to 50, event at 45 triggers
        
        assertEquals(50, updated.currentTime.minutes)
        assertEquals("place2", updated.getCharacter("char1")?.currentLocation)
    }
    
    // ========== Game Logic Helper Tests ==========
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getUnlockedCharacters returns only unlocked characters`() {
        val char1 = createTestCharacter("char1", "John")
        val char2 = createTestCharacter("char2", "Jane")
        char2.copy(unlockConditions = listOf("flag1"))
        val gameState = GameState(
            characters = listOf(
                char1,
                char2.copy(unlockConditions = listOf("flag1"))
            ),
            flags = mapOf("flag1" to true)
        )
        
        val unlocked = gameState.getUnlockedCharacters()
        
        assertEquals(2, unlocked.size) // Both unlocked
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getUnlockedCharacters filters locked characters`() {
        val char1 = createTestCharacter("char1", "John")
        val char2 = createTestCharacter("char2", "Jane")
        char2.copy(unlockConditions = listOf("flag1"))
        val gameState = GameState(
            characters = listOf(
                char1,
                char2.copy(unlockConditions = listOf("flag1"))
            ),
            flags = emptyMap() // flag1 not set
        )
        
        val unlocked = gameState.getUnlockedCharacters()
        
        assertEquals(1, unlocked.size)
        assertEquals("char1", unlocked.first().id)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getAvailableCharacters filters hidden characters`() {
        val char1 = createTestCharacter("char1", "John")
        val char2 = createTestCharacter("char2", "Jane")
        char2.copy(hidden = true)
        val gameState = GameState(
            characters = listOf(
                char1,
                char2.copy(hidden = true)
            ),
            flags = emptyMap()
        )
        
        val available = gameState.getAvailableCharacters()
        
        assertEquals(1, available.size)
        assertEquals("char1", available.first().id)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getCurrentLocation returns player location`() {
        val place = createTestPlace("place1", "Library")
        val player = Player(currentLocation = "place1")
        val gameState = GameState(
            player = player,
            places = listOf(place)
        )
        
        val location = gameState.getCurrentLocation()
        
        assertNotNull(location)
        assertEquals("Library", location?.name)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getCurrentLocation returns null when player has no location`() {
        val gameState = GameState(player = Player(currentLocation = ""))
        
        val location = gameState.getCurrentLocation()
        
        assertNull(location)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getCharactersAtCurrentLocation returns characters at player location`() {
        val place = createTestPlace("place1", "Library")
        val player = Player(currentLocation = "place1")
        val char1 = createTestCharacter("char1", "John", "place1")
        val char2 = createTestCharacter("char2", "Jane", "place2")
        val gameState = GameState(
            player = player,
            places = listOf(place),
            characters = listOf(char1, char2)
        )
        
        val characters = gameState.getCharactersAtCurrentLocation()
        
        assertEquals(1, characters.size)
        assertEquals("char1", characters.first().id)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getNearbyPlaces returns connected places`() {
        val place1 = createTestPlace("place1", "Library", connectedPlaces = listOf("place2", "place3"))
        val place2 = createTestPlace("place2", "Garden", connectedPlaces = listOf("place1"))
        val place3 = createTestPlace("place3", "Kitchen", connectedPlaces = listOf("place1"))
        val player = Player(currentLocation = "place1")
        val gameState = GameState(
            player = player,
            places = listOf(place1, place2, place3)
        )
        
        val nearby = gameState.getNearbyPlaces()
        
        assertEquals(2, nearby.size) // place2 and place3
        assertTrue(nearby.any { it.id == "place2" })
        assertTrue(nearby.any { it.id == "place3" })
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `canProgressToPhase allows valid phase transitions`() {
        val gameState = GameState(phase = GamePhase.START)
        
        assertTrue(gameState.canProgressToPhase(GamePhase.TUTORIAL))
        assertFalse(gameState.canProgressToPhase(GamePhase.INVESTIGATION))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `canProgressToPhase prevents invalid phase transitions`() {
        val gameState = GameState(phase = GamePhase.START)
        
        assertFalse(gameState.canProgressToPhase(GamePhase.INVESTIGATION))
        assertFalse(gameState.canProgressToPhase(GamePhase.GAME_OVER))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `canProgressToPhase prevents progression from end states`() {
        val gameState = GameState(phase = GamePhase.WIN)
        
        assertFalse(gameState.canProgressToPhase(GamePhase.INVESTIGATION))
        assertFalse(gameState.canProgressToPhase(GamePhase.START))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `checkWinConditions returns LOSE when time limit exceeded`() {
        val timeline = Timeline(GameTime(0), GameTime(480))
        val gameState = GameState(currentTime = GameTime(500), timeline = timeline)
        
        val result = gameState.checkWinConditions()
        
        assertNotNull(result)
        assertEquals(GameState.GameResult.LOSE, result)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `checkWinConditions returns null when game in progress`() {
        val timeline = Timeline(GameTime(0), GameTime(480))
        val gameState = GameState(currentTime = GameTime(240), timeline = timeline)
        
        val result = gameState.checkWinConditions()
        
        assertNull(result) // Game still in progress
    }
}

