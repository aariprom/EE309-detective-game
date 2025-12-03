package com.ee309.detectivegame.llm.model

import kotlinx.serialization.Serializable

/**
 * Response from LLM 3: Dialogue Generator
 */
@Serializable
data class DialogueResponse(
    val dialogue: String,
    val newClues: List<String>? = null,
    val mentalStateUpdate: String? = null,
    val hints: List<String>? = null
)

