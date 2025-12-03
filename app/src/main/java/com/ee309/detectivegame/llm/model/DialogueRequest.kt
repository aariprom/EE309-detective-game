package com.ee309.detectivegame.llm.model

import com.ee309.detectivegame.domain.model.Character
import com.ee309.detectivegame.domain.model.Clue
import com.ee309.detectivegame.domain.model.GameState
import com.ee309.detectivegame.domain.model.GameTime
import com.ee309.detectivegame.domain.model.TimelineEvent
import com.ee309.detectivegame.llm.model.TimeInfo
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Request data for LLM 3: Dialogue Generator
 * Contains character information, player context, conversation history, and question
 */
@Serializable
data class DialogueRequest(
    val character: CharacterDialogueInfo,
    val player: PlayerDialogueInfo,
    val conversationHistory: List<ConversationHistoryItem>? = null,
    val playerQuestion: String,
    val timeline: TimelineDialogueInfo,
    val caseInfo: CaseDialogueInfo
)

/**
 * Character information for dialogue generation
 */
@Serializable
data class CharacterDialogueInfo(
    val id: String,
    val name: String,
    val traits: List<String>,
    val mentalState: String,
    val knownClues: List<String>,
    val currentLocation: String,
    val isCriminal: Boolean  // For LLM context, but don't reveal directly in dialogue
)

/**
 * Player context information
 */
@Serializable
data class PlayerDialogueInfo(
    val collectedClues: List<String>,  // List of clue IDs the player has found
    val currentTime: TimeInfo,  // Current game time (relative to startTime)
    val currentLocation: String
)

/**
 * Conversation history item
 */
@Serializable
data class ConversationHistoryItem(
    val role: String,  // "player" or "character"
    val text: String
)

/**
 * Timeline information for dialogue context
 */
@Serializable
data class TimelineDialogueInfo(
    val pastEvents: List<EventDialogueInfo>  // Events that occurred before current time
)

/**
 * Event information for dialogue context
 */
@Serializable
data class EventDialogueInfo(
    val id: String,
    val time: TimeInfo,  // Absolute time
    val eventType: String,
    val description: String,
    val characterId: String?,
    val placeId: String?
)

/**
 * Case information
 */
@Serializable
data class CaseDialogueInfo(
    val title: String,
    val description: String
)

// TimeInfo is already defined in IntroRequest.kt, reusing it here

/**
 * Extension function to convert GameState to DialogueRequest
 */
@OptIn(InternalSerializationApi::class)
fun GameState.toDialogueRequest(
    characterId: String,
    playerQuestion: String,
    conversationHistory: List<com.ee309.detectivegame.ui.compose.ConversationMessage>?
): DialogueRequest {
    val character = getCharacter(characterId) ?: throw IllegalArgumentException("Character not found: $characterId")
    
    // Get character's known clues as full clue objects
    val characterKnownClues = character.knownClues.mapNotNull { clueId ->
        getClue(clueId)?.id
    }
    
    // Get player's collected clues
    val playerCollectedClues = player.collectedClues
    
    // Get past timeline events
    val pastEvents = timeline.getPastEvents(currentTime).map { event ->
        EventDialogueInfo(
            id = event.id,
            time = TimeInfo(event.time.minutes),
            eventType = event.eventType.name,
            description = event.description,
            characterId = event.characterId,
            placeId = event.placeId
        )
    }
    
    // Convert conversation history (exclude system messages)
    val history = conversationHistory?.filter { msg ->
        msg.type == com.ee309.detectivegame.ui.compose.ConversationMessageType.NORMAL
    }?.map { msg ->
        ConversationHistoryItem(
            role = if (msg.isFromPlayer) "player" else "character",
            text = msg.text
        )
    }
    
    return DialogueRequest(
        character = CharacterDialogueInfo(
            id = character.id,
            name = character.name,
            traits = character.traits,
            mentalState = character.mentalState,
            knownClues = characterKnownClues,
            currentLocation = character.currentLocation,
            isCriminal = character.isCriminal
        ),
        player = PlayerDialogueInfo(
            collectedClues = playerCollectedClues,
            currentTime = TimeInfo(currentTime.minutes),
            currentLocation = player.currentLocation
        ),
        conversationHistory = history,
        playerQuestion = playerQuestion,
        timeline = TimelineDialogueInfo(
            pastEvents = pastEvents
        ),
        caseInfo = CaseDialogueInfo(
            title = title,
            description = description
        )
    )
}

