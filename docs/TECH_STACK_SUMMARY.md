# Tech Stack - Quick Reference

## Recommended Stack

| Category | Technology | Version | Rationale |
|----------|------------|---------|-----------|
| **Language** | Kotlin | Latest | Official Android language, null-safe |
| **UI Framework** | Jetpack Compose | Latest | Modern, declarative, dynamic content |
| **Architecture** | MVVM | - | Well-established, good separation |
| **DI** | Hilt | Latest | Official Google solution |
| **State** | StateFlow | - | Kotlin-first, Compose integration |
| **Networking** | Retrofit + OkHttp | Latest | Industry standard, type-safe |
| **JSON** | kotlinx.serialization | Latest | Kotlin-first, type-safe |
| **Database** | Room | Latest | Official solution, type-safe |
| **LLM** | Upstage API | Solar LLM family | Partnership support, cost-effective |
| **Async** | Kotlin Coroutines | Latest | Official Kotlin solution |

## Key Dependencies

```kotlin
// Compose
androidx.compose.ui:ui
androidx.compose.material3:material3
androidx.activity:activity-compose

// ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.lifecycle:lifecycle-runtime-compose

// Hilt
com.google.dagger:hilt-android
kapt:com.google.dagger:hilt-compiler

// Retrofit
com.squareup.retrofit2:retrofit
com.squareup.retrofit2:converter-gson
com.squareup.okhttp3:okhttp
com.squareup.okhttp3:logging-interceptor

// Room
androidx.room:room-runtime
androidx.room:room-ktx
kapt:androidx.room:room-compiler

// Serialization
org.jetbrains.kotlinx:kotlinx-serialization-json

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android
```

## Alternatives

| Component | Alternative | When to Use |
|-----------|-------------|-------------|
| **UI** | XML Layouts | Team familiar with traditional Android |
| **LLM** | OpenAI API | Fallback if Upstage unavailable |
| **LLM** | Anthropic Claude | Fallback option, longer context windows |
| **LLM** | Local Model | Want offline, no API costs |
| **Architecture** | MVI | Want unidirectional data flow |
| **DI** | Koin | Prefer simpler Kotlin-first DI |

## Project Structure

```
app/
├── data/
│   ├── local/          # Room database
│   ├── remote/         # Retrofit API
│   └── repository/     # Repositories
├── domain/
│   ├── model/          # Domain models
│   └── usecase/        # Use cases
├── ui/
│   ├── compose/        # Compose screens
│   └── theme/          # App theme
├── presentation/
│   └── viewmodel/      # ViewModels
└── llm/
    ├── client/         # LLM API client
    ├── generators/     # LLM 1-5
    └── cache/          # Response cache
```

## Cost Estimates

- **Upstage API (Solar LLM)**: Partnership pricing (check with Upstage)
- **Estimated**: Similar to OpenAI (~$0.50-1.40 per game session)
- **Fallback**: OpenAI GPT-3.5-turbo if needed
- **Recommendation**: Use Upstage API (partnership pricing)

See [TECH_STACK.md](./TECH_STACK.md) for detailed explanations.

