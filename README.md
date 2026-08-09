# 🎯 DeepFocus - Modern Pomodoro & Productivity App

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-100%25-blue.svg?logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-green.svg?logo=android" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-orange.svg" alt="Clean Architecture">
  <img src="https://img.shields.io/badge/DI-Dagger%20Hilt-red.svg?logo=android" alt="Hilt">
</p>

DeepFocus is a feature-rich, beautifully designed productivity application built entirely with **Modern Android Development (MAD)** principles. Centered around the Pomodoro technique, it seamlessly blends time management with a rewarding gamification system and insightful analytics to keep users motivated and focused.

## ✨ Key Features

* **⏳ Advanced Pomodoro Engine:** Fully customizable Focus, Short Break, and Long Break cycles backed by reliable Foreground Services to ensure the timer never misses a beat, even when the screen is off.
* **🎮 Gamification System:** Consistency is rewarded. Users earn "Stardust" points, unlock Ranks, and build daily Streaks (🔥/🏆) to maintain motivation.
* **📊 Comprehensive Statistics:** A dedicated analytics dashboard featuring total focused time, average session length, a dynamic weekly bar chart, and a detailed historical timeline of all completed sessions.
* **🎨 Premium UI/UX:** Built with Jetpack Compose and Material Design 3. Features fluid spring animations, dynamic theme adjustments based on the active timer phase, glassmorphism elements, and localized haptic feedback for a truly tactile experience.

## 🏗 Architecture & Tech Stack

The application strictly adheres to **Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)** pattern, ensuring a high separation of concerns, scalability, and testability.

### 🛠 Built With:
* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
* **Architecture:** Clean Architecture (Presentation, Domain, Data layers)
* **Asynchronous Programming:** Kotlin Coroutines & `StateFlow`
* **Dependency Injection:** [Dagger Hilt](https://dagger.dev/hilt/)
* **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room) (Session History) & [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences & Settings)
* **Navigation:** Jetpack Navigation Compose (Type-safe routes via `kotlinx.serialization`)
* **Background Processing:** Android Foreground Services (for uninterrupted timer countdowns)

## 📁 Project Structure

```text
app/
├─ data/          # Concrete implementations, Room DB, DataStore, Data Mappers
├─ domain/        # Core business logic, Models, Repositories (Interfaces), UseCases
├─ presentation/  # Jetpack Compose Screens, ViewModels, Custom UI Components
└─ di/            # Dagger Hilt Modules for Dependency Injection
```

## 🚀 Getting Started

### Prerequisites
* Android Studio (Latest stable version recommended)
* JDK 17
* Minimum SDK: 24 (Android 7.0)

### Installation
1. Clone the repository:
   ```bash
   git clone [https://github.com/your-username/DeepFocus.git](https://github.com/azadevs/DeepFocus.git)
   ```
2. Open the project in Android Studio.

3. Sync the Gradle files.

4. Build and run the application on an emulator or a physical device.
