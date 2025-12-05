package com.ee309.detectivegame.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ee309.detectivegame.domain.model.Character
import com.ee309.detectivegame.domain.model.GameState
import com.ee309.detectivegame.domain.model.GameTime
import kotlinx.serialization.InternalSerializationApi

/**
 * Conversation/Interrogation screen with chat-like interface for questioning characters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    character: Character,
    messages: List<ConversationMessage>,
    gameState: GameState,
    isLoading: Boolean = false,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
) {
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    
    // Auto-scroll to bottom when new messages are added or loading state changes
    LaunchedEffect(messages.size, isLoading) {
        val itemCount = messages.size + if (isLoading) 1 else 0
        if (itemCount > 0) {
            listState.animateScrollToItem(itemCount - 1)
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top bar with character info and back button
        TopAppBar(
            title = {
                Column {
                    Text(text = character.name)
                    if (character.traits.isNotEmpty()) {
                        Text(
                            text = character.traits.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    // Clue progress indicator
                    val (collected, total) = getClueProgress(character, gameState)
                    Text(
                        text = "Clues: $collected/$total found",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        
        // Current time display
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current time: ${getCurrentAbsoluteTime(gameState).format()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Message list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Start the conversation...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                items(messages) { message ->
                    if (message.type == ConversationMessageType.SYSTEM) {
                        SystemMessageBubble(message = message)
                    } else {
                        ConversationMessageBubble(
                            message = message,
                            isFromPlayer = message.isFromPlayer
                        )
                    }
                }
                
                // Show loading indicator when character is generating response
                if (isLoading) {
                    item {
                        ConversationLoadingIndicator()
                    }
                }
            }
        }
        
        HorizontalDivider()
        
        // Input area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type your question...") },
                singleLine = true,
                enabled = !isLoading
            )
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !isLoading
            ) {
                Text("Send")
            }
        }
    }
}

/**
 * Represents a message in a conversation.
 */
@kotlinx.serialization.Serializable
data class ConversationMessage(
    val text: String,
    val isFromPlayer: Boolean,
    val timestamp: GameTime,
    val type: ConversationMessageType = ConversationMessageType.NORMAL
)

/**
 * Type of conversation message
 */
@kotlinx.serialization.Serializable
enum class ConversationMessageType {
    NORMAL,  // Regular player/character messages
    SYSTEM   // Game state change notifications
}

@Composable
private fun ConversationMessageBubble(
    message: ConversationMessage,
    isFromPlayer: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (isFromPlayer) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isFromPlayer) Arrangement.End else Arrangement.Start
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (isFromPlayer) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                tonalElevation = 2.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isFromPlayer) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
        // Time display (absolute time)
        Text(
            text = formatMessageTime(message.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 2.dp
            )
        )
    }
}

@Composable
private fun SystemMessageBubble(
    message: ConversationMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            tonalElevation = 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

/**
 * Formats the time display for a message.
 * messageTime is already absolute time, so just format it.
 */
private fun formatMessageTime(messageTime: GameTime): String {
    return messageTime.format()  // Returns "HH:MM"
}

/**
 * Gets the current absolute time from game state
 */
private fun getCurrentAbsoluteTime(gameState: GameState): GameTime {
    return GameTime(gameState.timeline.startTime.minutes + gameState.currentTime.minutes)
}

/**
 * Calculates clue progress for a character
 */
@OptIn(InternalSerializationApi::class)
private fun getClueProgress(character: Character, gameState: GameState): Pair<Int, Int> {
    // Get total obtainable clues (character's knownClues that are unlocked)
    val totalObtainable = character.knownClues
        .mapNotNull { clueId -> gameState.getClue(clueId) }
        .count { clue -> clue.isUnlocked(gameState.flags) }
    
    // Get collected clues (character's knownClues that player has)
    val collected = character.knownClues
        .count { clueId -> gameState.player.collectedClues.contains(clueId) }
    
    return Pair(collected, totalObtainable)
}

@Composable
private fun ConversationLoadingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Character is thinking...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

