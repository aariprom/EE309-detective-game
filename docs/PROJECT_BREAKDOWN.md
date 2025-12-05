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
- **Status**: ❌ NOT PLANNED
- **Reason**: Not needed - basic validation is sufficient for predefined actions (investigate, question, move, accuse). No free-form actions require advanced validation.

---

## 2. LLM Integration Layer

**Purpose**: Interface with LLM for content generation, conversations, and dynamic responses using **Hybrid Architecture** (upfront structure generation + lazy loading for dynamic content).

**Architecture Overview**:
- **LLM 1 (Initializer)**: Generates complete game structure upfront (characters, places, clues, timeline)
- **LLM 2 (Intro Generator)**: Generates introduction text before game starts
- **LLM 3-4 (Runtime)**: Generate dynamic content on-demand (dialogue, descriptions)
- **Epilogue Generator**: Generates epilogue text on game end
- **Note**: No caching system - prompts are unique per context, making caching inefficient

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
- **Status**: ✅ IMPLEMENTED
- **Description**:
  - **Generate conversations on-demand** when player questions a character
  - Input: character data, player's collected clues, current time, player's question, cooperation level
  - Output: character dialogue response, potential new clues
  - Character personality and knowledge-based responses
  - Context-aware responses (time, player's clues, mental_state)
  - Basic clue extraction from dialogue responses (structured output)

### 2.5 LLM 4: Description Generator (Lazy Loading)
- **Priority**: MEDIUM
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Status**: ✅ IMPLEMENTED
- **Description**:
  - **Generate descriptions on-demand** when player investigates a place or views character
  - Input: place/character data, current time, timeline events, player's clues
  - Output: contextual descriptions (place appearance, character expressions/appearance)
  - Event descriptions based on timeline and player actions
  - Epilogue generation for game endings (separate Epilogue Generator)

### 2.6 LLM 5: Action Handler (Lazy Loading)
- **Status**: ❌ NOT PLANNED
- **Reason**: Free-form action system is too complicated to implement and validate. Game uses predefined actions only (investigate, question, move, accuse).

### 2.7 LLM 6: Component Updater (Lazy Loading)
- **Status**: ❌ NOT PLANNED
- **Reason**: Too complicated. Timeline events are processed with basic logic only (character movement, place changes, flags). No LLM-based component updates needed for current gameplay.

### 2.7 Clue Extraction System
- **Status**: ✅ BASIC VERSION IMPLEMENTED
- **Description**:
  - Basic clue extraction from LLM 3 (Dialogue) responses using structured output
  - Clues are extracted from dialogue responses via JSON schema
  - No enhanced extraction system needed - current implementation is sufficient

### 2.8 LLM Response Caching System
- **Status**: ❌ NOT PLANNED
- **Reason**: Caching is inefficient since prompts are unique per context (different time, different player clues, different questions). No significant cache hit rate expected.

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
- **Status**: ❌ NOT PLANNED
- **Reason**: Free-form action system is not implemented. Game uses predefined actions only.

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
- **Status**: ❌ NOT PLANNED
- **Reason**: Tutorial system is not needed. Game is intuitive enough with predefined actions. Mock data serves as backward compatibility and LLM fallback only.

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
- **Status**: ⚠️ MIGHT IMPLEMENT
- **Description**:
  - Save game functionality using Room database
  - Load game screen with save slots
  - Save game metadata (date, time, progress)
  - Game state serialization already supported (kotlinx.serialization)

---

## 5. Game Logic & Validation System

**Purpose**: Implement game rules, action consequences, and win/lose conditions.

### 5.1 Investigation Action Handler
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM
- **Feasibility**: ✅ POSSIBLE
- **Status**: ✅ IMPLEMENTED
- **Description**:
  - Handle place investigation actions
  - Trigger **LLM 4 (Description Generator)** to generate place description
  - Generate clues based on investigation (direct extraction from available clues)
  - Time consumption calculation
  - Failure conditions (time-based, insufficient prerequisites)

### 5.2 Questioning/Interrogation Handler
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM-HIGH
- **Feasibility**: ✅ POSSIBLE
- **Status**: ✅ IMPLEMENTED
- **Description**:
  - Initiate questioning with character
  - Trigger **LLM 3 (Dialogue Generator)** for conversation flow
  - Extract clues from conversation (via structured output from LLM 3)
  - Cooperation level affecting responses (passed to LLM 3)
  - Time consumption calculation
  - Conversation history tracking

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
- **Status**: ✅ IMPLEMENTED (Simple Version)
- **Description**:
  - Handle player accusation of criminal
  - Simple validation: checks if accused character is criminal (isCriminal flag)
  - Trigger game end condition (win/lose)
  - **Note**: Advanced evidence validation not implemented - simple check is sufficient for current gameplay

### 5.5 Free Action Handler
- **Status**: ❌ NOT PLANNED
- **Reason**: Free-form action system is not implemented. Game uses predefined actions only.

### 5.6 Timeline Event Processor
- **Priority**: HIGH (Critical Path)
- **Difficulty**: MEDIUM-HIGH
- **Feasibility**: ✅ POSSIBLE
- **Status**: ✅ IMPLEMENTED (Basic Version)
- **Description**:
  - Process timeline events based on current time
  - Basic event processing: character movement, place changes, crime events, custom events
  - Updates game state with flags and character locations
  - **Note**: No LLM-based component updates - uses basic logic only. Game is playable without dynamic LLM updates.

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
- **Status**: ✅ IMPLEMENTED (Basic Version)
- **Description**:
  - Update components after each action/time unit
  - Apply timeline events to components (basic logic only)
  - Update character states, place states, clue availability
  - Handle unlock conditions
  - **Note**: No LLM-based component updates - uses basic state management only

---

## Task Summary by Priority

### Critical Path (Completed)
1. ✅ Core Game Engine & State Management (1.1, 1.2, 1.3)
2. ✅ LLM Integration Layer (2.1, 2.2, 2.3, 2.4, 2.5, Epilogue Generator)
3. ✅ Game Content System (3.1, 3.2, 3.3, 3.4, 3.5)
4. ✅ Android UI - Main Components (4.1, 4.2, 4.3, 4.5, 4.6, 4.7, 4.9)
5. ✅ Game Logic - Core Actions (5.1, 5.2, 5.3, 5.4, 5.6, 5.7, 5.8)

### Not Planned / Removed Features
- ❌ Action Validation System (1.4) - Not needed
- ❌ LLM 5: Action Handler (2.6) - Too complicated
- ❌ LLM 6: Component Updater (2.7) - Too complicated
- ❌ LLM Response Caching System (2.8) - Inefficient
- ❌ Enhanced Clue Extraction (2.7) - Basic version sufficient
- ❌ Free Action Input UI (4.4) - Not implementing free actions
- ❌ Free Action Handler (5.5) - Not implementing free actions
- ❌ Tutorial Screen (4.8) - Not needed

### Potential Future Features
- ⚠️ Save/Load System UI (4.10) - Might implement with Room database

---

## Feasibility Notes

### ✅ POSSIBLE
Most tasks are feasible with standard Android development and LLM API integration.

### ⚠️ CHALLENGING (Not Implemented)
- **Action Validation (1.4, 5.5)**: Not implemented - not needed for predefined actions.
- **LLM 5: Action Handler (2.6)**: Not implemented - free-form actions too complicated.
- **LLM 6: Component Updater (2.7)**: Not implemented - basic timeline processing sufficient.

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

## Development Phases (Completed)

### Phase 1: Foundation ✅
- ✅ Core game engine and state management (1.1, 1.2, 1.3)
- ✅ Basic data models (3.1, 3.2, 3.3, 3.4, 3.5)
- ✅ LLM API client setup (2.1)
- ✅ **LLM 1: Initial Content Generator** (2.2) - Upfront generation
- ✅ **LLM 2: Intro Generator** (2.3) - Introduction text generation

### Phase 2: Content System & Runtime LLMs ✅
- ✅ Complete Character, Place, Clue, Timeline systems (3.1, 3.2, 3.3, 3.4)
- ✅ **LLM 3: Dialogue Generator** (2.4) - Lazy loading for conversations
- ✅ **LLM 4: Description Generator** (2.5) - Lazy loading for descriptions
- ✅ **Epilogue Generator** - Game ending text generation
- ✅ Basic clue extraction from dialogue (structured output)

### Phase 3: Game Logic ✅
- ✅ Core action handlers (5.1, 5.2, 5.3, 5.4)
- ✅ Timeline event processing (5.6) - Basic logic only
- ✅ Win/lose conditions (5.7)
- ✅ Component update logic (5.8) - Basic state management

### Phase 4: Android UI ✅
- ✅ Main game screen (4.1, 4.2)
- ✅ Action selection UI (4.3)
- ✅ Conversation UI (4.6) - Integration with LLM 3
- ✅ Character & Place selection UI (4.5)
- ✅ Game Start Configuration (4.7)
- ✅ Game over screens (4.9)

### Potential Future Phase
- ⚠️ Save/Load System (4.10) - Might implement with Room database

