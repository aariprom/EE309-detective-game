package com.ee309.detectivegame.domain.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Complete game state
 * This class is used to store the complete game state.
 * These methods will be called from the GameViewModel to get the current game state.
 */
@OptIn(InternalSerializationApi::class)
@Serializable
data class GameState(
    val title: String = "Detective Scenario",
    val description: String = "A short, descriptive title of the detective case.",
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
    
    // Game logic helper methods
    
    /**
     * Gets all characters that are unlocked based on current game flags.
     * Characters are unlocked when all their unlockConditions are satisfied (flags are true).
     * 
     * @return A list of characters that are currently unlocked. Returns all characters if no unlock conditions exist.
     */
    fun getUnlockedCharacters(): List<Character> {
        return characters.filter { it.isUnlocked(flags) }
    }
    
    /**
     * Gets all places that are unlocked based on current game flags.
     * Places are unlocked when all their unlockConditions are satisfied (flags are true).
     * 
     * @return A list of places that are currently unlocked. Returns all places if no unlock conditions exist.
     */
    fun getUnlockedPlaces(): List<Place> {
        return places.filter { it.isUnlocked(flags) }
    }
    
    /**
     * Gets all characters that are available (visible and unlocked).
     * A character is available if it's not hidden and all its unlock conditions are met.
     * 
     * @return A list of characters that are visible and unlocked. Returns empty list if no characters are available.
     */
    fun getAvailableCharacters(): List<Character> {
        return getUnlockedCharacters().filter { !it.hidden }
    }
    
    /**
     * Checks if the time limit has been exceeded.
     * The time limit is defined by the timeline's endTime.
     * 
     * @return true if currentTime is after or equal to timeline endTime, false otherwise.
     */
    fun isTimeLimitExceeded(): Boolean {
        return currentTime.minutes >= timeline.endTime.minutes
    }
    
    /**
     * Gets the player's current location as a Place object.
     * 
     * @return The Place object for the player's current location, or null if location is not set or doesn't exist.
     */
    fun getCurrentLocation(): Place? {
        return if (player.currentLocation.isNotEmpty()) {
            getPlace(player.currentLocation)
        } else {
            null
        }
    }
    
    /**
     * Gets all characters at the player's current location.
     * Convenience method that combines getCurrentLocation() and getCharactersAtLocation().
     * 
     * @return A list of characters at the player's current location. Returns empty list if player has no location or no characters are present.
     */
    fun getCharactersAtCurrentLocation(): List<Character> {
        val currentLocation = getCurrentLocation() ?: return emptyList()
        return getCharactersAtLocation(currentLocation.id)
    }
    
    /**
     * Gets all places connected to the player's current location.
     * 
     * @return A list of Place objects that are connected to the current location. Returns empty list if player has no location or no connected places exist.
     */
    fun getNearbyPlaces(): List<Place> {
        val currentLocation = getCurrentLocation() ?: return emptyList()
        return currentLocation.connectedPlaces
            .mapNotNull { placeId -> getPlace(placeId) }
            .filter { it.isUnlocked(flags) }
    }
    
    /**
     * Gets all timeline events that occur at the current game time.
     * 
     * @return A list of TimelineEvent objects scheduled for the current time. Returns empty list if no events occur at this time.
     */
    fun getTimelineEventsAtCurrentTime(): List<TimelineEvent> {
        return timeline.getEventsAtTime(currentTime)
    }
    
    /**
     * Checks if the game can progress to a specific phase.
     * This is a basic validation method. More complex validation logic can be added later.
     * 
     * @param newPhase The phase to check progression to.
     * @return true if the phase transition is valid, false otherwise. Basic implementation allows progression forward through phases.
     */
    fun canProgressToPhase(newPhase: GamePhase): Boolean {
        // Basic phase progression validation
        // Allow progression: START -> TUTORIAL -> INTRODUCTION -> INVESTIGATION -> GAME_OVER/WIN/LOSE
        return when {
            phase == GamePhase.START -> newPhase == GamePhase.TUTORIAL
            phase == GamePhase.TUTORIAL -> newPhase == GamePhase.INTRODUCTION
            phase == GamePhase.INTRODUCTION -> newPhase == GamePhase.INVESTIGATION
            phase == GamePhase.INVESTIGATION -> newPhase in listOf(GamePhase.GAME_OVER, GamePhase.WIN, GamePhase.LOSE)
            phase in listOf(GamePhase.GAME_OVER, GamePhase.WIN, GamePhase.LOSE) -> false // Cannot progress from end states
            else -> false
        }
    }
    
    /**
     * Checks win/lose conditions for the game.
     * This is a basic implementation. More complex logic can be added based on game rules.
     * 
     * @return A GameResult indicating the game status. Returns null if game is still in progress.
     */
    fun checkWinConditions(): GameResult? {
        // Check if time limit exceeded
        if (isTimeLimitExceeded()) {
            return GameResult.LOSE // Time ran out
        }
        
        // Check if player has accused someone (if accusation system is implemented)
        // This would typically check if player has made an accusation and if it's correct
        
        // Check if player has collected enough clues (if clue-based win condition exists)
        // This would check if player has collected all required clues
        
        // For now, return null indicating game is still in progress
        // Win/lose conditions should be checked when player makes an accusation
        return null
    }
    
    // Time management methods
    
    /**
     * Advances the game time by a specified amount and returns updated state.
     * This method creates a new GameState with the updated time.
     * 
     * Note: This method does not automatically process timeline events.
     * Use [advanceTimeWithEvents] to automatically detect and return triggered events,
     * or use [getTriggeredEventsBetween] after advancing time to check for events.
     * 
     * @param minutes The number of minutes to advance the game time.
     * @return A new GameState instance with the time advanced. The time cannot exceed the timeline endTime.
     */
    fun advanceTime(minutes: Int): GameState {
        val newTime = currentTime.addMinutes(minutes)
        // Ensure time doesn't exceed timeline end time
        val finalTime = if (newTime.minutes > timeline.endTime.minutes) {
            timeline.endTime
        } else {
            newTime
        }
        return copy(currentTime = finalTime)
    }
    
    /**
     * Advances the game time and detects all timeline events that are triggered during the time advancement.
     * 
     * @param minutes The number of minutes to advance the game time.
     * @return A [TimeAdvanceResult] containing the updated GameState and a list of triggered TimelineEvents.
     */
    fun advanceTimeWithEvents(minutes: Int): TimeAdvanceResult {
        val oldTime = currentTime
        val newState = advanceTime(minutes)
        val triggeredEvents = newState.getTriggeredEventsBetween(oldTime, newState.currentTime)
        return TimeAdvanceResult(newState, triggeredEvents)
    }
    
    /**
     * Gets all timeline events that occur between two time points.
     * Events that occur exactly at the start time are included, but events at the end time are excluded.
     * This allows checking for events that would trigger when advancing from oldTime to newTime.
     * 
     * @param oldTime The starting time point.
     * @param newTime The ending time point (exclusive).
     * @return A list of TimelineEvents that occur between oldTime (inclusive) and newTime (exclusive).
     */
    fun getTriggeredEventsBetween(oldTime: GameTime, newTime: GameTime): List<TimelineEvent> {
        return timeline.events.filter { event ->
            val eventMinutes = event.time.minutes
            // Include events that occur at or after oldTime, but before newTime
            eventMinutes >= oldTime.minutes && eventMinutes < newTime.minutes
        }.sortedBy { it.time.minutes }
    }
    
    /**
     * Gets all timeline events that would be triggered when advancing time by a specified amount.
     * This is a convenience method that calculates the new time and finds events between current and new time.
     * 
     * @param minutes The number of minutes to advance.
     * @return A list of TimelineEvents that would trigger during the time advancement.
     */
    fun getEventsForTimeAdvance(minutes: Int): List<TimelineEvent> {
        val newTime = currentTime.addMinutes(minutes)
        val finalTime = if (newTime.minutes > timeline.endTime.minutes) {
            timeline.endTime
        } else {
            newTime
        }
        return getTriggeredEventsBetween(currentTime, finalTime)
    }
    
    /**
     * Result of advancing time, containing the updated state and triggered events.
     * 
     * @param newState The GameState with time advanced.
     * @param triggeredEvents List of TimelineEvents that were triggered during the time advancement.
     */
    data class TimeAdvanceResult(
        val newState: GameState,
        val triggeredEvents: List<TimelineEvent>
    )
    
    /**
     * Performs an action and automatically advances time based on the action's time cost.
     * This is a convenience method that combines action time calculation with time advancement.
     * 
     * @param actionType The type of action being performed.
     * @return A [TimeAdvanceResult] containing the updated GameState and triggered events.
     */
    fun performActionWithTime(actionType: ActionTimeCosts.ActionType): TimeAdvanceResult {
        val timeCost = ActionTimeCosts.getActionTime(actionType)
        return advanceTimeWithEvents(timeCost)
    }
    
    /**
     * Performs a movement action and automatically advances time based on distance.
     * 
     * @param from The starting place.
     * @param to The destination place.
     * @return A [TimeAdvanceResult] containing the updated GameState and triggered events.
     */
    fun performMovementWithTime(from: Place, to: Place): TimeAdvanceResult {
        val timeCost = ActionTimeCosts.getMovementTime(from, to)
        return advanceTimeWithEvents(timeCost)
    }
    
    /**
     * Performs a movement action and automatically advances time based on distance.
     * 
     * @param distance The distance between places (in distance units).
     * @return A [TimeAdvanceResult] containing the updated GameState and triggered events.
     */
    fun performMovementWithTime(distance: Int): TimeAdvanceResult {
        val timeCost = ActionTimeCosts.getMovementTime(distance)
        return advanceTimeWithEvents(timeCost)
    }
    
    /**
     * Processes a list of timeline events and applies their effects to the game state.
     * This is a basic implementation that handles common event types.
     * Complex event processing may require LLM 5 (Component Updater) for dynamic updates.
     * 
     * @param events The list of TimelineEvents to process.
     * @return A new GameState with event effects applied.
     */
    fun processTimelineEvents(events: List<TimelineEvent>): GameState {
        var updatedState = this
        
        events.forEach { event ->
            updatedState = updatedState.processSingleEvent(event)
        }
        
        return updatedState
    }
    
    /**
     * Processes a single timeline event and applies its effects to the game state.
     * 
     * @param event The TimelineEvent to process.
     * @return A new GameState with the event's effects applied.
     */
    private fun processSingleEvent(event: TimelineEvent): GameState {
        return when (event.eventType) {
            TimelineEvent.EventType.CHARACTER_MOVEMENT -> {
                processCharacterMovement(event)
            }
            TimelineEvent.EventType.PLACE_CHANGE -> {
                processPlaceChange(event)
            }
            TimelineEvent.EventType.CUSTOM -> {
                processCustomEvent(event)
            }
        }
    }
    
    /**
     * Processes a character movement event.
     * Moves the character to the new location specified in the event.
     * 
     * @param event The character movement event.
     * @return Updated GameState with character moved.
     */
    private fun processCharacterMovement(event: TimelineEvent): GameState {
        val characterId = event.characterId ?: return this
        val newLocation = event.placeId ?: return this
        
        val character = getCharacter(characterId) ?: return this
        val updatedCharacter = character.copy(currentLocation = newLocation)
        
        return updateCharacter(updatedCharacter)
    }
    
    /**
     * Processes a place change event.
     * Sets flags to indicate place state has changed.
     * 
     * @param event The place change event.
     * @return Updated GameState with place change flags set.
     */
    private fun processPlaceChange(event: TimelineEvent): GameState {
        // Set flag for place change
        val flagKey = "place_changed_${event.placeId}_${event.id}"
        return updateFlag(flagKey, true)
    }
    
    /**
     * Processes a custom event.
     * Sets generic flags for custom events.
     * 
     * @param event The custom event.
     * @return Updated GameState with custom event flags set.
     */
    private fun processCustomEvent(event: TimelineEvent): GameState {
        // Set flag for custom event
        val flagKey = "custom_event_${event.id}"
        return updateFlag(flagKey, true)
    }
    
    /**
     * Advances time and automatically processes all triggered timeline events.
     * This is a convenience method that combines time advancement with event processing.
     * 
     * @param minutes The number of minutes to advance the game time.
     * @return A new GameState with time advanced and all triggered events processed.
     */
    fun advanceTimeAndProcessEvents(minutes: Int): GameState {
        val result = advanceTimeWithEvents(minutes)
        return result.newState.processTimelineEvents(result.triggeredEvents)
    }
    
    /**
     * Gets the remaining time until the game ends.
     * 
     * @return A GameTime object representing the remaining time. Returns GameTime(0) if time limit has been exceeded.
     */
    fun getRemainingTime(): GameTime {
        val remaining = timeline.endTime.minutes - currentTime.minutes
        return GameTime(maxOf(0, remaining))
    }
    
    /**
     * Gets the percentage of time that has elapsed.
     * 
     * @return A value between 0.0 and 1.0 representing the percentage of time elapsed. Returns 1.0 if time limit exceeded.
     */
    fun getTimeProgress(): Double {
        val totalDuration = timeline.endTime.minutes - timeline.startTime.minutes
        if (totalDuration <= 0) return 1.0
        val elapsed = currentTime.minutes - timeline.startTime.minutes
        return (elapsed.toDouble() / totalDuration).coerceIn(0.0, 1.0)
    }
    
    /**
     * Represents the result of checking game win/lose conditions.
     */
    enum class GameResult {
        WIN,  // Player won
        LOSE  // Player lost
    }
}

