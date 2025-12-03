package com.ee309.detectivegame

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ee309.detectivegame.domain.model.GamePhase
import com.ee309.detectivegame.llm.config.LLMTask
import com.ee309.detectivegame.llm.config.LLMResponseProcessor
import com.ee309.detectivegame.llm.data.LLMRepository
import com.ee309.detectivegame.llm.model.LLMResponse
import com.ee309.detectivegame.llm.model.toDialogueRequest
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
 * Updated to include knownClues for characters (required for LLM 3 testing)
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
      "unlockConditions": [],
      "knownClues": ["clue_1", "clue_2"]
    },
    {
      "id": "char_bob",
      "name": "Bob",
      "description": "A calm and cooperative individual",
      "traits": ["Calm", "Cooperative"],
      "initialLocation": "place_office",
      "isCriminal": false,
      "unlockConditions": [],
      "knownClues": ["clue_1", "clue_5"]
    },
    {
      "id": "char_charlie",
      "name": "Charlie",
      "description": "A helpful and observant security guard",
      "traits": ["Helpful", "Observant"],
      "initialLocation": "place_lobby",
      "isCriminal": false,
      "unlockConditions": [],
      "knownClues": ["clue_3", "clue_4"]
    },
    {
      "id": "char_victim",
      "name": "Victim",
      "description": "The victim of the crime",
      "traits": [],
      "initialLocation": "place_office",
      "isCriminal": false,
      "unlockConditions": ["found_clue_1"],
      "knownClues": []
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
    "baseTime": {
      "minutes": 960
    },
    "startTime": {
      "minutes": 1080
    },
    "endTime": {
      "minutes": 1440
    },
    "events": [
      {
        "id": "event_crime",
        "time": {
          "minutes": 1020
        },
        "eventType": "CRIME",
        "description": "The crime occurs.",
        "characterId": "char_alice",
        "placeId": "place_office"
      },
      {
        "id": "event_alice_moves_to_office",
        "time": {
          "minutes": 1200
        },
        "eventType": "CHARACTER_MOVEMENT",
        "description": "Alice moves to the CEO Office.",
        "characterId": "char_alice",
        "placeId": "place_office"
      },
      {
        "id": "event_power_outage",
        "time": {
          "minutes": 1260
        },
        "eventType": "PLACE_CHANGE",
        "description": "The power goes out in the office.",
        "characterId": null,
        "placeId": "place_office"
      },
      {
        "id": "event_custom_scream",
        "time": {
          "minutes": 1320
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
class DialogueGeneratorTest {
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
    fun llm3_dialogue_generator_test() = runBlocking {
        // Create local json instance to avoid conflict with other tests
        val json = Json {
            ignoreUnknownKeys = true
        }
        
        println("=== LLM 3: Dialogue Generator Test ===\n")

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
        println("Characters: ${llm1Response.characters.size}")
        println("Places: ${llm1Response.places.size}")
        println("Clues: ${llm1Response.clues.size}\n")

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
        
        // Validate GameState
        require(gameState.characters.isNotEmpty()) { "No characters found in GameState" }
        require(gameState.places.isNotEmpty()) { "No places found in GameState" }
        require(gameState.clues.isNotEmpty()) { "No clues found in GameState" }
        require(gameState.phase == GamePhase.INTRODUCTION) { "Phase should be INTRODUCTION" }
        
        // Validate characters have knownClues
        val charCharlie = gameState.getCharacter("char_charlie")
        require(charCharlie != null) { "char_charlie not found" }
        require(charCharlie.knownClues.isNotEmpty()) { "char_charlie should have knownClues" }
        println("✅ char_charlie has ${charCharlie.knownClues.size} known clues: ${charCharlie.knownClues}")
        println()

        // Step 3: Set up conversation scenario
        println("--- Step 3: Setting Up Conversation Scenario ---")
        val characterId = "char_charlie"
        val character = gameState.getCharacter(characterId)
        require(character != null) { "Character $characterId not found" }
        
        // Ensure player is at the same location as character
        val playerLocation = character.currentLocation
        val updatedPlayer = gameState.player.copy(currentLocation = playerLocation)
        val gameStateWithPlayerLocation = gameState.copy(player = updatedPlayer)
        
        println("✅ Character: ${character.name}")
        println("✅ Character location: ${character.currentLocation}")
        println("✅ Player location set to: $playerLocation")
        println("✅ Character known clues: ${character.knownClues.size}")
        println()

        // Step 4: Convert GameState to DialogueRequest
        println("--- Step 4: Converting GameState to DialogueRequest ---")
        val playerQuestion = "What did you see around the time of the incident?"
        val dialogueRequest = try {
            gameStateWithPlayerLocation.toDialogueRequest(
                characterId = characterId,
                playerQuestion = playerQuestion,
                conversationHistory = null // First conversation, no history
            )
        } catch (e: Exception) {
            println("❌ Failed to convert GameState to DialogueRequest: $e")
            throw AssertionError("Failed to convert GameState to DialogueRequest: ${e.message}")
        }
        println("✅ Successfully created DialogueRequest")
        println("Character: ${dialogueRequest.character.name}")
        println("Character traits: ${dialogueRequest.character.traits}")
        println("Character known clues: ${dialogueRequest.character.knownClues.size}")
        println("Player question: $playerQuestion")
        println("Player collected clues: ${dialogueRequest.player.collectedClues.size}")
        println()

        // Serialize DialogueRequest to JSON
        val dialogueRequestJson = json.encodeToString(
            com.ee309.detectivegame.llm.model.DialogueRequest.serializer(),
            dialogueRequest
        )
        println("DialogueRequest JSON (first 300 chars): ${dialogueRequestJson.take(300)}...\n")

        // Step 5: Call LLM 3 (Dialogue Generator)
        println("--- Step 5: Calling LLM 3 (Dialogue Generator) ---")
        val llm3Response = try {
            llmRepository.callUpstage(
                task = LLMTask.DialogueGenerator,
                userContent = dialogueRequestJson,
                maxTokens = 2000
            )
        } catch (e: Exception) {
            println("❌ LLM 3 API call failed: $e")
            throw AssertionError("LLM 3 API call failed: ${e.message}")
        }
        println("✅ LLM 3 API call successful")
        println("LLM 3 Raw Response:\n$llm3Response\n")

        // Step 6: Process LLM 3 response
        println("--- Step 6: Processing LLM 3 Response ---")
        when (val result = LLMResponseProcessor.DialogueGenerator.process(llm3Response)) {
            is LLMResponseProcessor.ProcessingResult.Success -> {
                val dialogueData = result.data
                println("✅ Successfully processed dialogue response")
                println("Dialogue text length: ${dialogueData.dialogue.length} characters")
                println()
                
                println("=".repeat(80))
                println("CHARACTER DIALOGUE:")
                println("=".repeat(80))
                println(dialogueData.dialogue)
                println("=".repeat(80))
                println()
                
                if (dialogueData.newClues != null && dialogueData.newClues.isNotEmpty()) {
                    println("✅ Clues revealed: ${dialogueData.newClues.size}")
                    dialogueData.newClues.forEach { clueId ->
                        val clue = gameState.getClue(clueId)
                        if (clue != null) {
                            println("  - $clueId: ${clue.name}")
                        } else {
                            println("  - $clueId: (clue not found in GameState)")
                        }
                    }
                    println()
                } else {
                    println("ℹ️  No clues revealed in this conversation")
                    println()
                }
                
                if (dialogueData.mentalStateUpdate != null) {
                    println("✅ Mental state update: ${dialogueData.mentalStateUpdate}")
                    println()
                }
                
                if (dialogueData.hints != null && dialogueData.hints.isNotEmpty()) {
                    println("✅ Hints provided: ${dialogueData.hints.size}")
                    dialogueData.hints.forEach { hint ->
                        println("  - $hint")
                    }
                    println()
                }
                
                // Validate dialogue text
                require(dialogueData.dialogue.isNotBlank()) { "Dialogue text should not be blank" }
                require(dialogueData.dialogue.length <= 1000) { "Dialogue text should be <= 1000 characters" }
                println("✅ Dialogue validation passed")
            }
            is LLMResponseProcessor.ProcessingResult.Failure -> {
                println("❌ Failed to process dialogue response: ${result.error.message}")
                throw AssertionError("LLM 3 processing failed: ${result.error.message}")
            }
        }
        
        println("\n=== Test Completed Successfully ===")
    }
}

