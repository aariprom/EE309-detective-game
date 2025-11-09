# Getting Started Guide

## 🎯 What This Project Is

You're building an **Android detective game** where:
- Players solve mysteries
- An AI (LLM) generates the story, characters, and clues dynamically
- Players investigate places, question characters, and solve crimes

---

## 📱 What is Android Development?

Android apps are written in **Kotlin** (a programming language) and use:
- **Android Studio** - The program where you write and run your code
- **Gradle** - A tool that builds your app from code
- **Kotlin** - The programming language (like Python, but for Android)

---

## ✅ Prerequisites

### What You Need

1. **A Computer** (Windows, Mac, or Linux)
   - Any modern computer works!

2. **Android Studio** (We'll install it together)
   - This is the program where we write and run our code
   - It's free from Google
   - **Version**: Hedgehog (2023.1.1) or later

3. **JDK 17 or later**
   - Usually comes with Android Studio

4. **Android SDK**
   - API 24+ (minimum), API 34 (target)
   - Installed automatically with Android Studio

5. **An Android Phone** (Optional)
   - You can use an emulator (virtual phone) instead
   - Android Studio includes emulators

6. **Cursor** (Optional - for code editing)
   - A text editor for writing code
   - We'll use this alongside Android Studio

---

## 📥 Step 1: Install Android Studio

### Download Android Studio

1. **Go to:** https://developer.android.com/studio
2. **Click:** "Download Android Studio"
3. **Wait** for the download to finish (this might take a while - it's a big file!)

### Install Android Studio

1. **Open** the downloaded file
2. **Follow the installer** (click "Next" through the steps)
3. **Important:** Install the Android SDK when it asks
4. **Wait** for installation (10-20 minutes)
5. **Open** Android Studio when it's done

### First Time Setup

When you first open Android Studio:

1. **Choose "Standard" installation** (recommended)
2. **Wait** for it to download more components (10-15 minutes)
3. **Click "Finish"** when done
4. **Android Studio will restart** - this is normal!

**You're done with Step 1!** ✅

---

## 📂 Step 2: Get the Project

### Option A: If You Have Git Installed

1. **Open** a terminal/command prompt
2. **Type:**
   ```bash
   git clone <repository-url>
   cd EE309-detective-game
   ```
   (Replace `<repository-url>` with the actual repository URL)

### Option B: If You Don't Have Git

1. **Download** the project as a ZIP file
2. **Extract** it to a folder (like `C:\Users\YourName\EE309-detective-game`)
3. **Remember** where you saved it!

---

## 🚀 Step 3: Open the Project in Android Studio

1. **Open Android Studio**
2. **Click:** "Open" (or "Open an Existing Project")
3. **Navigate** to the project folder
   - Find the `EE309-detective-game` folder
   - Click on it
   - Click "OK"
4. **Wait** for Android Studio to load the project (2-5 minutes first time)

**What's happening:**
- Android Studio is reading your project files
- It's setting up Gradle (the build system)
- It's downloading dependencies (code libraries)
- This is normal and takes time!

---

## ⏳ Step 4: Wait for Gradle Sync

### What is Gradle Sync?

Gradle sync is when Android Studio:
- Downloads all the code libraries your project needs
- Sets up the build system
- Configures everything

### What You'll See:

1. **Bottom bar** will show "Gradle sync in progress..."
2. **Progress bar** at the bottom
3. **This takes 5-15 minutes the first time** - be patient!

### When It's Done:

- You'll see "Gradle sync finished" ✅
- No red error messages
- The project tree on the left shows all your files

**If you see errors:**
- Don't panic! Common issues are:
  - Internet connection needed for downloads
  - Need to wait longer (it's still downloading)
  - See [Common Issues & Solutions](#-common-issues--solutions) below

---

## 🔑 Step 5: Set Up Upstage API Key

1. **Contact Upstage** for your API key
2. **Create** a file called `local.properties` in the project root (if not exists)
3. **Add** this line:
   ```
   UPSTAGE_API_KEY=your_api_key_here
   ```
4. **Update** `AppModule.kt` to read from `local.properties` or use BuildConfig

---

## 📱 Step 6: Set Up a Device/Emulator

### Option A: Use Your Phone (Easier)

1. **Enable Developer Options** on your phone:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings → Developer Options
   - Enable "USB Debugging"
2. **Connect** your phone to your computer with a USB cable
3. **In Android Studio:** Click "Run" → Your phone should appear in the list

### Option B: Use an Emulator (Virtual Phone)

1. **In Android Studio:** Click the device dropdown (top bar)
2. **Click:** "Device Manager" (or "Create Virtual Device")
3. **Click:** "Create Device"
4. **Choose:** A phone model (like "Pixel 5")
5. **Click:** "Next"
6. **Choose:** A system image (download one if needed)
7. **Click:** "Finish"
8. **Click:** The play button ▶️ next to your emulator to start it

**First time creating an emulator:**
- Downloading system images takes 10-20 minutes
- This is normal! Just wait.

---

## ▶️ Step 7: Run the App!

1. **Click** the green ▶️ "Run" button (top right, or press Shift+F10)
2. **Select** your device/emulator
3. **Wait** for the app to build and install (1-2 minutes first time)
4. **The app will open** on your device/emulator!

**Congratulations!** 🎉 You just ran your first Android app!

---

## 🏗️ Understanding the Project Structure

### Project Configuration Files

These tell Android Studio how to build your app:

- **`build.gradle.kts`** - Lists all the tools/libraries your app needs (like a shopping list)
- **`settings.gradle.kts`** - Tells Gradle which parts of the project to build
- **`AndroidManifest.xml`** - Describes your app to Android (name, permissions, etc.)

**You don't need to change these right now!**

### Data Models (The Building Blocks)

These are like **blueprints** for your game data. They define what information you can store:

- **`GameTime.kt`** - Tracks time in the game (5-minute units)
- **`Player.kt`** - Represents the player (name, location, clues collected, tools)
- **`Character.kt`** - Represents characters in the game (name, traits, is_criminal, location, etc.)
- **`Place.kt`** - Represents locations (name, available clues, connected places)
- **`Clue.kt`** - Represents clues (who, what, when, where, why)
- **`GameState.kt`** - The "master" container (holds everything: player, characters, places, clues, timeline)

**Think of these like forms you fill out with information.**

### The App Structure (MVVM Pattern)

Apps use a pattern called **MVVM** (Model-View-ViewModel):

```
┌─────────────┐
│    View     │  ← What the user sees (UI/Screen)
│  (Compose)  │
└──────┬──────┘
       │
       ↓
┌─────────────┐
│  ViewModel  │  ← Logic/Brain (handles user actions)
└──────┬──────┘
       │
       ↓
┌─────────────┐
│    Model    │  ← Data (your game state)
└─────────────┘
```

**Key Files:**
- **`GameViewModel.kt`** - The "brain" that manages game logic
- **`GameScreen.kt`** - The screen users see
- **`GameUiState.kt`** - Tracks if the screen is loading, showing data, or showing an error

### Project Directory Structure

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

---

## 🎨 Kotlin Basics (What You Need to Know)

### Variables
```kotlin
val name = "John"        // Cannot be changed (like a constant)
var age = 25            // Can be changed
```

### Data Classes (Like Forms)
```kotlin
data class Person(
    val name: String,
    val age: Int
)

// Create a person
val person = Person(name = "John", age = 25)
```

### Functions
```kotlin
fun addNumbers(a: Int, b: Int): Int {
    return a + b
}

// Or shorter
fun addNumbers(a: Int, b: Int) = a + b
```

### Null Safety (Kotlin's Superpower)
```kotlin
val name: String? = null  // ? means "can be null"
val length = name?.length  // Safe call - won't crash if null
```

### Collections (Lists)
```kotlin
val names = listOf("Alice", "Bob", "Charlie")
val first = names[0]  // Gets "Alice"
```

### Example: Reading Code

Here's how to read `GameTime.kt`:

```kotlin
data class GameTime(
    val minutes: Int = 0  // Default value is 0
) {
    // Properties (computed values)
    val hours: Int get() = minutes / 60
    val minutesOfHour: Int get() = minutes % 60
    
    // Functions (things you can do)
    fun addMinutes(amount: Int): GameTime {
        return GameTime(minutes + amount)
    }
    
    fun format(): String {
        return String.format("%02d:%02d", hours, minutesOfHour)
    }
}
```

**What this does:**
- Creates a `GameTime` object that stores minutes
- Automatically calculates hours and minutes of hour
- Can add minutes or format as "HH:MM"

**How to use it:**
```kotlin
val time = GameTime(minutes = 90)  // 1 hour 30 minutes
val formatted = time.format()      // "01:30"
val later = time.addMinutes(15)     // 1 hour 45 minutes
```

---

## 🎓 Key Concepts to Learn

### 1. Compose (UI Framework)
- Modern way to build Android UIs
- Uses functions to describe UI
- Automatically updates when data changes

```kotlin
@Composable
fun MyScreen() {
    Text("Hello World")  // Shows text on screen
}
```

### 2. State Management
- `StateFlow` - Holds data that can change
- UI automatically updates when state changes

```kotlin
val uiState = MutableStateFlow("Hello")
// UI watches this and updates automatically
```

### 3. Coroutines (Async Programming)
- For doing things that take time (like API calls)
- Prevents app from freezing

```kotlin
viewModelScope.launch {
    val data = api.getData()  // Takes time, doesn't freeze app
    uiState.value = data
}
```

---

## 🎯 Your First Tasks (In Order)

### Task 1: Understand the Code Structure
1. Open `GameTime.kt` - Read the code
2. Open `Player.kt` - See how data is stored
3. Open `GameState.kt` - See how everything connects

### Task 2: Make a Small Change
1. Open `GameScreen.kt`
2. Find the text "Detective Game"
3. Change it to "My Detective Game"
4. Run the app and see your change!

### Task 3: Learn About Upstage API
1. Get API documentation from Upstage
2. Look at `UpstageApiClient.kt`
3. See what needs to be filled in

### Task 4: Implement LLM 1 (Initial Generator)
This is where you'll:
1. Create a prompt for the AI
2. Send it to Upstage API
3. Parse the response into game data
4. Create the initial game state

---

## 🆘 Common Issues & Solutions

### "Gradle sync failed"

**Solution:**
- Check your internet connection
- Wait longer (it's still downloading)
- Try: **File → Invalidate Caches → Restart**

### "SDK location not found"

**Solution:**
- Android Studio should auto-detect it
- If not: **File → Settings → Android SDK** → Check "Android SDK Location"
- Android Studio will create `local.properties` automatically

### "Cannot resolve symbol"

**Solution:**
- Wait for Gradle sync to finish
- Try: **File → Invalidate Caches → Restart**
- Make sure Gradle sync completed successfully

### "Build failed"

**Solution:**
- Check the error message at the bottom
- Usually means something is still downloading
- Wait and try again

### "App won't run"

**Solution:**
- Make sure you selected a device/emulator
- Check if emulator is running (if using emulator)
- Make sure phone is connected (if using phone)

---

## 💡 Tips for Beginners

### 1. **Don't Panic!**
- Everything takes time the first time
- Errors are normal - we all get them
- Google the error message - someone else had it too!

### 2. **Start Small**
- Don't try to understand everything at once
- Read one file at a time
- Make small changes and see what happens

### 3. **Use Android Studio's Help**
- **Right-click** on code → "Go to Definition" (see what code does)
- **Hover** over code → See tooltips
- **Ctrl+Click** (or Cmd+Click) → Jump to definitions

### 4. **Ask Questions**
- No question is too basic
- Ask your teammates
- Search online (Stack Overflow is your friend!)

### 5. **Experiment**
- Make changes and see what happens
- Break things (and then fix them!)
- That's how you learn!

---

## 📚 Learning Resources

### For Kotlin:
- **Official Tutorial:** https://kotlinlang.org/docs/getting-started.html
- **Practice Online:** https://play.kotlinlang.org/

### For Android:
- **Official Guide:** https://developer.android.com/courses
- **Compose Tutorial:** https://developer.android.com/jetpack/compose/tutorial

### For This Project:
- **Read the code:** Start with simple files like `GameTime.kt`
- **Read the docs:** Check the `/docs` folder
- **Ask questions:** Don't hesitate!

---

## 🎯 Quick Reference: What Each File Does

| File | Purpose | When to Touch |
|------|---------|---------------|
| `GameTime.kt` | Time management | When you need time logic |
| `Player.kt` | Player data | When tracking player info |
| `Character.kt` | Character data | When managing characters |
| `Place.kt` | Location data | When managing places |
| `Clue.kt` | Clue data | When managing clues |
| `GameState.kt` | Complete game state | When you need all game data |
| `GameViewModel.kt` | Game logic | **You'll modify this a lot!** |
| `GameScreen.kt` | UI screen | **You'll modify this a lot!** |
| `UpstageApiClient.kt` | AI API | When connecting to Upstage |
| `AppModule.kt` | Dependency setup | When adding new dependencies |

---

## 🚦 Development Workflow

1. **Write Code** in Android Studio
2. **Build** (Ctrl+F9) - Check for errors
3. **Run** (Shift+F10) - See it on device/emulator
4. **Test** - Try different scenarios
5. **Fix Bugs** - Debug errors
6. **Repeat** - Keep iterating

---

## ✅ Checklist

Before you start coding, make sure:

- [ ] Android Studio is installed
- [ ] Project is open in Android Studio
- [ ] Gradle sync completed successfully
- [ ] Device/emulator is set up
- [ ] App runs successfully (you see the screen!)
- [ ] Upstage API key is configured

---

## 🎯 You're Ready!

Once you've completed all the steps above, you're ready to start developing!

**Remember:**
- Take your time
- Don't be afraid to make mistakes
- Ask for help when you need it
- Have fun! 🎉

---

## 📖 Next Steps

1. **Read:** [PROJECT_BREAKDOWN.md](./PROJECT_BREAKDOWN.md) - What the project does
2. **Read:** [LLM_ARCHITECTURE.md](./LLM_ARCHITECTURE.md) - How the AI works
3. **Read:** [TECH_STACK.md](./TECH_STACK.md) - What technologies we use
4. **Read:** [TASK_SUMMARY.md](./TASK_SUMMARY.md) - Quick task reference

---

**Need Help?**
- **Kotlin Documentation**: https://kotlinlang.org/docs/home.html
- **Android Documentation**: https://developer.android.com/docs
- **Stack Overflow**: For specific questions
- **Project Documentation**: Check the `docs/` folder

