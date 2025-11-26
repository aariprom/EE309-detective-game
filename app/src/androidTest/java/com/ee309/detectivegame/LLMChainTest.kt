package com.ee309.detectivegame

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.ee309.detectivegame.llm.config.LLMTask
import com.ee309.detectivegame.llm.data.LLMRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4ClassRunner::class)
class LLMChainTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var llmRepository: LLMRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun llm_chain_end_to_end_test() = runBlocking {
        // --- Test Keyword ---
        val keyword = "a murder mystery in a futuristic city called 'Neo-Alexandria'"

        // 1. LLM 1: Initializer
        println("--- Calling LLM 1: Initializer ---")
        val gameStructure = llmRepository.callUpstage(
            task = LLMTask.GameInitializer,
            userContent = keyword
        )
        println("LLM 1 Response (Game Structure):\n$gameStructure\n")

        // 2. LLM 2: Dialogue Generator
        println("--- Calling LLM 2: Dialogue Generator ---")
        val dialogue = ""
        println("LLM 2 Response (Dialogue):\n$dialogue\n")

        // 3. LLM 3: Description Generator
        println("--- Calling LLM 3: Description Generator ---")
        val description = ""
        println("LLM 3 Response (Description):\n$description\n")

        // 4. LLM 4: Action Handler
        println("--- Calling LLM 4: Action Handler ---")
        val actionOutcome = ""
        println("LLM 4 Response (Action Outcome):\n$actionOutcome\n")

        // 5. LLM 5: Component Updater
        println("--- Calling LLM 5: Component Updater ---")
        val componentUpdate = ""
        println("LLM 5 Response (Component Update):\n$componentUpdate\n")
    }
}