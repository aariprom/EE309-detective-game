package com.ee309.detectivegame.domain.model

/**
 * Complete game state
 */
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
    fun getCharacter(id: String): Character? {
        return characters.find { it.id == id }
    }
    
    fun getPlace(id: String): Place? {
        return places.find { it.id == id }
    }
    
    fun getClue(id: String): Clue? {
        return clues.find { it.id == id }
    }
    
    fun getCharactersAtLocation(locationId: String): List<Character> {
        return characters.filter { it.isAtLocation(locationId) }
    }
    
    fun getAvailableCluesAtLocation(locationId: String): List<Clue> {
        val place = getPlace(locationId) ?: return emptyList()
        return place.availableClues
            .mapNotNull { clueId -> getClue(clueId) }
            .filter { it.isUnlocked(flags) }
    }
    
    fun updateFlag(key: String, value: Boolean): GameState {
        return copy(flags = flags + (key to value))
    }
    
    fun updateTime(newTime: GameTime): GameState {
        return copy(currentTime = newTime)
    }
    
    fun updatePlayer(newPlayer: Player): GameState {
        return copy(player = newPlayer)
    }
}

