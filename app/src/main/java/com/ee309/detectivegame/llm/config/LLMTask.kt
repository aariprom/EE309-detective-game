package com.ee309.detectivegame.llm.config

sealed class LLMTask (
    val model: String,
    val systemPrompt: String,
    val schema: String?
) {
    // LLM 1
    object GameInitializer : LLMTask(
        model = "solar-pro2",
        systemPrompt = LLMPrompt.GameInitializer.SYSTEM_PROMPT,
        schema = LLMSchema.GameInitializer.SCHEMA
    )

    // Todo: Implement followings
    // LLM 2: Dialogue Generator
    // LLM 3: Description Generator
    // LLM 4: Action Handler
    // LLM 5: Component Updater
}