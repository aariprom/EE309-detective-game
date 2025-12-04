# EE309 Detective Game

An Android-based interactive detective game with LLM-powered dynamic content generation.

## 🎓 New to Kotlin/Android?

**Start here**: [docs/GETTING_STARTED.md](./docs/GETTING_STARTED.md) - Complete beginner-friendly guide with setup instructions!

## Project Documentation

- **[docs/GETTING_STARTED.md](./docs/GETTING_STARTED.md)**: Complete beginner guide with setup instructions
- **[docs/PROJECT_BREAKDOWN.md](./docs/PROJECT_BREAKDOWN.md)**: Detailed module breakdown with task descriptions, priorities, difficulties, and feasibility assessments
- **[docs/TASK_SUMMARY.md](./docs/TASK_SUMMARY.md)**: Quick reference table with task dependencies and development phases
- **[docs/LLM_ARCHITECTURE.md](./docs/LLM_ARCHITECTURE.md)**: Detailed explanation of the Hybrid LLM Architecture approach
- **[docs/TECH_STACK.md](./docs/TECH_STACK.md)**: Tech stack documentation (matches actual build configuration)

## Project Structure

The project is divided into 5 main modules:

1. **Core Game Engine & State Management** - Foundation for game state and flow
2. **LLM Integration Layer (Hybrid Architecture)** - Interface with LLM for content generation
   - **LLM 1**: Initial content generator (upfront generation)
   - **LLM 2-4**: Runtime generators (intro, dialogue, descriptions)
   - **Caching**: Performance optimization
3. **Game Content System** - Management of characters, places, clues, and timeline
4. **Android UI/UX Components** - User interface and interaction
5. **Game Logic & Validation System** - Game rules and action handling

## Tech Stack

**Recommended Stack** (see [docs/TECH_STACK.md](./docs/TECH_STACK.md) for details):

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM + Hilt (DI)
- **State Management**: StateFlow
- **Networking**: Retrofit + OkHttp
- **Database**: Room Database
- **LLM**: Upstage API (Solar LLM) - Partnership support
- **JSON**: Gson (Retrofit) + kotlinx.serialization (Domain models)
- **Async**: Kotlin Coroutines
