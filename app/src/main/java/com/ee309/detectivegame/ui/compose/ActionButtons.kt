package com.ee309.detectivegame.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ee309.detectivegame.domain.model.GameAction

/**
 * Action selection UI component with predefined action buttons.
 * Provides buttons for Investigate, Question, Move, and Accuse actions.
 */
@Composable
fun ActionButtons(
    onInvestigateClick: () -> Unit,
    onQuestionClick: () -> Unit,
    onMoveClick: () -> Unit,
    onAccuseClick: () -> Unit,
    modifier: Modifier = Modifier,
    isInvestigateEnabled: Boolean = true,
    isQuestionEnabled: Boolean = true,
    isMoveEnabled: Boolean = true,
    isAccuseEnabled: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: Investigate and Question
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = "Investigate",
                icon = Icons.Default.Search,
                onClick = onInvestigateClick,
                enabled = isInvestigateEnabled,
                modifier = Modifier.weight(1f)
            )
            
            ActionButton(
                text = "Question",
                icon = Icons.Default.Info,
                onClick = onQuestionClick,
                enabled = isQuestionEnabled,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Row 2: Move and Accuse
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = "Move",
                icon = Icons.Default.ArrowForward,
                onClick = onMoveClick,
                enabled = isMoveEnabled,
                modifier = Modifier.weight(1f)
            )
            
            ActionButton(
                text = "Accuse",
                icon = Icons.Default.Warning,
                onClick = onAccuseClick,
                enabled = isAccuseEnabled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(text = text)
        }
    }
}

