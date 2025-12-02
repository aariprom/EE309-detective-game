package com.ee309.detectivegame.llm.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Response from LLM 2: Intro Generator
 */
@OptIn(InternalSerializationApi::class)
@Serializable
data class IntroResponse(
    val text: String
)

