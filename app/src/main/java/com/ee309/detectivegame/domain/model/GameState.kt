package com.ee309.detectivegame.domain.model

import kotlinx.serialization.Serializable

/**
 * Complete game state
 * This class is used to store the complete game state.
 * These methods will be called from the GameViewModel to get the current game state.
 */
@Serializable
data class GameState(
    val phase: GamePhase = GamePhase.START,
    val currentTime: GameTime = GameTime(0),
    val player: Player = Player(),
    val characters: List<Character> = emptyList(),
    val places: List<Place> = emptyList(),
    val clues: List<Clue> = emptyList(),
    val timeline: Timeline = Timeline(GameTime(0), GameTime(480)), // 8 hours default
    val flags: Map<String, Boolean> = emptyMap()
) {
    /**
     * Retrieves a character by its ID.
     * 
     * @param id The unique identifier of the character to find.
     * @return The Character if found, null otherwise.
     */
    fun getCharacter(id: String): Character? {
        return characters.find { it.id == id }
    }
    
    /**
     * Retrieves a place by its ID.
     * 
     * @param id The unique identifier of the place to find.
     * @return The Place if found, null otherwise.
     */
    fun getPlace(id: String): Place? {
        return places.find { it.id == id }
    }
    
    /**
     * Retrieves a clue by its ID.
     * 
     * @param id The unique identifier of the clue to find.
     * @return The Clue if found, null otherwise.
     */
    fun getClue(id: String): Clue? {
        return clues.find { it.id == id }
    }
    
    /**
     * Gets all characters currently at a specific location.
     * 
     * @param locationId The ID of the location to check.
     * @return A list of characters at the specified location. Returns empty list if no characters are found.
     */
    fun getCharactersAtLocation(locationId: String): List<Character> {
        return characters.filter { it.isAtLocation(locationId) }
    }
    
    /**
     * Gets all available clues at a specific location that are unlocked.
     * This method checks if the place exists, then filters clues based on:
     * 1. Clues that are listed in the place's availableClues
     * 2. Clues that are unlocked based on current game flags
     * 
     * @param locationId The ID of the location to check.
     * @return A list of unlocked clues available at the location. Returns empty list if place doesn't exist or no clues are available.
     */
    fun getAvailableCluesAtLocation(locationId: String): List<Clue> {
        val place = getPlace(locationId) ?: return emptyList()
        return place.availableClues
            .mapNotNull { clueId -> getClue(clueId) }
            .filter { it.isUnlocked(flags) }
    }
    
    /**
     * Updates or adds a game flag with a boolean value.
     * Game flags are used to track game state conditions (e.g., "has_investigated_room", "has_spoken_to_suspect").
     * 
     * @param key The flag identifier/key.
     * @param value The boolean value to set for the flag.
     * @return A new GameState instance with the updated flag. If the flag already exists, it will be overwritten.
     */
    fun updateFlag(key: String, value: Boolean): GameState {
        return copy(flags = flags + (key to value))
    }
    
    /**
     * Updates the current game time.
     * 
     * @param newTime The new GameTime value to set.
     * @return A new GameState instance with the updated time.
     */
    fun updateTime(newTime: GameTime): GameState {
        return copy(currentTime = newTime)
    }
    
    /**
     * Updates the player state.
     * 
     * @param newPlayer The updated Player object containing new player state (location, collected clues, tools, etc.).
     * @return A new GameState instance with the updated player.
     */
    fun updatePlayer(newPlayer: Player): GameState {
        return copy(player = newPlayer)
    }
    
    /**
     * Updates the current game phase.
     * Game phases represent the stage of the game (START, TUTORIAL, INTRODUCTION, INVESTIGATION, GAME_OVER, WIN, LOSE).
     * 
     * @param newPhase The new GamePhase to transition to.
     * @return A new GameState instance with the updated phase.
     */
    fun updatePhase(newPhase: GamePhase): GameState {
        return copy(phase = newPhase)
    }
    
    /**
     * Adds a new character to the game state.
     * 
     * @param character The Character object to add.
     * @return A new GameState instance with the character added to the characters list.
     */
    fun addCharacter(character: Character): GameState {
        return copy(characters = characters + character)
    }
    
    /**
     * Updates an existing character in the game state.
     * The character is identified by matching the ID of the updatedCharacter parameter.
     * 
     * @param updatedCharacter The Character object with updated properties. Must have the same ID as the character to update.
     * @return A new GameState instance with the character updated. Returns unchanged state if character with matching ID is not found.
     */
    fun updateCharacter(updatedCharacter: Character): GameState {
        val index = characters.indexOfFirst { it.id == updatedCharacter.id }
        return if (index >= 0) {
            copy(characters = characters.toMutableList().apply { set(index, updatedCharacter) })
        } else {
            this // Character not found, return unchanged
        }
    }
    
    /**
     * Removes a character from the game state.
     * 
     * @param characterId The unique identifier of the character to remove.
     * @return A new GameState instance with the character removed from the characters list. If character doesn't exist, returns unchanged state.
     */
    fun removeCharacter(characterId: String): GameState {
        return copy(characters = characters.filter { it.id != characterId })
    }
    
    /**
     * Adds a new place to the game state.
     * 
     * @param place The Place object to add.
     * @return A new GameState instance with the place added to the places list.
     */
    fun addPlace(place: Place): GameState {
        return copy(places = places + place)
    }
    
    /**
     * Updates an existing place in the game state.
     * The place is identified by matching the ID of the updatedPlace parameter.
     * 
     * @param updatedPlace The Place object with updated properties. Must have the same ID as the place to update.
     * @return A new GameState instance with the place updated. Returns unchanged state if place with matching ID is not found.
     */
    fun updatePlace(updatedPlace: Place): GameState {
        val index = places.indexOfFirst { it.id == updatedPlace.id }
        return if (index >= 0) {
            copy(places = places.toMutableList().apply { set(index, updatedPlace) })
        } else {
            this // Place not found, return unchanged
        }
    }
    
    /**
     * Removes a place from the game state.
     * 
     * @param placeId The unique identifier of the place to remove.
     * @return A new GameState instance with the place removed from the places list. If place doesn't exist, returns unchanged state.
     */
    fun removePlace(placeId: String): GameState {
        return copy(places = places.filter { it.id != placeId })
    }
    
    /**
     * Adds a new clue to the game state.
     * 
     * @param clue The Clue object to add.
     * @return A new GameState instance with the clue added to the clues list.
     */
    fun addClue(clue: Clue): GameState {
        return copy(clues = clues + clue)
    }
    
    /**
     * Updates an existing clue in the game state.
     * The clue is identified by matching the ID of the updatedClue parameter.
     * 
     * @param updatedClue The Clue object with updated properties. Must have the same ID as the clue to update.
     * @return A new GameState instance with the clue updated. Returns unchanged state if clue with matching ID is not found.
     */
    fun updateClue(updatedClue: Clue): GameState {
        val index = clues.indexOfFirst { it.id == updatedClue.id }
        return if (index >= 0) {
            copy(clues = clues.toMutableList().apply { set(index, updatedClue) })
        } else {
            this // Clue not found, return unchanged
        }
    }
    
    /**
     * Removes a clue from the game state.
     * 
     * @param clueId The unique identifier of the clue to remove.
     * @return A new GameState instance with the clue removed from the clues list. If clue doesn't exist, returns unchanged state.
     */
    fun removeClue(clueId: String): GameState {
        return copy(clues = clues.filter { it.id != clueId })
    }
    
    /**
     * Updates the game timeline.
     * The timeline contains all scheduled events and the start/end times of the game.
     * 
     * @param newTimeline The updated Timeline object.
     * @return A new GameState instance with the updated timeline.
     */
    fun updateTimeline(newTimeline: Timeline): GameState {
        return copy(timeline = newTimeline)
    }
}

