# Quick Start Guide

## 🚀 For Complete Beginners

**Start here**: Read [BEGINNER_GUIDE.md](./BEGINNER_GUIDE.md) first!

This guide explains:
- What was created
- How Android development works
- What Kotlin is
- How to get started

---

## ⚡ Quick Setup (5 Steps)

### 1. Install Android Studio
- Download: https://developer.android.com/studio
- Install and open

### 2. Open This Project
- File → Open → Select `EE309-detective-game` folder
- Wait for Gradle sync (5-10 minutes first time)

### 3. Get Upstage API Key
- Contact Upstage
- Create `local.properties` in project root:
  ```
  UPSTAGE_API_KEY=your_key_here
  ```

### 4. Run the App
- Click ▶️ Run button
- Select emulator or device
- See the app launch!

### 5. Start Learning
- Read `BEGINNER_GUIDE.md`
- Open code files and read them
- Make small changes and see what happens

---

## 📁 Project Structure (Simplified)

```
Your Project
├── app/src/main/java/com/ee309/detectivegame/
│   ├── domain/model/      ← Game data (Character, Place, Clue, etc.)
│   ├── presentation/      ← Game logic (ViewModel)
│   ├── ui/compose/        ← Screens (what user sees)
│   ├── llm/client/        ← AI API connection
│   └── di/                ← App setup
└── Documentation
    ├── BEGINNER_GUIDE.md  ← START HERE!
    └── docs/              ← Detailed docs
```

---

## 🎯 What to Do Next

1. **Read** `BEGINNER_GUIDE.md` (explains everything)
2. **Open** Android Studio
3. **Explore** the code (start with `GameTime.kt`)
4. **Make** a small change (change text in `GameScreen.kt`)
5. **Run** the app and see your change
6. **Learn** more about Kotlin and Android
7. **Implement** LLM 1 (Initial Content Generator)

---

## 💡 Key Files to Understand

| File | What It Does | Difficulty |
|------|--------------|------------|
| `GameTime.kt` | Manages time | ⭐ Easy |
| `Player.kt` | Stores player info | ⭐ Easy |
| `GameState.kt` | Holds all game data | ⭐⭐ Medium |
| `GameViewModel.kt` | Game logic | ⭐⭐⭐ Hard |
| `GameScreen.kt` | UI screen | ⭐⭐⭐ Hard |

**Start with the easy ones!**

---

## 🆘 Common Issues

| Problem | Solution |
|---------|----------|
| Gradle sync failed | Wait, retry, restart Android Studio |
| Can't run app | Check emulator/device is connected |
| Don't understand code | Read BEGINNER_GUIDE.md, search online |
| Build errors | Check error message, Google it |

---

**Remember**: Take your time, experiment, and ask questions! 🎓

