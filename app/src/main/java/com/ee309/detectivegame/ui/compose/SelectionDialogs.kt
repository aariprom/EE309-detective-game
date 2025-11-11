package com.ee309.detectivegame.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ee309.detectivegame.domain.model.Character
import com.ee309.detectivegame.domain.model.Place
import kotlinx.serialization.InternalSerializationApi

/**
 * Dialog for selecting a character (e.g., for questioning or accusation).
 */
@Composable
fun CharacterSelectionDialog(
    characters: List<Character>,
    onDismiss: () -> Unit,
    onCharacterSelected: (Character) -> Unit,
    title: String = "Select Character",
    filterUnlocked: Boolean = true,
    filterByLocation: String? = null,
    flags: Map<String, Boolean> = emptyMap()
) {
    val filteredCharacters = characters.filter { character ->
        (!filterUnlocked || character.isUnlocked(flags)) &&
        (filterByLocation == null || character.isAtLocation(filterByLocation))
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            if (filteredCharacters.isEmpty()) {
                Text("No characters available.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredCharacters) { character ->
                        CharacterSelectionItem(
                            character = character,
                            onClick = {
                                onCharacterSelected(character)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterSelectionItem(
    character: Character,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (character.traits.isNotEmpty()) {
                    Text(
                        text = character.traits.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Dialog for selecting a place (e.g., for investigation or movement).
 */
@OptIn(InternalSerializationApi::class)
@Composable
fun PlaceSelectionDialog(
    places: List<Place>,
    onDismiss: () -> Unit,
    onPlaceSelected: (Place) -> Unit,
    title: String = "Select Place",
    filterUnlocked: Boolean = true,
    excludePlaceId: String? = null,
    flags: Map<String, Boolean> = emptyMap()
) {
    val filteredPlaces = places.filter { place ->
        (!filterUnlocked || place.isUnlocked(flags)) &&
        (excludePlaceId == null || place.id != excludePlaceId)
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            if (filteredPlaces.isEmpty()) {
                Text("No places available.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredPlaces) { place ->
                        PlaceSelectionItem(
                            place = place,
                            onClick = {
                                onPlaceSelected(place)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(InternalSerializationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun PlaceSelectionItem(
    place: Place,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

