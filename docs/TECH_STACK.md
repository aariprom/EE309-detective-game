# Tech Stack

## Overview

This document describes the tech stack used in the detective game project. All technologies listed here are actively used in the codebase.

---

## Core Stack

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Language** | Kotlin | 1.9.22 | Official Android language, modern, null-safe |
| **UI Framework** | Jetpack Compose | BOM 2023.10.01 | Modern, declarative UI framework |
| **Navigation** | Navigation Compose | 2.7.5 | Compose navigation library |
| **Architecture** | MVVM | - | Model-View-ViewModel pattern |
| **DI** | Hilt | 2.48 | Dependency injection (with KSP) |
| **State Management** | StateFlow | - | Kotlin-first state management |
| **Networking** | Retrofit + OkHttp | 2.9.0 / 4.12.0 | HTTP client for LLM API calls |
| **JSON (Retrofit)** | Gson | 2.9.0 | JSON converter for Retrofit |
| **JSON (Domain)** | kotlinx.serialization | 1.6.0 | JSON serialization for domain models |
| **Database** | Room | 2.6.1 | Local database for persistence |
| **Code Generation** | KSP | 1.9.22-1.0.16 | Kotlin Symbol Processing (replaces kapt) |
| **Async** | Kotlin Coroutines | 1.7.3 | Asynchronous operations |
| **LLM Provider** | Upstage API | Solar LLM | LLM provider for content generation |

---

## Detailed Breakdown

### 1. Android Development

#### 1.1 Programming Language

**Kotlin 1.9.22**

- Official Android language (Google recommended)
- Modern, concise, null-safe
- Excellent coroutines support for async operations
- Strong community and documentation

#### 1.2 UI Framework

**Jetpack Compose (BOM 2023.10.01)**

- Modern declarative UI framework
- Less boilerplate code
- Better state management integration
- Easier to build dynamic UIs
- Growing ecosystem and community support

**Key Dependencies:**
- `androidx.compose.ui:ui`
- `androidx.compose.material3:material3`
- `androidx.activity:activity-compose`

#### 1.3 Navigation

**Navigation Compose 2.7.5**

- Compose-first navigation library
- Type-safe navigation
- Deep linking support
- Integrated with Hilt for dependency injection

**Dependency:**
- `androidx.navigation:navigation-compose`
- `androidx.hilt:hilt-navigation-compose`

---

### 2. Architecture & Dependency Injection

#### 2.1 Architecture Pattern

**MVVM (Model-View-ViewModel)**

- Well-established Android pattern
- Good separation of concerns
- Easy to test
- Works well with Jetpack Compose
- StateFlow integration

**Structure:**
```
View (Compose UI)
  ↓
ViewModel (State management)
  ↓
Repository (Data layer)
  ↓
DataSource (LLM API, Local DB)
```

#### 2.2 Dependency Injection

**Hilt 2.48 (with KSP)**

- Official Google solution
- Built on Dagger
- Easy to use
- Good documentation
- Compose integration
- Uses KSP (Kotlin Symbol Processing) instead of kapt

**Dependencies:**
- `com.google.dagger:hilt-android`
- `com.google.dagger:hilt-android-compiler` (via KSP)
- `androidx.hilt:hilt-navigation-compose`

**Note**: We use **KSP** (Kotlin Symbol Processing) instead of kapt for code generation. KSP is faster and more Kotlin-friendly.

---

### 3. State Management

#### 3.1 State Management Library

**StateFlow**

- Kotlin coroutines integration
- Type-safe
- Cold streams (more efficient)
- Works well with Compose
- Official Android solution

**Usage:**
```kotlin
class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
}
```

---

### 4. Networking & API

#### 4.1 HTTP Client

**Retrofit 2.9.0 + OkHttp 4.12.0**

- Industry standard for Android networking
- Type-safe API definitions
- Excellent error handling
- Easy to add interceptors (logging, retry, caching)
- Coroutines support
- Well-documented

**Dependencies:**
- `com.squareup.retrofit2:retrofit`
- `com.squareup.okhttp3:okhttp`
- `com.squareup.okhttp3:logging-interceptor`

#### 4.2 JSON Serialization

**Gson 2.9.0 (for Retrofit)**

- Used as Retrofit converter for API responses
- Simple, mature library
- Works well with Retrofit

**Dependency:**
- `com.squareup.retrofit2:converter-gson`

**kotlinx.serialization 1.6.0 (for Domain Models)**

- Used for serializing/deserializing domain models (GameState, etc.)
- Kotlin-first
- Compile-time code generation
- Type-safe
- Good performance

**Dependency:**
- `org.jetbrains.kotlinx:kotlinx-serialization-json`

**Note**: We use **Gson** for Retrofit API responses and **kotlinx.serialization** for domain model serialization (e.g., GameState persistence).

---

### 5. Data Persistence

#### 5.1 Local Database

**Room 2.6.1 (with KSP)**

- Official Android solution
- Type-safe SQL queries
- Compile-time validation
- Good for complex data structures
- Excellent for game state persistence

**Dependencies:**
- `androidx.room:room-runtime`
- `androidx.room:room-ktx`
- `androidx.room:room-compiler` (via KSP)

**Use Cases:**
- Save/load game state (if implemented)
- Store game history (if implemented)
- Persist player progress (if implemented)

**Note**: LLM response caching is not implemented - prompts are unique per context, making caching inefficient.

---

### 6. Asynchronous Operations

#### 6.1 Coroutines

**Kotlin Coroutines 1.7.3**

- Official Kotlin solution
- Excellent for async operations
- Works well with LLM API calls
- StateFlow integration
- Easy cancellation

**Dependency:**
- `org.jetbrains.kotlinx:kotlinx-coroutines-android`

**Key Components:**
- `viewModelScope` for ViewModel operations
- `CoroutineScope` for repository operations
- `Flow` for reactive data streams

**Example:**
```kotlin
fun generateContent() {
    viewModelScope.launch {
        _state.value = Result.Loading
        try {
            val content = llmRepository.generateInitialContent()
            _state.value = Result.Success(content)
        } catch (e: Exception) {
            _state.value = Result.Error(e)
        }
    }
}
```

---

### 7. LLM Integration

#### 7.1 LLM Provider

**Upstage API (Solar LLM family)**

- Partnership support
- Good performance and reliability
- Multi-language support (English, Korean, Japanese)
- Structured output support
- Fast response times
- Cost-effective (partnership pricing)

**Integration**: Direct API via Retrofit

**Fallback Options**: OpenAI API, Anthropic Claude API (if needed)

---

### 8. Testing

#### 8.1 Unit Testing

**JUnit 4.13.2**

- Testing framework
- Standard Android testing library

**Dependency:**
- `junit:junit`

#### 8.2 Android Testing

**AndroidX Test + Espresso**

- Android instrumentation tests
- UI testing with Espresso

**Dependencies:**
- `androidx.test.ext:junit`
- `androidx.test.espresso:espresso-core`

#### 8.3 Compose UI Testing

**Compose UI Test**

- Compose-specific UI testing
- Test Compose components

**Dependencies:**
- `androidx.compose.ui:ui-test-junit4`
- `androidx.compose.ui:ui-test-manifest`
- `androidx.compose.ui:ui-tooling` (debug)

---

### 9. Build Tools

#### 9.1 Build System

**Gradle (Kotlin DSL)**

- Official Android build system
- Kotlin DSL for type-safe build scripts
- Good dependency management

#### 9.2 Code Generation

**KSP (Kotlin Symbol Processing) 1.9.22-1.0.16**

- Kotlin-first code generation
- Faster than kapt
- Better Kotlin support
- Used for Hilt and Room code generation

**Note**: We use **KSP** instead of kapt. KSP is the modern replacement for kapt and is faster and more Kotlin-friendly.

---

## Project Structure

```
app/src/main/java/com/ee309/detectivegame/
├── data/
│   ├── local/          # Room database
│   ├── remote/         # Retrofit API
│   └── repository/     # Repositories
├── domain/
│   ├── model/          # Domain models (with kotlinx.serialization)
│   └── usecase/        # Use cases
├── ui/
│   ├── compose/        # Compose screens
│   └── theme/          # App theme
├── presentation/
│   ├── viewmodel/      # ViewModels
│   └── state/          # UI state classes
└── llm/
    ├── client/         # LLM API client (Retrofit)
    ├── config/         # LLM tasks, prompts, schemas
    ├── data/           # LLM repository
    └── model/          # LLM request/response models
```

---

## Key Dependencies Summary

```kotlin
// Core Android
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
implementation("androidx.activity:activity-compose:1.8.1")

// Compose
implementation(platform("androidx.compose:compose-bom:2023.10.01"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.5")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

// Hilt
implementation("com.google.dagger:hilt-android:2.48")
ksp("com.google.dagger:hilt-android-compiler:2.48")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Testing
testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

---

## Cost Considerations

### LLM API Costs (Estimated)

**Upstage API (Solar LLM)**:
- **Cost**: Partnership pricing (check with Upstage for rates)
- **Estimated**: Similar to OpenAI (~$0.50-1.40 per game session)
- **Note**: Check Upstage API documentation for current pricing model

**Fallback**: OpenAI GPT-3.5-turbo if needed
- Input: $0.50 per 1M tokens
- Output: $1.50 per 1M tokens
- Estimated: $0.50-1.40 per game session

---

## Rationale

- **Modern but Stable**: Uses current Android best practices
- **Well-Documented**: All technologies have excellent documentation
- **Scalable**: Can grow with the project
- **Partnership Support**: Upstage API provides startup support
- **Cost-Effective**: Partnership pricing with Upstage
- **Testable**: Easy to write unit and UI tests
- **Maintainable**: Clear separation of concerns

---

*This tech stack reflects what's actually used in the project. All dependencies are defined in `app/build.gradle.kts`.*
