# Beginner's Guide to the Detective Game Project

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

## 🏗️ What I've Created (The Foundation)

Think of building an app like building a house. I've created the foundation and structure.

### 1. **Project Configuration Files**

These tell Android Studio how to build your app:

- **`build.gradle.kts`** - Lists all the tools/libraries your app needs (like a shopping list)
- **`settings.gradle.kts`** - Tells Gradle which parts of the project to build
- **`AndroidManifest.xml`** - Describes your app to Android (name, permissions, etc.)

**You don't need to change these right now!**

### 2. **Data Models (The Building Blocks)**

These are like **blueprints** for your game data. They define what information you can store:

- **`GameTime.kt`** - Tracks time in the game (5-minute units)
  ```kotlin
  // Example: GameTime(minutes = 30) means 30 minutes have passed
  ```

- **`Player.kt`** - Represents the player
  ```kotlin
  // Stores: name, location, clues collected, tools
  ```

- **`Character.kt`** - Represents characters in the game
  ```kotlin
  // Stores: name, traits, is criminal, location, etc.
  ```

- **`Place.kt`** - Represents locations
  ```kotlin
  // Stores: name, available clues, connected places
  ```

- **`Clue.kt`** - Represents clues
  ```kotlin
  // Stores: who, what, when, where, why
  ```

- **`GameState.kt`** - The "master" container
  ```kotlin
  // Holds everything: player, characters, places, clues, timeline
  ```

**Think of these like forms you fill out with information.**

### 3. **The App Structure (MVVM Pattern)**

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

**Files I created:**
- **`GameViewModel.kt`** - The "brain" that manages game logic
- **`GameScreen.kt`** - The screen users see
- **`GameUiState.kt`** - Tracks if the screen is loading, showing data, or showing an error

### 4. **API Client (Talking to the AI)**

- **`UpstageApiClient.kt`** - Code to talk to the Upstage AI
  - Like a phone that calls the AI
  - Currently a skeleton (needs to be filled in with actual API details)

### 5. **Dependency Injection (Hilt)**

- **`AppModule.kt`** - Sets up how different parts of your app connect
  - Like a wiring diagram for your app
  - Makes sure all parts can talk to each other

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

---

## 🚀 What You Need to Do Next

### Step 1: Install Android Studio

1. Download from: https://developer.android.com/studio
2. Install it (follow the wizard)
3. Open Android Studio

### Step 2: Open This Project

1. In Android Studio: **File → Open**
2. Navigate to the `EE309-detective-game` folder
3. Click **OK**

### Step 3: Wait for Gradle Sync

- Android Studio will automatically download libraries
- Look for "Gradle sync" at the bottom
- Wait until it says "Gradle sync finished"

**This might take 5-10 minutes the first time!**

### Step 4: Get Upstage API Key

1. Contact Upstage for your API key
2. Create a file called `local.properties` in the project root
3. Add this line:
   ```
   UPSTAGE_API_KEY=your_actual_api_key_here
   ```

### Step 5: Run the App

1. Click the green **▶️ Run** button (or press Shift+F10)
2. Select an emulator (virtual phone) or connect a real phone
3. Wait for the app to build and launch

**You should see a simple screen with "Detective Game" text!**

---

## 📚 Learning Resources

### For Kotlin:
- **Official Tutorial**: https://kotlinlang.org/docs/getting-started.html
- **Practice**: https://play.kotlinlang.org/ (try code online)

### For Android:
- **Official Guide**: https://developer.android.com/courses
- **Compose Tutorial**: https://developer.android.com/jetpack/compose/tutorial

### For This Project:
- Read the code comments (I've added explanations)
- Start with simple files like `GameTime.kt`
- Try changing values and see what happens

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

## 🔍 How to Read the Code

### Example: `GameTime.kt`

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

## ⚠️ Common Issues & Solutions

### "Gradle sync failed"
- **Solution**: Check internet connection, wait and retry
- Sometimes Android Studio needs to be restarted

### "Cannot resolve symbol"
- **Solution**: Wait for Gradle sync to finish
- If persists, File → Invalidate Caches → Restart

### "Build failed"
- **Solution**: Check error message
- Usually means missing dependency or syntax error

### "App won't run"
- **Solution**: Make sure you have an emulator or connected device
- Check AndroidManifest.xml for errors

---

## 🎓 Key Concepts to Learn

### 1. **Compose (UI Framework)**
- Modern way to build Android UIs
- Uses functions to describe UI
- Automatically updates when data changes

```kotlin
@Composable
fun MyScreen() {
    Text("Hello World")  // Shows text on screen
}
```

### 2. **State Management**
- `StateFlow` - Holds data that can change
- UI automatically updates when state changes

```kotlin
val uiState = MutableStateFlow("Hello")
// UI watches this and updates automatically
```

### 3. **Coroutines (Async Programming)**
- For doing things that take time (like API calls)
- Prevents app from freezing

```kotlin
viewModelScope.launch {
    val data = api.getData()  // Takes time, doesn't freeze app
    uiState.value = data
}
```

---

## 📝 Next Steps Summary

1. ✅ **Install Android Studio** (if not done)
2. ✅ **Open the project** in Android Studio
3. ✅ **Wait for Gradle sync** to finish
4. ✅ **Get Upstage API key** and add to `local.properties`
5. ✅ **Run the app** to see it work
6. ✅ **Read the code** starting with simple files
7. ✅ **Make a small change** to see how it works
8. ✅ **Start implementing LLM 1** (Initial Content Generator)

---

## 💡 Tips for Learning

1. **Start Small**: Don't try to understand everything at once
2. **Read Code**: Open files and read them, even if you don't understand everything
3. **Make Changes**: Try changing values and see what happens
4. **Use Google**: If you don't understand something, search for it
5. **Ask Questions**: Don't hesitate to ask for help
6. **Practice**: The more you code, the easier it gets

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

## 📞 Need Help?

- **Kotlin Documentation**: https://kotlinlang.org/docs/home.html
- **Android Documentation**: https://developer.android.com/docs
- **Stack Overflow**: For specific questions
- **Project Documentation**: Check the `docs/` folder

---

**Remember**: Everyone starts somewhere! Take your time, experiment, and don't be afraid to make mistakes. That's how you learn! 🎉

