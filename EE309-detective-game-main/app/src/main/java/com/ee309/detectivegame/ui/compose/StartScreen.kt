package com.ee309.detectivegame.ui.compose

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
import com.ee309.detectivegame.presentation.state.GameUiState
import com.ee309.detectivegame.presentation.viewmodel.GameViewModel

@Composable
fun StartScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    var keywords by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Detective Game",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Subtitle
        Text(
            text = "Solve mysteries and catch the culprit",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Keyword Input Field
        OutlinedTextField(
            value = keywords,
            onValueChange = { keywords = it },
            label = { Text("Keywords (Optional)") },
            placeholder = { Text("Enter keywords for game generation...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Start Button or Loading/Error State
        when {
            // Show loading when starting a game
            uiState is GameUiState.Loading -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Starting game...")
            }
            
            // Show error only if message is not empty (initial state has empty error)
            uiState is GameUiState.Error -> {
                val errorState = uiState as GameUiState.Error
                if (errorState.message.isNotEmpty()) {
                    Text(
                        text = "Error: ${errorState.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = { viewModel.startNewGame(keywords) },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Retry")
                    }
                } else {
                    // Empty error message means initial state - show start button
                    Button(
                        onClick = { viewModel.startNewGame(keywords) },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Start New Game")
                    }
                }
            }
            
            else -> {
                // Default: show start button (for Success state or other states)
                Button(
                    onClick = { viewModel.startNewGame(keywords) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Start New Game")
                }
            }
        }
    }
}