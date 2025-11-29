package com.ee309.detectivegame

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ee309.detectivegame.domain.model.GamePhase
import com.ee309.detectivegame.llm.config.LLMTask
import com.ee309.detectivegame.llm.data.LLMRepository
import com.ee309.detectivegame.llm.model.LLMResponse
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.InternalSerializationApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
}

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GameInitializerTest {
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
    fun llm_chain_end_to_end_test() = runBlocking {
        // --- Test Keyword ---
        val keyword = "a murder mystery in a futuristic city called 'Neo-Alexandria'"

        // 1. LLM 1: Initializer
        println("--- Calling LLM 1: Initializer ---")
        val gameStructure = llmRepository.callUpstage(
            task = LLMTask.GameInitializer,
            userContent = keyword,
            maxTokens = 10000
        )
        println("LLM 1 Response (Game Structure):\n$gameStructure\n")

        // Check if every necessary field exists
        val response = try {
            json.decodeFromString<LLMResponse>(gameStructure)
        } catch (e: Exception) {
            println("❌ JSON parsing failed: $e")
            null
        }

        if (response == null) {
            println("LLM returned invalid JSON")
            throw AssertionError("Test failed due to invalid JSON")
        } else {
            val gameState = response.toGameState()
            println("Parsed gameState: $gameState")

            // further validation; this will be useful to enforce LLM to generate correct JSON
            require(gameState.characters.isNotEmpty()) { "No characters found" }
            require(gameState.places.isNotEmpty()) { "No places found" }
            require(gameState.phase == GamePhase.INTRODUCTION) { "Phase is not INTRODUCTION "}

            // todo: for every character, location of character must be valid(its id must be in place list)
            // require(...)

            // todo: there must be one criminal
            // require(...)
        }
    }
}