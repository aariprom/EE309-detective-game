# EE309 Detective Game - Project Breakdown

## Overview
This document breaks down the Android detective game project into manageable modules and tasks with priority, difficulty, and feasibility assessments.

---

## 1. Core Game Engine & State Management

**Purpose**: Foundation for game state, time management, and game flow control.

### 1.1 Game State Data Models
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**: 
  - Create data classes/models for: GameState, Player, Time, Flags
  - Implement serialization/deserialization for state persistence
  - Define game phase enum (Tutorial, Introduction, Investigation, GameOver)

### 1.2 Time Management System
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Implement time unit system (5-minute minimum unit)
  - Time progression based on actions
  - Time limit tracking and enforcement
  - Time-based event triggers

### 1.3 Game Flow Controller
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Main game loop controller
  - Phase transitions (Start → Tutorial → Introduction → Investigation → End)
  - Action validation and execution pipeline
  - State persistence (save/load game)

### 1.4 Action Validation System
- **Priority**: HIGH (Critical Path)
- **Difficulty**: HIGH
- **Feasibility**: ⚠️ CHALLENGING (Requires strong LLM validation)
- **Description**:
  - Validate user actions (investigation, questioning, movement, free actions)
  - Prevent invalid/impossible actions
  - Context-aware validation (time, location, prerequisites)

---

## 2. LLM Integration Layer

**Purpose**: Interface with LLM for content generation, conversations, and dynamic responses using **Hybrid Architecture** (upfront structure generation + lazy loading for dynamic content).

**Architecture Overview**:
- **LLM 1 (Initializer)**: Generates complete game structure upfront (characters, places, clues, timeline)
- **LLM 2 (Intro Generator)**: Generates introduction text before game starts
- **LLM 3-4 (Runtime)**: Generate dynamic content on-demand (dialogue, descriptions)
- **Caching**: Cache generated content to improve performance and reduce costs

### 2.1 LLM API Client
- **Priority**: HIGH (Critical Path)
- **Difficulty**: LOW-MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Set up LLM API client (OpenAI, Anthropic, or local model)
  - API key management and secure storage
  - Request/response handling with error handling
  - Rate limiting and retry logic
  - Support for multiple LLM instances (different models for different purposes)

### 2.2 LLM 1: Initial Content Generator (Upfront Generation)
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - **Single comprehensive LLM call** at game start
  - Generate complete game structure from user input/keywords:
    - **Characters**: Full details (name, traits, is_criminal, known_clues, mental_state, items, unlock_conditions)
    - **Places**: Full details (name, traits, available_clues, items, connections, unlock_conditions)
    - **Clues**: Complete structured data (who, what, when, where, why, unlock_conditions)
    - **Timeline**: Complete event schedule with structured data
  - Ensure specificity and avoid generic responses
  - Use structured output (JSON schema, function calling) for reliable parsing
  - **NOT** generating: dialogue, detailed descriptions (these are lazy-loaded)

### 2.3 LLM 2: Intro Generator (Initial Generation)
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - **Single LLM call** after LLM 1 generates GameState
  - Generate compelling, spoiler-free introduction text
  - Input: Public information from GameState (title, description, characters, places, timeline)
  - Output: Introduction text (3-7 paragraphs) in JSON format
  - Shown to player before game starts
  - No spoilers (isCriminal, hidden clues excluded)

### 2.4 LLM 3: Dialogue Generator (Lazy Loading)
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM-HIGH
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - **Generate conversations on-demand** when player questions a character
  - Input: character data, player's collected clues, current time, player's question, cooperation level
  - Output: character dialogue response, potential new clues
  - Character personality and knowledge-based responses
  - Context-aware responses (time, player's clues, mental_state)
  - **Caching**: Cache dialogue for same character + same context (time, player clues)
  - Extract clues from conversations for clue system

### 2.5 LLM 4: Description Generator (Lazy Loading)
- **Priority**: MEDIUM
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - **Generate descriptions on-demand** when player investigates a place or views character
  - Input: place/character data, current time, timeline events, player's clues
  - Output: contextual descriptions (place appearance, character expressions/appearance)
  - Event descriptions based on timeline and player actions
  - Epilogue generation for game endings
  - **Caching**: Cache descriptions for same place/character + same time state

### 2.6 Clue Extraction System
- **Priority**: MEDIUM
- **Difficulty**: MEDIUM-HIGH
- **Feasibility**: ⚠️ CHALLENGING (Requires structured output parsing)
- **Description**:
  - Parse LLM responses to extract structured clues
  - Used by LLM 3 (Dialogue) and LLM 4 (Descriptions)
  - Convert narrative output to clue objects (who, what, when, where, why)
  - Validate clue format and completeness
  - Use structured output formats (JSON, function calling) when possible

### 2.8 LLM Response Caching System
- **Priority**: MEDIUM
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Cache generated dialogue, descriptions, and action outcomes
  - Cache key: Character/Place + Context (time, player clues, game state)
  - Cache invalidation: When context changes significantly (time advances, new clues found)
  - Storage: In-memory for session, persistent for save games
  - Reduce API costs and improve response times
  - Cache hit/miss statistics for optimization

---

## 3. Game Content System

**Purpose**: Manage game entities (characters, places, clues, timeline).

### 3.1 Character Management System
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Character data model (name, traits, is_criminal, known_clues, mental_state, hidden, unlock_conditions, items)
  - Character state tracking
  - Character location management
  - Character unlock/reveal system

### 3.2 Place Management System
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Place data model (name, traits, available_clues, hidden, unlock_conditions, items, current_characters)
  - Place-to-place movement system
  - Distance-based time calculation for movement
  - Place unlock system

### 3.3 Clue Management System
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Clue data model (who, whom, time, place, content, unlock_conditions)
  - Clue discovery/unlock system
  - Clue collection and inventory
  - Clue dependency tracking

### 3.4 Timeline System
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM-HIGH
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Timeline data structure (past events, future events)
  - Event scheduling and triggering
  - Character movement tracking in timeline
  - Timeline-based component updates

### 3.5 Flag System
- **Priority**: MEDIUM
- **Difficulty**: LOW-MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Track game flags (found clues, player tools, selected choices, current location)
  - Flag-based condition checking
  - Flag persistence

---

## 4. Android UI/UX Components

**Purpose**: User interface and interaction components for Android app.

### 4.1 Main Game Screen Layout
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Main game view layout
  - Time display widget
  - Current location display
  - Player inventory/clue display area

### 4.2 Text Display System
- **Priority**: HIGH (Critical Path)
- **Difficulty**: LOW-MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Scrollable text view for game narrative
  - Typing animation for text display
  - Rich text formatting support
  - Text history/view log

### 4.3 Action Selection UI
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Predefined action buttons (Investigate, Question, Move, Accuse)
  - Context-sensitive action menu
  - Action confirmation dialogs

### 4.4 Free Action Input System
- **Priority**: MEDIUM
- **Difficulty**: MEDIUM-HIGH
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Text input field for free-form actions
  - Input validation feedback
  - Action suggestions/autocomplete
  - Input history

### 4.5 Character & Place Selection UI
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Character selection for questioning
  - Place selection for investigation/movement
  - Character/place information display
  - Visual indicators for available actions

### 4.6 Conversation/Interrogation UI
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Chat-like interface for questioning
  - Character portrait/name display
  - Message bubbles for dialogue
  - Loading indicators during LLM response

### 4.7 Game Start & Configuration Screen
- **Priority**: MEDIUM
- **Difficulty**: LOW-MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Game content input screen (keywords/story)
  - Difficulty settings (cooperation level, criminal behavior, clue requirements)
  - Game start button
  - Settings persistence

### 4.8 Tutorial Screen
- **Priority**: MEDIUM
- **Difficulty**: LOW
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Tutorial content display
  - Step-by-step guidance
  - Skip option

### 4.9 Game Over Screen
- **Priority**: MEDIUM
- **Difficulty**: LOW-MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Win/lose screen display
  - Epilogue display
  - Restart/new game options
  - Game statistics

### 4.10 Save/Load System UI
- **Priority**: LOW
- **Difficulty**: LOW-MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Save game functionality
  - Load game screen with save slots
  - Save game metadata (date, time, progress)

---

## 5. Game Logic & Validation System

**Purpose**: Implement game rules, action consequences, and win/lose conditions.

### 5.1 Investigation Action Handler
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Handle place investigation actions
  - Trigger **LLM 4 (Description Generator)** to generate place description
  - Generate clues based on investigation (via LLM 4 or direct extraction)
  - Check cache before calling LLM (same place + same time state)
  - Time consumption calculation
  - Failure conditions (time-based, insufficient prerequisites)

### 5.2 Questioning/Interrogation Handler
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM-HIGH
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Initiate questioning with character
  - Trigger **LLM 3 (Dialogue Generator)** for conversation flow
  - Check cache before calling LLM (same character + same context)
  - Extract clues from conversation (via LLM 3 or clue extraction system)
  - Cooperation level affecting responses (passed to LLM 3)
  - Time consumption calculation

### 5.3 Movement Action Handler
- **Priority**: HIGH (Critical Path)
- **Difficulty**: LOW-MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Handle location movement
  - Distance-based time calculation
  - Location unlock checking
  - Update current location state

### 5.4 Accusation/Accuse Handler
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM-HIGH
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Handle player accusation of criminal
  - Evidence validation system
  - Check if sufficient evidence exists
  - Trigger game end condition (win/lose)

### 5.5 Free Action Handler
- **Priority**: MEDIUM
- **Difficulty**: HIGH
- **Feasibility**: ⚠️ CHALLENGING (Requires strong validation)
- **Description**:
  - Handle free-form player actions
  - Action validation handled by game logic
  - LLM-based action validation (feasible/not feasible)
  - Strong validation to prevent game-breaking actions
  - Execute valid actions (e.g., breaking locked door, hiding)
  - Generate narrative for action outcomes (via LLM 4)
  - Time/effect calculation

### 5.6 Timeline Event Processor
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM-HIGH
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Process timeline events based on current time
  - Trigger character actions (evidence destruction, movement)
  - Update component states directly via game logic
  - Generate narrative for timeline events

### 5.7 Win/Lose Condition Checker
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Check game end conditions:
    - Successful accusation with sufficient evidence
    - Time limit exceeded
    - Player death (special condition)
    - Criminal confession (special condition)
  - Trigger appropriate ending

### 5.8 Component Update Logic
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Description**:
  - Update components after each action/time unit
  - Apply timeline events to components directly via game logic
  - Update character states, place states, clue availability
  - Handle unlock conditions
  - Cache invalidation when components are updated

---

## Task Summary by Priority

### Critical Path (Must Complete)
1. Core Game Engine & State Management (1.1, 1.2, 1.3)
2. LLM Integration Layer (2.1, 2.2, 2.3, 2.4, 2.5, 2.8)
3. Game Content System (3.1, 3.2, 3.3, 3.4)
4. Android UI - Main Components (4.1, 4.2, 4.3, 4.5, 4.6)
5. Game Logic - Core Actions (5.1, 5.2, 5.3, 5.4, 5.6, 5.7, 5.8)

### High Priority (Important for Full Experience)
- Action Validation System (1.4)
- LLM 2: Intro Generator (2.3)
- LLM 4: Description Generator (2.5)
- Clue Extraction System (2.8)
- Flag System (3.5)
- Free Action Input UI (4.4)
- Game Start Configuration (4.7)
- Free Action Handler (5.5)

### Medium Priority (Enhancement Features)
- Tutorial Screen (4.8)
- Game Over Screen (4.9)

### Low Priority (Nice to Have)
- Save/Load System UI (4.10)

---

## Feasibility Notes

### ✅ POSSIBLE
Most tasks are feasible with standard Android development and LLM API integration.

### ⚠️ CHALLENGING
- **Action Validation (1.4, 5.5)**: Requires sophisticated validation logic to prevent game-breaking actions while maintaining flexibility.
- **Clue Extraction (2.6)**: Requires structured output from LLM or robust parsing. May need to use function calling or JSON mode.

### 🔧 Recommended Tech Stack

See **[TECH_STACK.md](./TECH_STACK.md)** for comprehensive tech stack recommendations.

**Quick Summary**:
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM + Hilt (DI)
- **State Management**: StateFlow
- **Networking**: Retrofit + OkHttp
- **Database**: Room Database
- **LLM**: OpenAI API (GPT-3.5-turbo/GPT-4)
- **JSON**: kotlinx.serialization
- **Async**: Kotlin Coroutines

---

## Development Phases Recommendation

### Phase 1: Foundation (Weeks 1-2)
- Core game engine and state management (1.1, 1.2, 1.3)
- Basic data models (3.1, 3.2, 3.3, 3.4)
- LLM API client setup (2.1)
- **LLM 1: Initial Content Generator** (2.2) - Upfront generation
- **LLM 2: Intro Generator** (2.3) - Introduction text generation

### Phase 2: Content System & Runtime LLMs (Weeks 2-3)
- Complete Character, Place, Clue, Timeline systems (3.1, 3.2, 3.3, 3.4)
- **LLM 3: Dialogue Generator** (2.4) - Lazy loading for conversations
- **LLM 4: Description Generator** (2.5) - Lazy loading for descriptions
- **LLM Response Caching System** (2.8) - Performance optimization
- Clue Extraction System (2.7)

### Phase 3: Game Logic (Weeks 3-4)
- Core action handlers (5.1, 5.2, 5.3, 5.4)
- Timeline event processing (5.6)
- Win/lose conditions (5.7)
- Component update logic (5.8)

### Phase 4: Android UI (Weeks 4-5)
- Main game screen (4.1, 4.2)
- Action selection UI (4.3)
- Conversation UI (4.6) - Integration with LLM 2
- Character & Place selection UI (4.5)
- Basic navigation

### Phase 5: Advanced Features (Weeks 5-6)
- Free Action Input UI (4.4)
- Action Validation System (1.4)
- Game Start Configuration (4.7)
- Tutorial (4.8)
- Game over screens (4.9)
- Save/load (if time permits) (4.10)

