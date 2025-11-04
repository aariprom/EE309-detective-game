# Quick Start Guide - For Complete Beginners

## 🎯 Welcome!

This guide is for people who have **never done Android development before**. No worries - we'll walk you through everything step by step!

---

## ✅ What You Need

### 1. **A Computer** (Windows, Mac, or Linux)
- Any modern computer works!

### 2. **Android Studio** (We'll install it together)
- This is the program where we write and run our code
- It's free from Google

### 3. **An Android Phone** (Optional)
- You can use an emulator (virtual phone) instead
- Android Studio includes emulators

### 4. **Cursor** (Optional - for code editing)
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
  - See [docs/README_SETUP.md](./README_SETUP.md) for troubleshooting

---

## 📱 Step 5: Set Up a Device/Emulator

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

## ▶️ Step 6: Run the App!

1. **Click** the green ▶️ "Run" button (top right)
2. **Select** your device/emulator
3. **Wait** for the app to build and install (1-2 minutes first time)
4. **The app will open** on your device/emulator!

**Congratulations!** 🎉 You just ran your first Android app!

---

## 🎓 Next Steps

### Learn the Basics

1. **Read:** [BEGINNER_GUIDE.md](./BEGINNER_GUIDE.md) - Explains how everything works
2. **Explore:** Open some code files and read them
3. **Make a small change:** Try changing text in `GameScreen.kt`
4. **Run again:** See your change in action!

### Understand the Project

1. **Read:** [PROJECT_BREAKDOWN.md](./PROJECT_BREAKDOWN.md) - What the project does
2. **Read:** [LLM_ARCHITECTURE.md](./LLM_ARCHITECTURE.md) - How the AI works
3. **Read:** [TECH_STACK.md](./TECH_STACK.md) - What technologies we use

### Start Coding

1. **Start simple:** Read code files starting with `GameTime.kt`
2. **Make small changes:** Change values and see what happens
3. **Ask questions:** Don't be afraid to ask for help!

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

## ✅ Checklist

Before you start coding, make sure:

- [ ] Android Studio is installed
- [ ] Project is open in Android Studio
- [ ] Gradle sync completed successfully
- [ ] Device/emulator is set up
- [ ] App runs successfully (you see the screen!)
- [ ] You've read [BEGINNER_GUIDE.md](./BEGINNER_GUIDE.md)

---

## 🎯 You're Ready!

Once you've completed all the steps above, you're ready to start developing!

**Remember:**
- Take your time
- Don't be afraid to make mistakes
- Ask for help when you need it
- Have fun! 🎉

---

**Next:** Read [BEGINNER_GUIDE.md](./BEGINNER_GUIDE.md) to understand how everything works!

