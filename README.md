# Ilm-e-Zubaan

Ilm-e-Zubaan (Knowledge of the Tongue) is a modern Android application designed for learning regional languages. The app leverages Jetpack Compose, Room, Hilt, and Firebase to provide a seamless learning experience with features like vocabulary tracking, literacy lessons, and AI-powered insights.

## 🚀 Features

- **Language Selection**: Choose your native language and the language you want to learn.
- **Home Dashboard**: Track your daily streak, XP, and progress.
- **Vocabulary**: Explore and learn new words with audio support.
- **Literacy Lessons**: Engage with audio/video lessons tailored to your learning path.
- **AI Insights**: Get deeper word insights and explanations using Gemini AI.
- **User Profile**: Customize your experience and track your learning statistics.
- **Secure Authentication**: Multiple login options (Email, Phone, Google) with encrypted local storage.

## 🛠️ Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) for a modern, declarative UI.
- **Architecture**: MVVM with [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for Dependency Injection.
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room) for local data persistence and offline support.
- **Networking**: [Retrofit](https://square.github.io/retrofit/) for API calls.
- **Backend/Auth**: [Firebase](https://firebase.google.com/) (Auth, Firestore, Analytics).
- **AI Integration**: [Gemini AI](https://ai.google.dev/) for intelligent language insights.
- **Logging**: [Timber](https://github.com/JakeWharton/timber) for structured logging.
- **Security**: [Jetpack Security](https://developer.android.com/topic/security/data) for encrypted preferences.

## 🏗️ Project Structure

- `app/src/main/java/com/ilmezubaan/app/`
    - `data/`: Local and remote data sources, entities, and repositories.
    - `di/`: Hilt modules for dependency injection.
    - `ui/`: Screens, navigation, and theme components.
    - `utils/`: Security and helper utilities.
    - `viewmodel/`: ViewModels for state management.

## 🚦 Getting Started

### Prerequisites
- Android Studio Ladybug or later.
- JDK 11+.
- A Google Cloud Project with Gemini API and Firebase enabled.

### Setup
1. Clone the repository.
2. Add your `google-services.json` to the `app/` directory.
3. Add your Gemini API keys to `local.properties`:
   ```properties
   GEMINI_API_KEY_1=your_key_here
   GEMINI_API_KEY_2=your_other_key_here
   ```
4. Sync Gradle and run the app.

## 📈 Roadmap
- [ ] Complete Facebook login integration.
- [ ] Implement explicit Room migrations.
- [ ] Add unit and UI tests.
- [ ] Enhance AI-driven personalized lesson recommendations.

---
*Note: This project is currently in active development. See `PROJECT_REVIEW.md` for the latest status and pending issues.*
