# Tech Stack Recommendation

## Overview

This document provides detailed tech stack recommendations for the Android detective game project, with alternatives and rationale for each choice.

---

## 1. Android Development

### 1.1 Programming Language

**Recommended: Kotlin**

- **Rationale**:
  - Official Android language (Google recommended)
  - Modern, concise, null-safe
  - Excellent coroutines support for async operations
  - Interoperable with Java (if needed)
  - Strong community and documentation

**Alternative**: Java
- More verbose, less null-safe
- Still supported but not recommended for new projects

### 1.2 UI Framework

**Option A: Jetpack Compose (Recommended for Modern Approach)**

- **Pros**:
  - Modern declarative UI framework
  - Less boilerplate code
  - Better state management integration
  - Easier to build dynamic UIs
  - Growing ecosystem and community support

- **Cons**:
  - Relatively new (learning curve)
  - Some third-party libraries may not support it yet
  - Requires Android API 21+ (shouldn't be an issue)

- **Best For**: New projects, modern UI patterns, dynamic content

**Option B: XML Layouts + View Binding (Traditional)**

- **Pros**:
  - Mature, well-documented
  - Extensive library support
  - Familiar to most Android developers
  - Stable and proven

- **Cons**:
  - More boilerplate
  - More verbose
  - Harder to build dynamic UIs

- **Best For**: If team is more familiar with traditional Android development

**Recommendation**: **Jetpack Compose** - Better suited for dynamic game content and LLM-driven UI updates

---

## 2. LLM Integration

### 2.1 LLM Provider

**Primary: Upstage API (Recommended)**

- **Models**: Solar LLM family (Solar Pro, Solar Plus, etc.)
- **Integration Options**:
  1. **Direct API** (via Retrofit) - Recommended for Android
  2. **LangChain Integration** - If available for Java/Kotlin
- **Pros**:
  - Startup partnership support
  - Good performance and reliability
  - Multi-language support (English, Korean, Japanese)
  - Structured output support
  - Fast response times
  - Cost-effective (partnership pricing)

- **Cons**:
  - Requires internet connection
  - Rate limits (check partnership terms)
  - May need to adapt from OpenAI/Anthropic patterns

- **API Documentation**: Check Upstage API docs for REST API endpoints
- **LangChain**: Check if `langchain-upstage` has Java/Kotlin support or use via backend

**Alternative Option A: OpenAI API (Fallback)**

- **Models**: GPT-4, GPT-3.5-turbo
- **Use Case**: Fallback if Upstage API unavailable or for comparison
- **Pros**: Well-documented, good function calling support
- **Cons**: API costs, external dependency

**Alternative Option B: Anthropic Claude API (Fallback)**

- **Models**: Claude 3 Opus, Sonnet, Haiku
- **Use Case**: Fallback option
- **Pros**: Good for long context windows
- **Cons**: API costs, external dependency

**Alternative Option C: Local Model (Llama.cpp, Ollama)**

- **Models**: Llama 2/3, Mistral, etc.
- **Use Case**: Offline capability, no API costs
- **Pros**: Privacy, offline, no rate limits
- **Cons**: Device requirements, may be slower

**Recommendation**: **Upstage API** (primary via Retrofit or LangChain), with OpenAI/Anthropic as fallback if needed

### 2.2 LLM API Client Library

**Option A: Retrofit + OkHttp (Recommended for Direct API)**

- **Rationale**:
  - Industry standard for Android networking
  - Type-safe API definitions
  - Excellent error handling
  - Easy to add interceptors (logging, retry, caching)
  - Coroutines support
  - Well-documented
  - Direct REST API integration with Upstage

- **Implementation**:
  - Create Retrofit interface for Upstage API endpoints
  - Use OkHttp interceptors for authentication (API key)
  - Type-safe request/response models

**Option B: LangChain (If Available for Java/Kotlin)**

- **Pros**:
  - Abstraction layer over LLM providers
  - Easy to switch between providers
  - Built-in tools (function calling, structured output)
  - Good for complex LLM workflows

- **Cons**:
  - May not have full Java/Kotlin support (check LangChain4j or similar)
  - Additional dependency
  - May require backend if Python-based

- **Note**: Check if LangChain has Java/Kotlin bindings (`langchain4j` or similar) or if Upstage provides Kotlin SDK

**Option C: Upstage SDK (If Available)**

- **Pros**:
  - Official SDK if provided by Upstage
  - Optimized for their API
  - May include Android-specific features

- **Cons**:
  - May not exist yet
  - Less flexible than Retrofit

**Recommendation**: 
1. **Primary**: Retrofit + OkHttp (direct Upstage API integration)
2. **Alternative**: LangChain if Java/Kotlin support available
3. **Check**: Upstage SDK availability

### 2.2.1 LangChain Integration Notes

**Python-based LangChain**:
- If using `langchain-upstage` (Python):
  - Requires backend server (Python/Flask/FastAPI)
  - Android app communicates with backend via REST API
  - Backend handles LangChain + Upstage API calls
  - Pros: Easy to use LangChain features, Python ecosystem
  - Cons: Requires backend infrastructure, additional latency

**Java/Kotlin LangChain**:
- Check if `langchain4j` or similar exists with Upstage support
- If available: Direct integration in Android app
- Pros: No backend needed, lower latency
- Cons: May not have all Python LangChain features

**Recommendation**:
- **Start with**: Direct Upstage API via Retrofit (simpler, no backend)
- **Consider**: LangChain if you need advanced features (chains, agents, tools)
- **If LangChain needed**: Use Python backend with REST API from Android

### 2.3 Structured Output Handling

**Option A: Function Calling (Recommended if Supported by Upstage)**

- **Pros**:
  - Type-safe schema definitions
  - Reliable structured output
  - Better for complex data structures
  - Native support in many LLM APIs

- **Note**: Check if Upstage API supports function calling/tool use

**Option B: JSON Mode / Structured Output (Recommended)**

- **Pros**:
  - Simple JSON parsing
  - Works with most models
  - Easy to implement
  - Upstage API likely supports this

- **Cons**:
  - May need retry logic
  - Less reliable than function calling

**Option C: Prompt Engineering + Parsing**

- **Pros**:
  - Works with any model
  - Flexible
  - Universal fallback

- **Cons**:
  - Less reliable
  - Requires robust parsing
  - May need validation

**Recommendation**: 
1. **Primary**: Check Upstage API for structured output/JSON mode support
2. **LLM 1 (Initializer)**: Use structured output (JSON mode or function calling)
3. **LLM 2-5 (Runtime)**: Use JSON mode for responses, with prompt engineering as fallback

---

## 3. Architecture Pattern

### 3.1 Architecture

**Option A: MVVM (Model-View-ViewModel) - Recommended**

- **Pros**:
  - Well-established Android pattern
  - Good separation of concerns
  - Easy to test
  - Works well with Jetpack Compose
  - StateFlow/LiveData integration

- **Structure**:
  ```
  View (Compose UI)
    ↓
  ViewModel (State management)
    ↓
  Repository (Data layer)
    ↓
  DataSource (LLM API, Local DB)
  ```

**Option B: MVI (Model-View-Intent)**

- **Pros**:
  - Unidirectional data flow
  - Predictable state management
  - Good for complex state

- **Cons**:
  - More boilerplate
  - Steeper learning curve

**Option C: Clean Architecture**

- **Pros**:
  - Very scalable
  - Testable
  - Separation of concerns

- **Cons**:
  - Overkill for MVP
  - More complex setup

**Recommendation**: **MVVM** - Good balance of simplicity and structure for this project

### 3.2 Dependency Injection

**Option A: Hilt (Recommended)**

- **Pros**:
  - Official Google solution
  - Built on Dagger
  - Easy to use
  - Good documentation
  - Compose integration

**Option B: Koin**

- **Pros**:
  - Kotlin-first
  - Simpler than Hilt
  - No code generation

- **Cons**:
  - Less type-safe
  - Runtime dependency resolution

**Option C: Manual Dependency Injection**

- **Pros**:
  - No external dependencies
  - Full control

- **Cons**:
  - More boilerplate
  - Harder to maintain

**Recommendation**: **Hilt** - Official solution, well-integrated with Android

---

## 4. State Management

### 4.1 State Management Library

**Option A: StateFlow (Recommended)**

- **Pros**:
  - Kotlin coroutines integration
  - Type-safe
  - Cold streams (more efficient)
  - Works well with Compose
  - Official Android solution

**Option B: LiveData**

- **Pros**:
  - Lifecycle-aware
  - Simple API

- **Cons**:
  - Not Kotlin-first
  - Less flexible than StateFlow
  - Being phased out

**Option C: RxJava/Flow**

- **Pros**:
  - Powerful operators
  - Mature ecosystem

- **Cons**:
  - More complex
  - Overkill for simple state

**Recommendation**: **StateFlow** - Modern, Kotlin-first, works perfectly with Compose

### 4.2 Game State Management

**Recommended: Custom State Manager + StateFlow**

- Create a centralized game state class
- Use StateFlow to expose state to UI
- Update state through ViewModel
- Handle state persistence (save/load)

**Example Structure**:
```kotlin
data class GameState(
    val currentTime: GameTime,
    val player: Player,
    val characters: List<Character>,
    val places: List<Place>,
    val clues: List<Clue>,
    val timeline: Timeline,
    val flags: Map<String, Boolean>
)

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
}
```

---

## 5. Data Persistence

### 5.1 Local Database

**Option A: Room Database (Recommended)**

- **Pros**:
  - Official Android solution
  - Type-safe SQL queries
  - Compile-time validation
  - Good for complex data structures
  - Excellent for game state persistence

- **Use Cases**:
  - Save/load game state
  - Cache LLM responses
  - Store game history
  - Persist player progress

**Option B: DataStore (Preferences)**

- **Pros**:
  - Simple key-value storage
  - Coroutines support
  - Type-safe

- **Cons**:
  - Not suitable for complex data
  - No query support

**Option C: SharedPreferences**

- **Pros**:
  - Simple
  - Synchronous API

- **Cons**:
  - Not type-safe
  - Synchronous (blocks UI)
  - Being deprecated

**Recommendation**: **Room Database** for game state, **DataStore** for simple preferences

### 5.2 Caching Strategy

**Recommended: In-Memory Cache + Room Database**

- **In-Memory Cache**: Fast access to LLM responses during session
- **Room Database**: Persistent cache for save/load games
- Use keys: `Character/Place + Context (time, clues)`

**Implementation**:
```kotlin
// In-memory cache
private val responseCache = mutableMapOf<String, CachedResponse>()

// Persistent cache (Room)
@Entity
data class CachedLLMResponse(
    @PrimaryKey val key: String,
    val response: String,
    val timestamp: Long
)
```

---

## 6. Networking & API

### 6.1 HTTP Client

**Recommended: Retrofit + OkHttp**

- **Retrofit**: Type-safe REST client
- **OkHttp**: HTTP client with interceptors
- **Features**:
  - Request/response logging
  - Retry logic
  - Timeout handling
  - Error handling

### 6.2 JSON Serialization

**Option A: kotlinx.serialization (Recommended)**

- **Pros**:
  - Kotlin-first
  - Compile-time code generation
  - Type-safe
  - Good performance

**Option B: Gson**

- **Pros**:
  - Simple
  - Mature

- **Cons**:
  - Runtime reflection
  - Less type-safe

**Option C: Moshi**

- **Pros**:
  - Type-safe
  - Good performance

- **Cons**:
  - Less popular than Gson

**Recommendation**: **kotlinx.serialization** - Modern, Kotlin-first, type-safe

### 6.3 API Error Handling

**Recommended: Sealed Classes for Result Handling**

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

---

## 7. Asynchronous Operations

### 7.1 Coroutines

**Recommended: Kotlin Coroutines**

- **Rationale**:
  - Official Kotlin solution
  - Excellent for async operations
  - Works well with LLM API calls
  - StateFlow integration
  - Easy cancellation

**Key Components**:
- `viewModelScope` for ViewModel operations
- `CoroutineScope` for repository operations
- `Flow` for reactive data streams

**Example**:
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

## 8. Build Tools & Dependencies

### 8.1 Build System

**Recommended: Gradle (Kotlin DSL)**

- **Rationale**:
  - Official Android build system
  - Kotlin DSL for type-safe build scripts
  - Good dependency management

### 8.2 Dependency Management

**Recommended: Version Catalog**

- Centralized dependency versions
- Easy to manage
- Type-safe

**Example `libs.versions.toml`**:
```toml
[versions]
kotlin = "1.9.20"
compose = "1.5.0"
retrofit = "2.9.0"
hilt = "2.48"

[libraries]
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
```

---

## 9. Testing

### 9.1 Unit Testing

**Recommended: JUnit 5 + MockK**

- **JUnit 5**: Modern testing framework
- **MockK**: Kotlin-first mocking library
- **Coroutines Testing**: `kotlinx.coroutines-test`

### 9.2 UI Testing

**Recommended: Compose UI Testing**

- `androidx.compose.ui:ui-test-junit4`
- `androidx.compose.ui:ui-test-manifest`
- For Compose UI testing

**Alternative**: Espresso
- For traditional XML layouts

### 9.3 Integration Testing

**Recommended: Room + Repository Testing**

- Test database operations
- Test LLM API integration (mock)
- Test game state management

---

## 10. Recommended Tech Stack Summary

### Core Stack

| Category | Technology | Rationale |
|----------|------------|-----------|
| **Language** | Kotlin | Official Android language, modern, null-safe |
| **UI Framework** | Jetpack Compose | Modern, declarative, good for dynamic content |
| **Architecture** | MVVM | Well-established, good separation of concerns |
| **DI** | Hilt | Official Google solution, easy to use |
| **State Management** | StateFlow | Kotlin-first, works well with Compose |
| **Networking** | Retrofit + OkHttp | Industry standard, type-safe |
| **JSON** | kotlinx.serialization | Kotlin-first, type-safe, compile-time |
| **Database** | Room | Official solution, type-safe queries |
| **Async** | Kotlin Coroutines | Official Kotlin solution, excellent integration |
| **LLM Provider** | Upstage API (Solar LLM) | Partnership support, multi-language |

### LLM-Specific Stack

| Component | Technology | Rationale |
|-----------|------------|-----------|
| **LLM Provider** | Upstage API (Solar LLM family) | Partnership support, cost-effective |
| **API Client** | Retrofit + OkHttp (or LangChain) | Direct API integration |
| **Structured Output** | JSON Mode / Structured Output (LLM 1), JSON Mode (LLM 2-5) | Reliable structured data |
| **Caching** | In-Memory + Room Database | Fast access + persistence |
| **Error Handling** | Sealed Result classes | Type-safe error handling |

### Optional/Alternative

| Component | Alternative | When to Use |
|-----------|-------------|-------------|
| **UI** | XML Layouts | Team more familiar with traditional Android |
| **LLM** | OpenAI API | Fallback if Upstage unavailable |
| **LLM** | Anthropic Claude | Fallback option, longer context windows |
| **LLM** | Local Model (Llama.cpp) | Want offline capability, no API costs |
| **Architecture** | MVI | Want unidirectional data flow |
| **DI** | Koin | Prefer Kotlin-first, simpler DI |

---

## 11. Project Structure

```
app/
├── data/
│   ├── local/
│   │   ├── database/          # Room database
│   │   └── cache/              # Cache entities
│   ├── remote/
│   │   ├── api/                # Retrofit API interfaces
│   │   └── dto/                # Data transfer objects
│   └── repository/             # Repository implementations
├── domain/
│   ├── model/                  # Domain models
│   ├── usecase/                # Use cases
│   └── repository/             # Repository interfaces
├── ui/
│   ├── compose/                # Compose screens
│   ├── theme/                  # App theme
│   └── components/             # Reusable components
├── presentation/
│   ├── viewmodel/              # ViewModels
│   └── state/                  # UI state classes
├── llm/
│   ├── client/                 # LLM API client
│   ├── generators/             # LLM 1-5 generators
│   ├── cache/                  # LLM response cache
│   └── parsers/                # Response parsers
└── di/                          # Dependency injection modules
```

---

## 12. Dependencies (Gradle)

### Core Dependencies

```kotlin
// Compose
implementation("androidx.compose.ui:ui:$compose_version")
implementation("androidx.compose.material3:material3:$material3_version")
implementation("androidx.activity:activity-compose:$activity_compose_version")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")

// StateFlow
implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")

// Hilt
implementation("com.google.dagger:hilt-android:$hilt_version")
kapt("com.google.dagger:hilt-compiler:$hilt_version")

// Retrofit
implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
implementation("com.squareup.okhttp3:logging-interceptor:$okhttp_version")

// Room
implementation("androidx.room:room-runtime:$room_version")
implementation("androidx.room:room-ktx:$room_version")
kapt("androidx.room:room-compiler:$room_version")

// Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
```

---

## 13. Development Timeline Considerations

### Phase 1: Foundation
- Set up Kotlin + Compose project
- Set up Hilt DI
- Create basic data models
- Set up Retrofit for LLM API

### Phase 2: LLM Integration
- Implement Upstage API client (Retrofit or LangChain)
- Set up Room database for caching
- Implement LLM 1 (Initializer)
- Set up structured output parsing
- Check Upstage API documentation for endpoints and authentication

### Phase 3: Runtime LLMs
- Implement LLM 2-5
- Set up caching system
- Implement clue extraction

### Phase 4: UI
- Build Compose UI
- Integrate with ViewModels
- Connect to LLM generators

---

## 14. Cost Considerations

### LLM API Costs (Estimated)

**Upstage API (Solar LLM)**:
- **Cost**: Partnership pricing (check with Upstage for rates)
- **Estimated**: Likely similar or better than OpenAI pricing
- **Note**: Check Upstage API documentation for current pricing model
- **Estimated per game session**: Similar to OpenAI estimates ($0.50-1.40 per game session, see LLM_ARCHITECTURE.md)

**OpenAI GPT-3.5-turbo (Fallback)**:
- Input: $0.50 per 1M tokens
- Output: $1.50 per 1M tokens
- Estimated: $0.50-1.40 per game session

**OpenAI GPT-4 (Fallback)**:
- Input: $30 per 1M tokens
- Output: $60 per 1M tokens
- Much more expensive, better quality

**Recommendation**: 
- **Primary**: Use Upstage API (Solar LLM) - partnership pricing
- **Fallback**: OpenAI GPT-3.5-turbo if needed
- Check Upstage partnership terms for rate limits and costs

---

## 15. Final Recommendation

### MVP Tech Stack

1. **Kotlin + Jetpack Compose** - Modern UI
2. **MVVM + Hilt** - Clean architecture
3. **StateFlow** - State management
4. **Retrofit + OkHttp** - LLM API calls (Upstage API)
5. **Room Database** - Persistence + caching
6. **Upstage API (Solar LLM)** - LLM provider (partnership support)
7. **kotlinx.serialization** - JSON handling
8. **Kotlin Coroutines** - Async operations

### Rationale

- **Modern but Stable**: Uses current Android best practices
- **Well-Documented**: All technologies have excellent documentation
- **Scalable**: Can grow with the project
- **Partnership Support**: Upstage API provides startup support
- **Cost-Effective**: Partnership pricing with Upstage
- **Testable**: Easy to write unit and UI tests
- **Maintainable**: Clear separation of concerns

---

## 16. Next Steps

1. **Set up Android project** with recommended stack
2. **Get Upstage API credentials** and documentation
3. **Check Upstage API endpoints** and authentication method
4. **Set up dependency injection** (Hilt)
5. **Create data models** for game entities
6. **Implement Upstage API client** (Retrofit or LangChain)
7. **Set up Room database** for persistence
8. **Build first UI screen** (Compose)
9. **Implement LLM 1** (Initializer) using Upstage API
10. **Test Upstage API integration** with simple prompts

---

*This tech stack can evolve as the project develops. Start with MVP stack and add more advanced features as needed.*

