# Setup Instructions

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK API 24+ (minimum), API 34 (target)

## Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd EE309-detective-game
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory

3. **Sync Gradle**
   - Android Studio should automatically sync Gradle
   - If not, click "Sync Now" or go to File > Sync Project with Gradle Files

4. **Set up Upstage API Key**
   - Create `local.properties` in the root directory (if not exists)
   - Add your Upstage API key:
     ```
     UPSTAGE_API_KEY=your_api_key_here
     ```
   - Update `AppModule.kt` to read from `local.properties` or use BuildConfig

5. **Build and Run**
   - Select a device/emulator
   - Click Run (Shift+F10) or Build > Make Project

## Project Structure

```
app/src/main/java/com/ee309/detectivegame/
├── domain/           # Domain models and business logic
│   ├── model/        # Game entities (Character, Place, Clue, etc.)
│   ├── repository/   # Repository interfaces
│   └── usecase/      # Use cases
├── data/             # Data layer
│   ├── local/        # Room database, cache
│   ├── remote/       # API clients, DTOs
│   └── repository/   # Repository implementations
├── presentation/     # UI state and ViewModels
│   ├── viewmodel/    # ViewModels
│   └── state/        # UI state classes
├── ui/               # Compose UI
│   ├── compose/      # Screens
│   ├── theme/        # Theme configuration
│   └── components/   # Reusable components
├── llm/              # LLM integration
│   ├── client/       # API client
│   ├── generators/   # LLM 1-5 generators
│   ├── cache/        # Response cache
│   └── parsers/      # Response parsers
└── di/               # Dependency injection modules
```

## Next Steps

1. **Set up Upstage API**
   - Update `UpstageApiClient.kt` with actual API endpoints
   - Implement LLM 1 (Initial Content Generator)
   - Test API connection

2. **Set up Room Database**
   - Create database entities
   - Set up DAOs
   - Implement caching

3. **Implement Game Logic**
   - Create action handlers
   - Implement timeline processing
   - Add win/lose conditions

4. **Build UI**
   - Create game screens
   - Add navigation
   - Implement user interactions

## Development Phases

See [PROJECT_BREAKDOWN.md](./PROJECT_BREAKDOWN.md) for detailed development phases.

## Documentation

- [PROJECT_BREAKDOWN.md](./PROJECT_BREAKDOWN.md) - Detailed task breakdown
- [TASK_SUMMARY.md](./TASK_SUMMARY.md) - Quick reference
- [LLM_ARCHITECTURE.md](./LLM_ARCHITECTURE.md) - LLM architecture details
- [TECH_STACK.md](./TECH_STACK.md) - Tech stack recommendations

