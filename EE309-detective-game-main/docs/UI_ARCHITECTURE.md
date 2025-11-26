# UI Architecture Guide

## 📋 Table of Contents

1. [Overview](#overview)
2. [What is `uiState`?](#what-is-uistate)
3. [How UI Gets Info from Model](#how-ui-gets-info-from-model)
4. [How It Displays on Screen](#how-it-displays-on-screen)
5. [Complete Data Flow Example](#complete-data-flow-example)
6. [Key Concepts Summary](#key-concepts-summary)
7. [Why This Architecture?](#why-this-architecture)

---

## Overview

This app uses the **MVVM (Model-View-ViewModel)** architecture pattern with **Jetpack Compose** for the UI and **StateFlow** for reactive state management.

### Architecture Diagram

```
┌─────────────┐         ┌────────────────┐         ┌────────────┐
│   Model     │         │  ViewModel     │         │   View     │
│ (GameState) │◄────────│ (GameViewModel)│◄─────── │(GameScreen)│
└─────────────┘         └────────────────┘         └────────────┘
     Data              Business Logic              UI Display
```

**Key Components:**
- **Model** (`GameState`) - Contains all game data (characters, places, clues, time, etc.)
- **ViewModel** (`GameViewModel`) - Manages game logic and state
- **View** (`GameScreen`) - Displays the UI to the user

---

## What is `uiState`?

`uiState` is a **StateFlow** that holds the current UI state. It can be one of three states:

1. **Loading** - Game is loading
2. **Success** - Game is running (contains `GameState`)
3. **Error** - Something went wrong (contains error message)

### Definition

```kotlin
sealed class GameUiState {
    data object Loading : GameUiState()
    data class Success(val gameState: GameState) : GameUiState()
    data class Error(val message: String) : GameUiState()
}
```

**Why use a sealed class?**
- Type-safe: Compiler ensures you handle all possible states
- Clear: Easy to see what states are possible
- Exhaustive: `when` expressions must handle all cases

---

## How UI Gets Info from Model

The UI doesn't directly access the model. Instead, it goes through the ViewModel using a reactive data flow.

### Step 1: ViewModel Holds the State

```kotlin
class GameViewModel : ViewModel() {
    // Private, writable StateFlow (only ViewModel can change it)
    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    
    // Public, read-only StateFlow (UI can observe it)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    // Also holds the actual game state separately
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
}
```

**Key Points:**
- `_uiState` is **private** and **mutable** - only ViewModel can update it
- `uiState` is **public** and **read-only** - UI can observe but not modify
- **StateFlow** is a reactive stream that emits values over time
- When the value changes, all observers are automatically notified

### Step 2: ViewModel Updates State When Game Changes

```kotlin
private fun updateGameState(newState: GameState) {
    _gameState.value = newState
    _uiState.value = GameUiState.Success(newState)
}
```

**What happens:**
1. ViewModel updates `_gameState` with the new `GameState`
2. ViewModel updates `_uiState` to `Success(newState)`
3. StateFlow automatically notifies all observers (the UI)

### Step 3: UI Observes the State

```kotlin
@Composable
fun GameScreen(viewModel: GameViewModel = hiltViewModel()) {
    // Convert StateFlow to Compose State
    val uiState by viewModel.uiState.collectAsState()
    
    // UI automatically recomposes when uiState changes
    when (uiState) {
        // ... display based on state
    }
}
```

**What `collectAsState()` does:**
- Converts the StateFlow into a Compose State
- The `by` delegate makes `uiState` reactive
- When `viewModel.uiState` changes, Compose automatically recomposes the UI

### Step 4: UI Displays Based on State

```kotlin
when (uiState) {
    is GameUiState.Loading -> {
        CircularProgressIndicator()
        Text("Loading game...")
    }
    
    is GameUiState.Success -> {
        val gameState = uiState.gameState
        Text("Current Phase: ${gameState.phase}")
        Text("Time: ${gameState.currentTime.format()}")
    }
    
    is GameUiState.Error -> {
        Text("Error: ${uiState.message}")
    }
}
```

**What happens:**
- UI checks the current state using `when`
- Shows different content based on the state:
  - **Loading** → Shows spinner
  - **Success** → Shows game info (reads `gameState.phase`, `gameState.currentTime`, etc.)
  - **Error** → Shows error message

---

## How It Displays on Screen

### Jetpack Compose (Declarative UI)

Jetpack Compose is Android's modern UI toolkit. Instead of writing XML layouts, you write Kotlin functions that describe the UI.

### Entry Point

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DetectiveGameTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GameScreen()
                }
            }
        }
    }
}
```

**What happens:**
1. `MainActivity.onCreate()` is called when the app starts
2. `setContent { ... }` sets the Compose UI tree
3. `GameScreen()` is a Composable function that builds the UI
4. Compose renders the UI to the screen

### How Compose Renders

**Composable functions** are special Kotlin functions that describe UI:
- `Text()` - Displays text
- `Button()` - Displays a button
- `Column()` - Arranges items vertically
- `Row()` - Arranges items horizontally

**Recomposition:**
- When state changes, Compose automatically **recomposes** (re-renders) only the parts that changed
- This is efficient - it doesn't rebuild the entire UI
- Only the affected Composable functions are called again

**Example:**
```kotlin
@Composable
fun GameScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    // This Text recomposes when uiState changes
    Text("Phase: ${uiState.gameState.phase}")
}
```

When `uiState` changes:
1. `collectAsState()` detects the change
2. Compose marks `GameScreen()` for recomposition
3. Compose calls `GameScreen()` again with the new state
4. UI updates automatically

---

## Complete Data Flow Example

### User Clicks "Start New Game" Button

```
1. User clicks button
   ↓
2. onClick = { viewModel.startNewGame("") }
   ↓
3. GameViewModel.startNewGame() runs
   ↓
4. ViewModel updates: _uiState.value = GameUiState.Loading
   ↓
5. StateFlow emits new value
   ↓
6. GameScreen's collectAsState() detects change
   ↓
7. Compose recomposes GameScreen()
   ↓
8. UI shows "Loading game..." spinner
   ↓
9. ViewModel creates game: _uiState.value = GameUiState.Success(gameState)
   ↓
10. StateFlow emits new value
   ↓
11. Compose recomposes again
   ↓
12. UI shows game info (phase, time, etc.)
```

### User Performs an Action (e.g., Investigate)

```
1. User clicks "Investigate" button
   ↓
2. onClick = { viewModel.executeAction(GameAction.Investigate(placeId)) }
   ↓
3. GameViewModel.executeAction() runs
   ↓
4. ViewModel validates action
   ↓
5. ViewModel calls handleInvestigation()
   ↓
6. ViewModel creates new GameState with updated time
   ↓
7. ViewModel calls updateGameState(newState)
   ↓
8. ViewModel updates: _uiState.value = GameUiState.Success(newState)
   ↓
9. StateFlow emits new value
   ↓
10. Compose recomposes GameScreen()
   ↓
11. UI shows updated game info (new time, new clues, etc.)
```

---

## Key Concepts Summary

| Concept | What it is | Why it's used |
|---------|------------|---------------|
| **StateFlow** | Reactive stream that holds a value | Automatically notifies UI when data changes |
| **MutableStateFlow** | Writable StateFlow (private in ViewModel) | ViewModel can update it |
| **StateFlow** | Read-only StateFlow (public) | UI can observe but not modify |
| **collectAsState()** | Converts Flow to Compose State | Makes Flow reactive in Compose |
| **GameUiState** | Sealed class for UI states | Type-safe way to represent Loading/Success/Error |
| **GameState** | Domain model (actual game data) | Contains all game information (characters, places, time, etc.) |
| **Recomposition** | Compose re-renders UI when state changes | UI automatically updates without manual refresh |
| **Composable** | Function that describes UI | Declarative way to build UI |
| **ViewModel** | Manages UI state and business logic | Separates UI from business logic |
| **MVVM** | Model-View-ViewModel pattern | Clean architecture pattern |

---

## Why This Architecture?

### 1. **Separation of Concerns**
- **UI** doesn't know about business logic
- **ViewModel** doesn't know about UI details
- **Model** is independent of both

### 2. **Reactive Updates**
- UI automatically updates when state changes
- No manual refresh needed
- Efficient - only changed parts recompose

### 3. **Testability**
- ViewModel can be tested without UI
- Business logic is isolated
- Easy to write unit tests

### 4. **Lifecycle-Aware**
- ViewModel survives configuration changes (screen rotation)
- State is preserved automatically
- No manual state saving/restoring needed

### 5. **Type Safety**
- Sealed classes ensure all states are handled
- Compiler catches missing cases
- Less runtime errors

---

## Code References

### GameUiState Definition

```8:12:app/src/main/java/com/ee309/detectivegame/presentation/state/GameUiState.kt
sealed class GameUiState {
    data object Loading : GameUiState()
    data class Success(val gameState: GameState) : GameUiState()
    data class Error(val message: String) : GameUiState()
}
```

### ViewModel State Management

```17:21:app/src/main/java/com/ee309/detectivegame/presentation/viewmodel/GameViewModel.kt
    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
```

### ViewModel State Update

```130:133:app/src/main/java/com/ee309/detectivegame/presentation/viewmodel/GameViewModel.kt
    private fun updateGameState(newState: GameState) {
        _gameState.value = newState
        _uiState.value = GameUiState.Success(newState)
    }
```

### UI State Observation

```19:19:app/src/main/java/com/ee309/detectivegame/ui/compose/GameScreen.kt
    val uiState by viewModel.uiState.collectAsState()
```

### UI State Display

```28:58:app/src/main/java/com/ee309/detectivegame/ui/compose/GameScreen.kt
        when (uiState) {
            is GameUiState.Loading -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading game...")
            }
            
            is GameUiState.Success -> {
                val successState = uiState as GameUiState.Success
                val gameState = successState.gameState
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
                val errorState = uiState as GameUiState.Error
                Text(
                    text = "Error: ${errorState.message}",
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
```

### MainActivity Entry Point

```18:27:app/src/main/java/com/ee309/detectivegame/MainActivity.kt
        setContent {
            DetectiveGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen()
                }
            }
        }
```

---

## Summary

**The Complete Flow:**

1. **ViewModel** holds `uiState` (StateFlow)
2. **ViewModel** updates `uiState` when game changes
3. **UI** observes `uiState` with `collectAsState()`
4. **UI** displays different content based on `uiState` (Loading/Success/Error)
5. **Compose** renders the UI and recomposes when state changes

**Key Takeaway:**
The UI doesn't directly access `GameState` - it goes through `GameUiState`, which wraps it and adds Loading/Error states. This makes the UI more robust and easier to handle edge cases.

---

## Related Documentation

- [GETTING_STARTED.md](./GETTING_STARTED.md) - Basic setup and project overview
- [TECH_STACK.md](./TECH_STACK.md) - Technologies used in this project
- [PROJECT_BREAKDOWN.md](./PROJECT_BREAKDOWN.md) - Detailed project tasks and structure

