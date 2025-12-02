package com.ee309.detectivegame.llm.config

import com.ee309.detectivegame.domain.model.GamePhase
import com.ee309.detectivegame.domain.model.GameState
import com.ee309.detectivegame.llm.model.LLMResponse
import com.ee309.detectivegame.llm.model.IntroResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * Processor for LLM responses with JSON parsing, validation, and error handling.
 * Follows the pattern of other LLM config files (LLMPrompt, LLMSchema, LLMTask).
 * 
 * Uses a hybrid approach with:
 * - Common utilities for JSON parsing
 * - Generic ProcessingResult for type safety
 * - Task-specific processors as nested objects
 */
object LLMResponseProcessor {
    
    /**
     * Shared JSON instance with common configuration
     */
    private val json = Json {
        ignoreUnknownKeys = true
    }
    
    /**
     * Common validation errors shared across all LLM tasks
     */
    sealed class ValidationError(open val message: String) {
        data class EmptyResponse(override val message: String = "LLM returned empty response") : ValidationError(message)
        data class JsonParseError(override val message: String) : ValidationError(message)
    }
    
    /**
     * Generic result type for processing LLM responses.
     * 
     * @param T The type of data returned on success
     */
    sealed class ProcessingResult<out T> {
        data class Success<T>(val data: T) : ProcessingResult<T>()
        data class Failure(val error: ValidationError) : ProcessingResult<Nothing>()
    }
    
    /**
     * Generic JSON parsing utility that works with any serializable type.
     * 
     * @param rawResponse The raw JSON string from the LLM
     * @param serializer The serializer for type T (KSerializer works for both serialization and deserialization)
     * @return Result containing parsed object on success, or exception on failure
     */
    @OptIn(InternalSerializationApi::class)
    fun <T> parseJson(
        rawResponse: String,
        serializer: KSerializer<T>
    ): Result<T> {
        // Check for empty response
        if (rawResponse.isBlank()) {
            return Result.failure(Exception(ValidationError.EmptyResponse().message))
        }
        
        // Parse JSON
        return try {
            val response = json.decodeFromString(serializer, rawResponse)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(ValidationError.JsonParseError("JSON parsing failed: ${e.message}").message))
        }
    }
    
    /**
     * Processor for LLM 1: GameInitializer
     * Handles parsing and validation of game initialization responses.
     */
    object GameInitializer {
        
        /**
         * GameInitializer-specific validation errors
         */
        sealed class ValidationError(override val message: String) : LLMResponseProcessor.ValidationError(message) {
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
            data class MissingBaseTime(
                override val message: String = "Timeline is missing baseTime"
            ) : ValidationError(message)
            data class InvalidBaseTime(
                override val message: String = "Timeline baseTime must be before startTime (all absolute times)"
            ) : ValidationError(message)
            data class MissingCrimeEvent(
                override val message: String = "Timeline must contain exactly one CRIME event between baseTime and startTime"
            ) : ValidationError(message)
            data class InvalidCrimeEventTime(
                override val message: String = "Crime event time must be between baseTime and startTime (all absolute times)"
            ) : ValidationError(message)
            data class InvalidClueReference(
                val clueId: String,
                val locationId: String,
                override val message: String = "Clue '$clueId' references invalid location '$locationId'"
            ) : ValidationError(message)
        }
        
        /**
         * Parses the raw LLM response string into an LLMResponse object.
         * 
         * @param rawResponse The raw JSON string from the LLM
         * @return Result containing LLMResponse on success, or exception on failure
         */
        @OptIn(InternalSerializationApi::class)
        fun parse(rawResponse: String): Result<LLMResponse> {
            return LLMResponseProcessor.parseJson(rawResponse, LLMResponse.serializer())
        }
        
        /**
         * Validates a GameState to ensure it meets all requirements.
         * 
         * @param gameState The GameState to validate
         * @return List of validation errors (empty if valid)
         */
        @OptIn(InternalSerializationApi::class)
        fun validate(gameState: GameState): List<ValidationError> {
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
            
            // Validate timeline structure - all times are absolute
            val timeline = gameState.timeline
            
            // Validate timeline structure
            if (timeline.baseTime.minutes >= timeline.startTime.minutes) {
                errors.add(ValidationError.InvalidBaseTime())
            }
            
            if (timeline.startTime.minutes >= timeline.endTime.minutes) {
                errors.add(ValidationError.InvalidTimeline())
            }
            
            // Validate crime event
            val crimeEvents = timeline.getCrimeEvents()
            if (crimeEvents.isEmpty()) {
                errors.add(ValidationError.MissingCrimeEvent())
            } else if (crimeEvents.size > 1) {
                errors.add(ValidationError.MissingCrimeEvent()) // Should be exactly one
            } else {
                // Validate crime event time is between baseTime and startTime (all absolute)
                val crimeEvent = crimeEvents.first()
                val baseTimeMinutes = timeline.baseTime.minutes
                val startTimeMinutes = timeline.startTime.minutes
                val crimeTimeMinutes = crimeEvent.time.minutes
                
                if (crimeTimeMinutes <= baseTimeMinutes || crimeTimeMinutes >= startTimeMinutes) {
                    errors.add(ValidationError.InvalidCrimeEventTime())
                }
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
        fun process(rawResponse: String): ProcessingResult<GameState> {
            // Parse JSON
            val parseResult = parse(rawResponse)
            val llmResponse = parseResult.getOrElse { exception ->
                return ProcessingResult.Failure(
                    LLMResponseProcessor.ValidationError.JsonParseError(exception.message ?: "Unknown parsing error")
                )
            }
            
            // Convert to GameState
            val gameState = try {
                llmResponse.toGameState()
            } catch (e: Exception) {
                return ProcessingResult.Failure(
                    LLMResponseProcessor.ValidationError.JsonParseError("Failed to convert LLMResponse to GameState: ${e.message}")
                )
            }
            
            // Validate GameState
            val validationErrors = validate(gameState)
            if (validationErrors.isNotEmpty()) {
                // Return the first error (or combine them if needed)
                return ProcessingResult.Failure(validationErrors.first())
            }
            
            return ProcessingResult.Success(gameState)
        }
    }
    
    /**
     * Processor for LLM 2: IntroGenerator
     * Handles parsing and validation of intro text responses.
     */
    object IntroGenerator {
        
        /**
         * IntroGenerator-specific validation errors
         */
        sealed class ValidationError(override val message: String) : LLMResponseProcessor.ValidationError(message) {
            data class EmptyText(override val message: String = "Intro text is empty") : ValidationError(message)
            data class TextTooShort(
                val length: Int,
                override val message: String = "Intro text is too short (minimum 50 characters, got $length)"
            ) : ValidationError(message)
            data class TextTooLong(
                val length: Int,
                override val message: String = "Intro text is too long (maximum 2000 characters, got $length)"
            ) : ValidationError(message)
        }
        
        /**
         * Processes a raw LLM response for intro text.
         * IntroGenerator returns JSON with a "text" field according to the schema.
         * 
         * @param rawResponse The raw response string from the LLM
         * @return ProcessingResult with intro text on success, or ValidationError on failure
         */
        @OptIn(InternalSerializationApi::class)
        fun process(rawResponse: String): ProcessingResult<String> {
            // Check for empty response
            if (rawResponse.isBlank()) {
                return ProcessingResult.Failure(
                    LLMResponseProcessor.ValidationError.EmptyResponse()
                )
            }
            
            // Parse JSON response
            val parseResult = LLMResponseProcessor.parseJson(rawResponse, IntroResponse.serializer())
            val introResponse = parseResult.getOrElse { exception ->
                // If JSON parsing fails, try to treat as plain text (fallback)
                val fallbackText = rawResponse.trim()
                val validationErrors = validate(fallbackText)
                if (validationErrors.isEmpty()) {
                    return ProcessingResult.Success(fallbackText)
                }
                return ProcessingResult.Failure(
                    LLMResponseProcessor.ValidationError.JsonParseError(exception.message ?: "Unknown parsing error")
                )
            }
            
            // Extract text from response
            val introText = introResponse.text.trim()
            
            // Validate intro text
            val validationErrors = validate(introText)
            if (validationErrors.isNotEmpty()) {
                return ProcessingResult.Failure(validationErrors.first())
            }
            
            return ProcessingResult.Success(introText)
        }
        
        /**
         * Validates intro text to ensure it meets requirements.
         * 
         * @param introText The intro text to validate
         * @return List of validation errors (empty if valid)
         */
        fun validate(introText: String): List<ValidationError> {
            val errors = mutableListOf<ValidationError>()
            
            if (introText.isBlank()) {
                errors.add(ValidationError.EmptyText())
            }
            
            if (introText.length < 50) {
                errors.add(ValidationError.TextTooShort(introText.length))
            }
            
            if (introText.length > 2000) {
                errors.add(ValidationError.TextTooLong(introText.length))
            }
            
            return errors
        }
    }
}
