# 🧩 Sudoku Genius

![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Language](https://img.shields.io/badge/Language-Java-orange.svg)
![Architecture](https://img.shields.io/badge/Architecture-MVP-blue.svg)
![Status](https://img.shields.io/badge/Status-Completed-success.svg)

> **"Logic is a skill that can be taught."**

**Sudoku Genius** is an intelligent Android application designed to transform players from "guessers" into strategic solvers. Built with a focus on education and clean design, it features a dedicated Learning Center, adaptive gameplay, and a robust offline experience.

---

## ✨ Key Features

### 🎮 Core Gameplay
* **Dynamic Puzzle Generation:** Play endless unique puzzles across 4 difficulty levels: **Easy, Medium, Hard, and Expert**.
* **Smart Input Methods:**
    * **Standard Input:** Tap to fill.
    * **Pencil Mode (Notes):** Mark potential candidates in small text to track your logic.
    * **Eraser:** Quickly clear mistakes.
* **Game Aids:** Intelligent **Hint System** and optional **Auto-Check Errors** (Real-time conflict detection).

### 📚 Learning Center (The "Genius" Aspect)
A dedicated educational module designed to teach Sudoku logic rather than just testing it.
* **Basic Rules:** Understand rows, columns, and boxes.
* **Single Candidate:** Learn the fundamental solving technique.
* **Hidden Single:** Master advanced elimination strategies.

### 💾 Robust Persistence (Save & Resume)
* **Auto-Save:** The game automatically saves the exact board state, timer, notes, and moves when you pause, switch apps, or exit.
* **Seamless Resume:** Continue exactly where you left off, ensuring no progress is ever lost.
* **Offline First:** Fully functional without an internet connection.

### 🌍 Localization & Settings
* **Multi-language Support:** Fully localized for **English** and **Simplified Chinese (简体中文)**.
* **Customization:** Toggle Vibration, Hints, and Error Checking in Settings.
* **Dark Mode Compatible:** UI elements are designed to be legible in various lighting conditions.

---

## 🛠️ Technical Architecture

This project strictly follows the **MVP (Model-View-Presenter)** architectural pattern to ensure separation of concerns, testability, and maintainability.

### 1. Model (Data Layer)
* **Entities:** `Puzzle` (Board state), `Difficulty`, `Position`.
* **Repository:** `PuzzleRepositoryImpl` encapsulates the complexity of Sudoku generation algorithms, solving logic, and rule validation.
* **Persistence:** Utilizes `SharedPreferences` for serializing and storing complex 2D array game states locally.

### 2. View (UI Layer)
* **Custom Views:**
    * `SudokuBoardView`: A high-performance, custom-drawn View using Android's `Canvas` API. It handles touch events, grid rendering, and highlighting far more efficiently than standard Layouts.
    * `NumberPadView`: A reusable component for input handling.
* **Responsive Design:** Utilizes `ConstraintLayout` to maintain a perfect 1:1 board aspect ratio across different screen sizes (Phones & Tablets).

### 3. Presenter (Logic Layer)
* Acts as the bridge between Model and View.
* Manages the game timer, move validation logic, hint requests, and coordinates the auto-save lifecycle (`saveGameState` / `loadGameState`).

---

## 📂 Project Structure

```text
com.example.sudokugenius
├── base            // (BaseView, BasePresenter)
├── model           // 
│   ├── entity      // (Puzzle, Difficulty, Position)
│   └── repository  // (PuzzleRepositoryImpl)
├── presenter       // (GamePresenter, MainPresenter)
├── view            // 
│   ├── activity    // (Game, Main, Settings, Learning, About)
│   └── custom      // (SudokuBoardView, NumberPadView)
└── util            // (GameSettings)

## 🚀 Getting Started

### Prerequisites
* Android Studio Ladybug | 2024.2.1 or newer.
* JDK 1.8 or higher.
* Android SDK API Level 26 (Android 8.0) or higher.

### Installation
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YourUsername/SudokuGenius.git](https://github.com/YourUsername/SudokuGenius.git)
    ```
2.  **Open in Android Studio:**
    * Select "Open an Existing Project" and navigate to the folder.
3.  **Build and Run:**
    * Wait for Gradle sync to complete.
    * Click the "Run" button (green play icon) to deploy to an emulator or physical device.

---

## 👨‍💻 Author

**Yang Xiaotian**
* **Student ID:** 59534065
* **Project:** Final Individual Project
* **Course:** Mobile Application Development
