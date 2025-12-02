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
    private fun createTestPlace(
        id: String,
        name: String,
        description: String = "Test place",
        connectedPlaces: List<String> = emptyList(),
        availableClues: List<String> = emptyList()
    ): Place {
        return Place(
            id = id,
            name = name,
            description = description,
            connectedPlaces = connectedPlaces,
            availableClues = availableClues
        )
    }

    @OptIn(InternalSerializationApi::class)
    private fun createTestClue(
        id: String,
        name: String,
        description: String,
        unlockConditions: List<String> = emptyList()
    ): Clue {
        return Clue(
            id = id,
            name = name,
            description = description,
            unlockConditions = unlockConditions
        )
    }

    // ========== Query Methods Tests ==========

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `getCharacter returns character when found`() {
        val character = createTestCharacter("char1", "John")
        val gameState = GameState(characters = listOf(character))

        val result = gameState.getCharacter("char1")

        assertNotNull(result)
        assertEquals("John", result?.name)
        assertEquals("char1", result?.id)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `getCharacter returns null when not found`() {
        val gameState = GameState()

        val result = gameState.getCharacter("nonexistent")

        assertNull(result)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `getPlace returns place when found`() {
        val place = createTestPlace("place1", "Library")
        val gameState = GameState(places = listOf(place))

        val result = gameState.getPlace("place1")

        assertNotNull(result)
        assertEquals("Library", result?.name)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `getPlace returns null when not found`() {
        val gameState = GameState()

        val result = gameState.getPlace("nonexistent")

        assertNull(result)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `getClue returns clue when found`() {
        val clue = createTestClue("clue1", "Fingerprints", "Fingerprints on the door")
        val gameState = GameState(clues = listOf(clue))

        val result = gameState.getClue("clue1")

        assertNotNull(result)
        assertEquals("Fingerprints on the door", result?.description)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `getClue returns null when not found`() {
        val gameState = GameState()

        val result = gameState.getClue("nonexistent")

        assertNull(result)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
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

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `getCharactersAtLocation returns empty list when no characters at location`() {
        val char1 = createTestCharacter("char1", "John", "place1")
        val gameState = GameState(characters = listOf(char1))

        val result = gameState.getCharactersAtLocation("place2")

        assertTrue(result.isEmpty())
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `getAvailableCluesAtLocation returns unlocked clues at place`() {
        val clue1 = createTestClue("clue1", "Clue 1", "...")
        val clue2 = createTestClue("clue2", "Clue 2", "...")
        val clue3 = createTestClue("clue3", "Clue 3", "...")
        val place =
            createTestPlace("place1", "Library", availableClues = listOf("clue1", "clue2", "clue3"))
        val gameState = GameState(
            places = listOf(place),
            clues = listOf(clue1, clue2, clue3),
            flags = emptyMap() // All clues unlocked (no unlock conditions)
        )

        val result = gameState.getAvailableCluesAtLocation("place1")

        assertEquals(3, result.size)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `getAvailableCluesAtLocation filters locked clues`() {
        val clue1 = createTestClue("clue1", "Clue 1", "...")
        val clue2 = createTestClue("clue2", "Clue 2", "...")
        val clue3 = createTestClue("clue3", "Clue 3", "...", unlockConditions = listOf("flag1"))
        val place =
            createTestPlace("place1", "Library", availableClues = listOf("clue1", "clue2", "clue3"))
        val gameState = GameState(
            places = listOf(place),
            clues = listOf(clue1, clue2, clue3),
            flags = emptyMap() // flag1 not set, so clue3 is locked
        )

        val result = gameState.getAvailableCluesAtLocation("place1")

        assertEquals(2, result.size) // Only clue1 and clue2
        assertTrue(result.all { it.id != "clue3" })
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `getAvailableCluesAtLocation returns empty when place not found`() {
        val gameState = GameState()

        val result = gameState.getAvailableCluesAtLocation("nonexistent")

        assertTrue(result.isEmpty())
    }

    // ========== Update Methods Tests ==========

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `updateFlag adds new flag`() {
        val gameState = GameState()

        val updated = gameState.updateFlag("flag1", true)

        assertEquals(true, updated.flags["flag1"])
        assertTrue(updated.flags.containsKey("flag1"))
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `updateFlag overwrites existing flag`() {
        val gameState = GameState(flags = mapOf("flag1" to false))

        val updated = gameState.updateFlag("flag1", true)

        assertEquals(true, updated.flags["flag1"])
        assertEquals(1, updated.flags.size)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `updateFlag maintains immutability`() {
        val gameState = GameState()
        gameState.updateFlag("flag1", true)

        // Original state unchanged
        assertTrue(gameState.flags.isEmpty())
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `updateTime updates time correctly`() {
        val gameState = GameState(currentTime = GameTime(30))
        val newTime = GameTime(60)

        val updated = gameState.updateTime(newTime)

        assertEquals(60, updated.currentTime.minutes)
        assertEquals(30, gameState.currentTime.minutes) // Original unchanged
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `updatePlayer updates player correctly`() {
        val oldPlayer = Player(name = "Old Name", currentLocation = "place1")
        val newPlayer = Player(name = "New Name", currentLocation = "place2")
        val gameState = GameState(player = oldPlayer)

        val updated = gameState.updatePlayer(newPlayer)

        assertEquals("New Name", updated.player.name)
        assertEquals("place2", updated.player.currentLocation)
        assertEquals("Old Name", gameState.player.name) // Original unchanged
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `updatePhase updates phase correctly`() {
        val gameState = GameState(phase = GamePhase.START)

        val updated = gameState.updatePhase(GamePhase.INVESTIGATION)

        assertEquals(GamePhase.INVESTIGATION, updated.phase)
        assertEquals(GamePhase.START, gameState.phase) // Original unchanged
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `addCharacter adds character to state`() {
        val gameState = GameState()
        val character = createTestCharacter("char1", "John")

        val updated = gameState.addCharacter(character)

        assertEquals(1, updated.characters.size)
        assertEquals("John", updated.characters.first().name)
        assertTrue(gameState.characters.isEmpty()) // Original unchanged
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `updateCharacter updates existing character`() {
        val character = createTestCharacter("char1", "John", "place1")
        val gameState = GameState(characters = listOf(character))
        val updatedCharacter = character.copy(name = "Johnny", mentalState = "Suspicious")

        val updated = gameState.updateCharacter(updatedCharacter)

        assertEquals("Johnny", updated.characters.first().name)
        assertEquals("Suspicious", updated.characters.first().mentalState)
        assertEquals("John", gameState.characters.first().name) // Original unchanged
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `updateCharacter returns unchanged state when character not found`() {
        val character = createTestCharacter("char1", "John")
        val gameState = GameState(characters = listOf(character))
        val nonExistent = createTestCharacter("char2", "Jane")

        val updated = gameState.updateCharacter(nonExistent)

        // Should return unchanged state (same reference or equal state)
        assertEquals(1, updated.characters.size)
        assertEquals("John", updated.characters.first().name)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `removeCharacter removes character from state`() {
        val char1 = createTestCharacter("char1", "John")
        val char2 = createTestCharacter("char2", "Jane")
        val gameState = GameState(characters = listOf(char1, char2))

        val updated = gameState.removeCharacter("char1")

        assertEquals(1, updated.characters.size)
        assertEquals("char2", updated.characters.first().id)
        assertEquals(2, gameState.characters.size) // Original unchanged
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `addPlace adds place to state`() {
        val gameState = GameState()
        val place = createTestPlace("place1", "Library")

        val updated = gameState.addPlace(place)

        assertEquals(1, updated.places.size)
        assertEquals("Library", updated.places.first().name)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `updatePlace updates existing place`() {
        val place = createTestPlace("place1", "Library")
        val gameState = GameState(places = listOf(place))
        val updatedPlace = place.copy(name = "Grand Library")

        val updated = gameState.updatePlace(updatedPlace)

        assertEquals("Grand Library", updated.places.first().name)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `removePlace removes place from state`() {
        val place1 = createTestPlace("place1", "Library")
        val place2 = createTestPlace("place2", "Garden")
        val gameState = GameState(places = listOf(place1, place2))

        val updated = gameState.removePlace("place1")

        assertEquals(1, updated.places.size)
        assertEquals("place2", updated.places.first().id)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `addClue adds clue to state`() {
        val gameState = GameState()
        val clue = createTestClue("clue1", "Fingerprints", "A clue")

        val updated = gameState.addClue(clue)

        assertEquals(1, updated.clues.size)
        assertEquals("Fingerprints", updated.clues.first().name)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `updateClue updates existing clue`() {
        val clue = createTestClue("clue1", "Old Name", "Old description")
        val gameState = GameState(clues = listOf(clue))
        val updatedClue = clue.copy(description = "New description")

        val updated = gameState.updateClue(updatedClue)

        assertEquals("New description", updated.clues.first().description)
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `removeClue removes clue from state`() {
        val clue1 = createTestClue("clue1", "Clue 1", "...")
        val clue2 = createTestClue("clue2", "Clue 2", "...")
        val gameState = GameState(clues = listOf(clue1, clue2))

        val updated = gameState.removeClue("clue1")

        assertEquals(1, updated.clues.size)
        assertEquals("clue2", updated.clues.first().id)
    }

    // ========== Time Management Tests ==========

    @Test
    @OptIn(InternalSerializationApi::class)
    fun `advanceTime advances time correctly`() {
        val gameState = GameState(currentTime = GameTime(30))

        val updated = gameState.advanceTime(15)

        assertEquals(45, updated.currentTime.minutes)
        assertEquals(30, gameState.currentTime.minutes) // Original unchanged
    }
}
