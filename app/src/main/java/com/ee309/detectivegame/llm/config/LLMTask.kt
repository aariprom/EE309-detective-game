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

    // LLM 2: Intro Generator
    object IntroGenerator: LLMTask(
        model = "solar-pro2",
        systemPrompt = LLMPrompt.IntroGenerator.SYSTEM_PROMPT,
        schema = LLMSchema.IntroGenerator.SCHEMA
    )
    // Todo: Implement followings

    // LLM 3: Dialogue Generator
    // LLM 4: Description Generator
}