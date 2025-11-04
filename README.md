# EE309 Detective Game

An Android-based interactive detective game with LLM-powered dynamic content generation.

## 🎓 New to Kotlin/Android?

**Start here**: [docs/BEGINNER_GUIDE.md](./docs/BEGINNER_GUIDE.md) - Complete beginner-friendly explanation!

Or jump to: [docs/QUICK_START.md](./docs/QUICK_START.md) - Quick setup guide for complete beginners

## Project Documentation

- **[docs/PROJECT_BREAKDOWN.md](./docs/PROJECT_BREAKDOWN.md)**: Detailed module breakdown with task descriptions, priorities, difficulties, and feasibility assessments
- **[docs/TASK_SUMMARY.md](./docs/TASK_SUMMARY.md)**: Quick reference table with task dependencies and development phases
- **[docs/LLM_ARCHITECTURE.md](./docs/LLM_ARCHITECTURE.md)**: Detailed explanation of the Hybrid LLM Architecture approach
- **[docs/TECH_STACK.md](./docs/TECH_STACK.md)**: Comprehensive tech stack recommendations with alternatives and rationale
- **[docs/README_SETUP.md](./docs/README_SETUP.md)**: Detailed setup instructions

## Project Structure

The project is divided into 5 main modules:

1. **Core Game Engine & State Management** - Foundation for game state and flow
2. **LLM Integration Layer (Hybrid Architecture)** - Interface with LLM for content generation
   - **LLM 1**: Initial content generator (upfront generation)
   - **LLM 2-5**: Runtime generators (dialogue, descriptions, actions, updates)
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
- **JSON**: kotlinx.serialization
- **Async**: Kotlin Coroutines
