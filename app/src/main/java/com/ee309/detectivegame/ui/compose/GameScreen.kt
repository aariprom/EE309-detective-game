package com.ee309.detectivegame.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ee309.detectivegame.presentation.state.GameUiState
import com.ee309.detectivegame.presentation.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState) {
            is GameUiState.Loading -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading game...")
            }
            
            is GameUiState.Success -> {
                val gameState = uiState.gameState
                Text(
                    text = "Detective Game",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Current Phase: ${gameState.phase}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Time: ${gameState.currentTime.format()}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.startNewGame("") }
                ) {
                    Text("Start New Game")
                }
            }
            
            is GameUiState.Error -> {
                Text(
                    text = "Error: ${uiState.message}",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.startNewGame("") }
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

