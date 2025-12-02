package com.ee309.detectivegame.llm.config

import com.ee309.detectivegame.domain.model.GamePhase
import com.ee309.detectivegame.domain.model.GameState
import com.ee309.detectivegame.llm.model.LLMResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json

/**
 * Processor for LLM responses with JSON parsing, validation, and error handling.
 * Follows the pattern of other LLM config files (LLMPrompt, LLMSchema, LLMTask).
 */
object LLMResponseProcessor {
    
    private val json = Json {
        ignoreUnknownKeys = true
    }
    
    /**
     * Sealed class representing validation errors
     */
    sealed class ValidationError(open val message: String) {
        data class EmptyResponse(override val message: String = "LLM returned empty response") : ValidationError(message)
        data class JsonParseError(override val message: String) : ValidationError(message)
        data class NoCharacters(override val message: String = "No characters found in game data") : ValidationError(message)
        data class NoPlaces(override val message: String = "No places found in game data") : ValidationError(message)
        data class InvalidPhase(override val message: String) : ValidationError(message)
        data class InvalidCharacterLocation(
            val characterId: String,
            val locationId: String,
            override val message: String = "Character '$characterId' has invalid location '$locationId'"
        ) : ValidationError(message)
        data class InvalidPlayerLocation(
            val locationId: String,
            override val message: String = "Player has invalid starting location '$locationId'"
        ) : ValidationError(message)
        data class NoCriminal(override val message: String = "No criminal found in game data") : ValidationError(message)
        data class MultipleCriminals(override val message: String = "Multiple criminals found in game data") : ValidationError(message)
        data class InvalidTimeline(
            override val message: String = "Timeline is invalid: startTime must be before endTime"
        ) : ValidationError(message)
        data class InvalidClueReference(
            val clueId: String,
            val locationId: String,
            override val message: String = "Clue '$clueId' references invalid location '$locationId'"
        ) : ValidationError(message)
    }
    
    /**
     * Result of processing an LLM response
     */
    sealed class ProcessingResult {
        data class Success(val gameState: GameState) : ProcessingResult()
        data class Failure(val error: ValidationError) : ProcessingResult()
    }
    
    /**
     * Parses the raw LLM response string into an LLMResponse object.
     * 
     * @param rawResponse The raw JSON string from the LLM
     * @return Result containing LLMResponse on success, or ValidationError on failure
     */
    @OptIn(InternalSerializationApi::class)
    fun parseGameInitializerResponse(rawResponse: String): Result<LLMResponse> {
        // Check for empty response
        if (rawResponse.isBlank()) {
            return Result.failure(Exception(ValidationError.EmptyResponse().message))
        }
        
        // Parse JSON
        return try {
            val response = json.decodeFromString<LLMResponse>(rawResponse)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(ValidationError.JsonParseError("JSON parsing failed: ${e.message}").message))
        }
    }
    
    /**
     * Validates a GameState to ensure it meets all requirements.
     * 
     * @param gameState The GameState to validate
     * @return List of validation errors (empty if valid)
     */
    @OptIn(InternalSerializationApi::class)
    fun validateGameState(gameState: GameState): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        
        // Check for non-empty characters
        if (gameState.characters.isEmpty()) {
            errors.add(ValidationError.NoCharacters())
        }
        
        // Check for non-empty places
        if (gameState.places.isEmpty()) {
            errors.add(ValidationError.NoPlaces())
        }
        
        // If we don't have places, skip location validation
        if (gameState.places.isNotEmpty()) {
            val placeIds = gameState.places.map { it.id }.toSet()
            
            // Validate character locations
            gameState.characters.forEach { character ->
                if (!placeIds.contains(character.currentLocation)) {
                    errors.add(ValidationError.InvalidCharacterLocation(character.id, character.currentLocation))
                }
            }
            
            // Validate player location
            if (!placeIds.contains(gameState.player.currentLocation)) {
                errors.add(ValidationError.InvalidPlayerLocation(gameState.player.currentLocation))
            }
            
            // Validate clue locations (can be place IDs or character IDs)
            val characterIds = gameState.characters.map { it.id }.toSet()
            gameState.clues.forEach { clue ->
                val isValidLocation = placeIds.contains(clue.location) || characterIds.contains(clue.location)
                if (!isValidLocation) {
                    errors.add(ValidationError.InvalidClueReference(clue.id, clue.location))
                }
            }
        }
        
        // Validate phase (should be INTRODUCTION for initial game state)
        val validInitialPhases = listOf(GamePhase.START, GamePhase.TUTORIAL, GamePhase.INTRODUCTION)
        if (!validInitialPhases.contains(gameState.phase)) {
            errors.add(ValidationError.InvalidPhase(
                "Phase should be one of ${validInitialPhases.joinToString()}, but got ${gameState.phase}"
            ))
        }
        
        // Validate exactly one criminal exists
        val criminals = gameState.characters.filter { it.isCriminal }
        when {
            criminals.isEmpty() -> errors.add(ValidationError.NoCriminal())
            criminals.size > 1 -> errors.add(ValidationError.MultipleCriminals(
                "Found ${criminals.size} criminals: ${criminals.joinToString { it.id }}"
            ))
        }
        
        // Validate timeline
        if (gameState.timeline.startTime.minutes >= gameState.timeline.endTime.minutes) {
            errors.add(ValidationError.InvalidTimeline())
        }
        
        return errors
    }
    
    /**
     * Processes a raw LLM response: parses JSON, converts to GameState, and validates.
     * 
     * @param rawResponse The raw JSON string from the LLM
     * @return ProcessingResult with GameState on success, or ValidationError on failure
     */
    @OptIn(InternalSerializationApi::class)
    fun processGameInitializerResponse(rawResponse: String): ProcessingResult {
        // Parse JSON
        val parseResult = parseGameInitializerResponse(rawResponse)
        val llmResponse = parseResult.getOrElse { exception ->
            return ProcessingResult.Failure(
                ValidationError.JsonParseError(exception.message ?: "Unknown parsing error")
            )
        }
        
        // Convert to GameState
        val gameState = try {
            llmResponse.toGameState()
        } catch (e: Exception) {
            return ProcessingResult.Failure(
                ValidationError.JsonParseError("Failed to convert LLMResponse to GameState: ${e.message}")
            )
        }
        
        // Validate GameState
        val validationErrors = validateGameState(gameState)
        if (validationErrors.isNotEmpty()) {
            // Return the first error (or combine them if needed)
            return ProcessingResult.Failure(validationErrors.first())
        }
        
        return ProcessingResult.Success(gameState)
    }
}

