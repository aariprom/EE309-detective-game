package com.ee309.detectivegame

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ee309.detectivegame.domain.model.GamePhase
import com.ee309.detectivegame.llm.config.LLMTask
import com.ee309.detectivegame.llm.config.LLMResponseProcessor
import com.ee309.detectivegame.llm.data.LLMRepository
import com.ee309.detectivegame.llm.model.LLMResponse
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Mock LLM 1 response JSON matching the LLMSchema.GameInitializer.SCHEMA
 * Based on MockGameData structure for consistency
 */
private const val MOCK_LLM1_RESPONSE = """
{
  "title": "Mock Game Title",
  "description": "Mock Game Description - A mysterious case in a corporate building",
  "phase": "INTRODUCTION",
  "currentTime": 0,
  "player": {
    "currentLocation": "place_lobby",
    "tools": []
  },
  "characters": [
    {
      "id": "char_alice",
      "name": "Alice",
      "description": "A suspicious person who seems nervous",
      "traits": ["Suspicious", "Nervous"],
      "initialLocation": "place_lobby",
      "isCriminal": true,
      "unlockConditions": []
    },
    {
      "id": "char_bob",
      "name": "Bob",
      "description": "A calm and cooperative individual",
      "traits": ["Calm", "Cooperative"],
      "initialLocation": "place_office",
      "isCriminal": false,
      "unlockConditions": []
    },
    {
      "id": "char_charlie",
      "name": "Charlie",
      "description": "A helpful and observant security guard",
      "traits": ["Helpful", "Observant"],
      "initialLocation": "place_lobby",
      "isCriminal": false,
      "unlockConditions": []
    },
    {
      "id": "char_victim",
      "name": "Victim",
      "description": "The victim of the crime",
      "traits": [],
      "initialLocation": "place_office",
      "isCriminal": false,
      "unlockConditions": ["found_clue_1"]
    }
  ],
  "places": [
    {
      "id": "place_office",
      "name": "CEO Office",
      "description": "The crime scene",
      "availableClues": ["clue_1", "clue_2", "clue_5"],
      "unlockConditions": [],
      "connections": ["place_lobby"]
    },
    {
      "id": "place_lobby",
      "name": "Building Lobby",
      "description": "The starting location",
      "availableClues": ["clue_3", "clue_4"],
      "unlockConditions": [],
      "connections": ["place_office", "place_parking"]
    },
    {
      "id": "place_parking",
      "name": "Parking Lot",
      "description": "An outdoor area",
      "availableClues": [],
      "unlockConditions": [],
      "connections": ["place_lobby"]
    }
  ],
  "clues": [
    {
      "id": "clue_1",
      "name": "Bloodstain",
      "description": "A suspicious bloodstain on the carpet.",
      "location": "place_office",
      "unlockConditions": []
    },
    {
      "id": "clue_2",
      "name": "Broken Window",
      "description": "A window in the office has been shattered.",
      "location": "place_office",
      "unlockConditions": []
    },
    {
      "id": "clue_3",
      "name": "Security Footage",
      "description": "The lobby security camera footage.",
      "location": "place_lobby",
      "unlockConditions": []
    },
    {
      "id": "clue_4",
      "name": "Security Guard Testimony",
      "description": "The security guard's account of the events.",
      "location": "char_charlie",
      "unlockConditions": ["talked_to_charlie"]
    },
    {
      "id": "clue_5",
      "name": "Fingerprints",
      "description": "Unidentified fingerprints on the victim's desk.",
      "location": "place_office",
      "unlockConditions": ["found_clue_1", "investigated_office"]
    }
  ],
  "timeline": {
    "startTime": {
      "minutes": 0
    },
    "endTime": {
      "minutes": 480
    },
    "events": [
      {
        "id": "event_alice_moves_to_office",
        "time": {
          "minutes": 120
        },
        "eventType": "CHARACTER_MOVEMENT",
        "description": "Alice moves to the CEO Office.",
        "characterId": "char_alice",
        "placeId": "place_office"
      },
      {
        "id": "event_power_outage",
        "time": {
          "minutes": 180
        },
        "eventType": "PLACE_CHANGE",
        "description": "The power goes out in the office.",
        "characterId": null,
        "placeId": "place_office"
      },
      {
        "id": "event_custom_scream",
        "time": {
          "minutes": 240
        },
        "eventType": "CUSTOM",
        "description": "A scream is heard from the parking lot.",
        "characterId": null,
        "placeId": "place_parking"
      }
    ]
  },
  "flags": [
    {
      "id": "game_started",
      "value": true
    }
  ]
}
"""

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class IntroGeneratorTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var llmRepository: LLMRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @OptIn(ExperimentalCoroutinesApi::class, InternalSerializationApi::class)
    @Test
    fun llm2_intro_generator_test() = runBlocking {
        // Create local json instance to avoid conflict with GameInitializerTest
        val json = Json {
            ignoreUnknownKeys = true
        }
        
        println("=== LLM 2: Intro Generator Test ===\n")

        // Step 1: Parse mocked LLM 1 response
        println("--- Step 1: Parsing Mocked LLM 1 Response ---")
        val llm1Response = try {
            json.decodeFromString<LLMResponse>(MOCK_LLM1_RESPONSE)
        } catch (e: Exception) {
            println("❌ JSON parsing failed: $e")
            throw AssertionError("Failed to parse mock LLM 1 response: ${e.message}")
        }
        println("✅ Successfully parsed LLM 1 response")
        println("Title: ${llm1Response.title}")
        println("Description: ${llm1Response.description}")
        println("Phase: ${llm1Response.phase}")
        println("Characters: ${llm1Response.characters.size}")
        println("Places: ${llm1Response.places.size}\n")

        // Step 2: Convert LLMResponse to GameState
        println("--- Step 2: Converting to GameState ---")
        val gameState = try {
            llm1Response.toGameState()
        } catch (e: Exception) {
            println("❌ Failed to convert to GameState: $e")
            throw AssertionError("Failed to convert LLMResponse to GameState: ${e.message}")
        }
        println("✅ Successfully converted to GameState")
        println("GameState title: ${gameState.title}")
        println("GameState characters: ${gameState.characters.size}")
        println("GameState places: ${gameState.places.size}\n")

        // Validate GameState
        require(gameState.characters.isNotEmpty()) { "No characters found in GameState" }
        require(gameState.places.isNotEmpty()) { "No places found in GameState" }
        require(gameState.phase == GamePhase.INTRODUCTION) { "Phase should be INTRODUCTION" }
        println("✅ GameState validation passed\n")

        // Step 3: Convert GameState to IntroRequest
        println("--- Step 3: Converting GameState to IntroRequest ---")
        val introRequest = try {
            gameState.toIntroRequest()
        } catch (e: Exception) {
            println("❌ Failed to convert GameState to IntroRequest: $e")
            throw AssertionError("Failed to convert GameState to IntroRequest: ${e.message}")
        }
        println("✅ Successfully created IntroRequest")
        println("IntroRequest title: ${introRequest.title}")
        println("IntroRequest characters (public): ${introRequest.characters.size}")
        println("IntroRequest places (public): ${introRequest.places.size}\n")

        // Serialize IntroRequest to JSON
        val introRequestJson = json.encodeToString(
            com.ee309.detectivegame.llm.model.IntroRequest.serializer(),
            introRequest
        )
        println("IntroRequest JSON (first 200 chars): ${introRequestJson.take(200)}...\n")

        // Step 4: Call LLM 2 (Intro Generator)
        println("--- Step 4: Calling LLM 2 (Intro Generator) ---")
        val llm2Response = llmRepository.callUpstage(
            task = LLMTask.IntroGenerator,
            userContent = introRequestJson,
            maxTokens = 2000
        )
        println("LLM 2 Raw Response:\n$llm2Response\n")

        // Step 5: Process LLM 2 response
        println("--- Step 5: Processing LLM 2 Response ---")
        when (val result = LLMResponseProcessor.IntroGenerator.process(llm2Response)) {
            is LLMResponseProcessor.ProcessingResult.Success -> {
                val introText = result.data
                println("✅ Successfully processed intro text")
                println("Intro text length: ${introText.length} characters\n")
                println("=".repeat(80))
                println("INTRO TEXT:")
                println("=".repeat(80))
                println(introText)
                println("=".repeat(80))
            }
            is LLMResponseProcessor.ProcessingResult.Failure -> {
                println("❌ Failed to process intro text: ${result.error.message}")
                throw AssertionError("LLM 2 processing failed: ${result.error.message}")
            }
        }
    }
}

