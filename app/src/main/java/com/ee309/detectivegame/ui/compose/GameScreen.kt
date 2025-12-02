package com.ee309.detectivegame.ui.compose
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ee309.detectivegame.domain.model.GameAction
import com.ee309.detectivegame.domain.model.GamePhase
import com.ee309.detectivegame.presentation.state.GameUiState
import com.ee309.detectivegame.presentation.viewmodel.GameViewModel
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val conversationHistory by viewModel.conversationHistory.collectAsState()
    val introText by viewModel.introText.collectAsState()
    val introShown by viewModel.introShown.collectAsState()
    
    // Local state for dialogs and conversation
    var showCharacterDialog by remember { mutableStateOf(false) }
    var showPlaceDialog by remember { mutableStateOf(false) }
    var dialogActionType by remember { mutableStateOf<DialogActionType?>(null) }
    var conversationCharacterId by remember { mutableStateOf<String?>(null) }
    
    // Show StartScreen if game hasn't started (gameState is null)
    when {
        gameState == null -> {
            StartScreen(viewModel = viewModel)
        }
        
        // Show IntroScreen if intro text is available and hasn't been shown yet
        introText != null && !introShown -> {
            IntroScreen(
                introText = introText!!,
                onContinue = { viewModel.onIntroComplete() }
            )
        }
        
        uiState is GameUiState.Success -> {
            val successState = uiState as GameUiState.Success
            val currentGameState = successState.gameState
            
            // Check for win/lose phase
            when (currentGameState.phase) {
                GamePhase.WIN, GamePhase.LOSE -> {
                    GameOverScreen(
                        phase = currentGameState.phase,
                        onRestart = {
                            viewModel.startNewGame("")
                            conversationCharacterId = null
                        }
                    )
                }
                
                else -> {
                    // Show conversation screen if a character is being questioned
                    conversationCharacterId?.let { charId ->
                        val character = currentGameState.getCharacter(charId)
                        if (character != null) {
                            ConversationScreen(
                                character = character,
                                messages = conversationHistory[charId] ?: emptyList(),
                                onBack = { conversationCharacterId = null },
                                onSendMessage = { message ->
                                    viewModel.addConversationMessage(charId, ConversationMessage(message, true))
                                    viewModel.addConversationMessage(charId, ConversationMessage("[Character response placeholder]", false))
                                }
                            )
                            return
                        }
                    }
                    
                    // Main game screen
                    MainGameScreenContent(
                        gameState = currentGameState,
                        viewModel = viewModel,
                        onInvestigateClick = {
                            val currentLocation = currentGameState.player.currentLocation
                            if (currentLocation.isNotEmpty()) {
                                val place = currentGameState.getPlace(currentLocation)
                                if (place != null && place.isUnlocked(currentGameState.flags)) {
                                    viewModel.executeAction(GameAction.Investigate(currentLocation))
                                }
                            }
                        },
                        onQuestionClick = {
                            dialogActionType = DialogActionType.Question
                            showCharacterDialog = true
                        },
                        onMoveClick = {
                            dialogActionType = DialogActionType.Move
                            showPlaceDialog = true
                        },
                        onAccuseClick = {
                            dialogActionType = DialogActionType.Accuse
                            showCharacterDialog = true
                        }
                    )
                    
                    // Character selection dialog
                    if (showCharacterDialog) {
                        val characters = when (dialogActionType) {
                            DialogActionType.Question -> {
                                // Show only characters at current location
                                currentGameState.getCharactersAtLocation(currentGameState.player.currentLocation)
                            }
                            else -> {
                                // Show all unlocked characters
                                currentGameState.getUnlockedCharacters()
                            }
                        }
                        
                        CharacterSelectionDialog(
                            characters = characters,
                            onDismiss = {
                                showCharacterDialog = false
                                dialogActionType = null
                            },
                            onCharacterSelected = { character ->
                                when (dialogActionType) {
                                    DialogActionType.Question -> {
                                        conversationCharacterId = character.id
                                        val existingHistory = viewModel.getConversationHistory(character.id)
                                        if (existingHistory.isEmpty()) {
                                            viewModel.addConversationMessage(character.id, ConversationMessage("You start questioning ${character.name}...", false))
                                        }
                                        viewModel.executeAction(GameAction.Question(character.id))
                                    }
                                    DialogActionType.Accuse -> {
                                        viewModel.executeAction(GameAction.Accuse(character.id))
                                    }
                                    else -> {}
                                }
                                showCharacterDialog = false
                                dialogActionType = null
                            },
                            title = when (dialogActionType) {
                                DialogActionType.Question -> "Select Character to Question"
                                DialogActionType.Accuse -> "Select Character to Accuse"
                                else -> "Select Character"
                            },
                            filterUnlocked = true,
                            filterByLocation = if (dialogActionType == DialogActionType.Question) {
                                currentGameState.player.currentLocation
                            } else null,
                            flags = currentGameState.flags
                        )
                    }
                    
                    // Place selection dialog
                    if (showPlaceDialog) {
                        PlaceSelectionDialog(
                            places = currentGameState.getUnlockedPlaces(),
                            onDismiss = {
                                showPlaceDialog = false
                                dialogActionType = null
                            },
                            onPlaceSelected = { place ->
                                when (dialogActionType) {
                                    DialogActionType.Move -> {
                                        viewModel.executeAction(GameAction.Move(place.id))
                                    }
                                    else -> {}
                                }
                                showPlaceDialog = false
                                dialogActionType = null
                            },
                            title = when (dialogActionType) {
                                DialogActionType.Move -> "Select Place to Move To"
                                else -> "Select Place"
                            },
                            filterUnlocked = true,
                            excludePlaceId = if (dialogActionType == DialogActionType.Move) {
                                currentGameState.player.currentLocation
                            } else null,
                            flags = currentGameState.flags
                        )
                    }
                }
            }
        }
        
        uiState is GameUiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading game...")
            }
        }
        
        uiState is GameUiState.Error -> {
            val errorState = uiState as GameUiState.Error
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error: ${errorState.message}",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Error will be handled by showing StartScreen when gameState is null
                    }
                ) {
                    Text("Back to Start")
                }
            }
        }
    }
}

private enum class DialogActionType {
    Investigate, Question, Move, Accuse
}

@OptIn(InternalSerializationApi::class)
@Composable
private fun MainGameScreenContent(
    gameState: com.ee309.detectivegame.domain.model.GameState,
    viewModel: GameViewModel,
    onInvestigateClick: () -> Unit,
    onQuestionClick: () -> Unit,
    onMoveClick: () -> Unit,
    onAccuseClick: () -> Unit
) {
    val currentPlace = gameState.getCurrentLocation()
    val timeRemaining = gameState.timeline.endTime.minutes - gameState.currentTime.minutes
    val timeRemainingHours = timeRemaining / 60
    val timeRemainingMinutes = timeRemaining % 60
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top bar: Time and Location widgets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Time display widget
            TimeDisplayWidget(
                currentTime = gameState.currentTime,
                timeRemaining = "$timeRemainingHours:${String.format("%02d", timeRemainingMinutes)}",
                modifier = Modifier.weight(1f)
            )
            
            // Location display widget
            LocationDisplayWidget(
                locationName = currentPlace?.name ?: "Unknown",
                modifier = Modifier.weight(1f)
            )
        }
        
        // Text display area (showing general game log - can be enhanced later)
        TextDisplay(
            messages = emptyList(), // TODO: Add general game log messages (investigations, movements, etc.)
            modifier = Modifier.weight(1f)
        )
        
        // Inventory/Clue display (expandable)
        InventoryDisplayWidget(
            collectedClues = gameState.player.collectedClues.mapNotNull { clueId ->
                gameState.getClue(clueId)
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Action buttons
        ActionButtons(
            onInvestigateClick = onInvestigateClick,
            onQuestionClick = onQuestionClick,
            onMoveClick = onMoveClick,
            onAccuseClick = onAccuseClick,
            modifier = Modifier.fillMaxWidth(),
            isInvestigateEnabled = gameState.player.currentLocation.isNotEmpty() && 
                gameState.getPlace(gameState.player.currentLocation)?.isUnlocked(gameState.flags) == true,
            isQuestionEnabled = gameState.getCharactersAtLocation(gameState.player.currentLocation).isNotEmpty(),
            isMoveEnabled = gameState.getUnlockedPlaces().size > 1,
            isAccuseEnabled = gameState.getUnlockedCharacters().isNotEmpty()
        )
    }
}

@Composable
private fun TimeDisplayWidget(
    currentTime: com.ee309.detectivegame.domain.model.GameTime,
    timeRemaining: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Time: ${currentTime.format()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Remaining: $timeRemaining",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun LocationDisplayWidget(
    locationName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Location",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = locationName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@InternalSerializationApi
@Composable
private fun InventoryDisplayWidget(
    collectedClues: List<com.ee309.detectivegame.domain.model.Clue>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "Collected Clues (${collectedClues.size})",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (expanded) "▼" else "▶",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                if (collectedClues.isEmpty()) {
                    Text(
                        text = "No clues collected yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                } else {
                    collectedClues.forEach { clue ->
                        Text(
                            text = "• ${clue.name}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
