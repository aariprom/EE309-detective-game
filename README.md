# EE309 Detective Game

An Android-based interactive detective game with LLM-powered dynamic content generation.

## 🎓 New to Kotlin/Android?

**Start here**: [BEGINNER_GUIDE.md](./BEGINNER_GUIDE.md) - Complete beginner-friendly explanation!

Or jump to: [QUICK_START.md](./QUICK_START.md) - Quick setup guide

## Project Documentation

- **[PROJECT_BREAKDOWN.md](./PROJECT_BREAKDOWN.md)**: Detailed module breakdown with task descriptions, priorities, difficulties, and feasibility assessments
- **[TASK_SUMMARY.md](./TASK_SUMMARY.md)**: Quick reference table with task dependencies and development phases
- **[LLM_ARCHITECTURE.md](./LLM_ARCHITECTURE.md)**: Detailed explanation of the Hybrid LLM Architecture approach
- **[TECH_STACK.md](./TECH_STACK.md)**: Comprehensive tech stack recommendations with alternatives and rationale

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

**Recommended Stack** (see [TECH_STACK.md](./TECH_STACK.md) for details):

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM + Hilt (DI)
- **State Management**: StateFlow
- **Networking**: Retrofit + OkHttp
- **Database**: Room Database
- **LLM**: Upstage API (Solar LLM) - Partnership support
- **JSON**: kotlinx.serialization
- **Async**: Kotlin Coroutines

See [TECH_STACK.md](./TECH_STACK.md) for detailed explanations, alternatives, and rationale.
