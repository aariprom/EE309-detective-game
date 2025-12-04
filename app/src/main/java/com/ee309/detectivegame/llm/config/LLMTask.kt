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
    // LLM 3: Dialogue Generator
    object DialogueGenerator : LLMTask(
        model = "solar-pro2",
        systemPrompt = LLMPrompt.DialogueGenerator.SYSTEM_PROMPT,
        schema = LLMSchema.DialogueGenerator.SCHEMA
    )

    // LLM 4: Description Generator
    object DescriptionGenerator : LLMTask(
        model = "solar-pro2",
        systemPrompt = LLMPrompt.DescriptionGenerator.SYSTEM_PROMPT,
        schema = LLMSchema.DescriptionGenerator.SCHEMA
    )

    // LLM 5: Epilogue Generator
    object EpilogueGenerator : LLMTask(
        model = "solar-pro2",
        systemPrompt = LLMPrompt.EpilogueGenerator.SYSTEM_PROMPT,
        schema = LLMSchema.EpilogueGenerator.SCHEMA
    )
}
