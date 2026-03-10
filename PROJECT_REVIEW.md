# Ilm‑e‑Zaban — Project Understanding & MVVM Alignment

## 1) Confirmed Product Purpose
Ilm‑e‑Zaban is a **Pakistan‑focused** language learning and communication support app for users from different regions who speak different native languages.

### Core intent
- Help users communicate across Pakistani regional languages (including Pashto, Punjabi, Sindhi, Urdu, Balochi, and related regional variants).
- Provide practical learning using **video, audio, and text** lessons.
- Support pronunciation and comprehension for daily communication use-cases.
- Provide an **AI assistant (Gemini)** for explaining words/phrases in simple language.
- Support **low-connectivity/offline usage** via local storage.

This is **not** positioned as a global/international language platform; it is region-specific to Pakistan’s multilingual context.

## 2) MVVM Architecture (Target Model)
The project follows an MVVM structure:
- **View**: Jetpack Compose screens + user interaction handling.
- **ViewModel**: UI state, business logic, async orchestration.
- **Model/Data**: repositories, Room entities/DAO, Firebase integration, and AI provider integration.

## 3) View Layer (UI)
Compose screens should focus on:
- rendering state from ViewModel,
- forwarding user actions/events,
- avoiding direct business/data logic.

Typical screens in this product direction:
- Home screen (language/lesson entry points),
- Lesson list/detail,
- Audio/Video player,
- Text lesson (script + transliteration),
- AI assistant/chat support screen.

## 4) ViewModel Layer (State + Logic)
ViewModels should:
- expose immutable UI state (`StateFlow` preferred),
- handle loading/error/success states,
- coordinate repositories,
- send AI prompts and transform responses for UI.

Typical responsibilities:
- `loadLessons(language)`
- `loadLesson(lessonId)`
- `syncIfNeeded()`
- `askAi(promptContext)`

## 5) Model/Data Layer (Online + Offline)
Data layer should combine:
- **Firebase Firestore** for lesson metadata/content structure,
- **Firebase Storage** for media assets (video/audio),
- **Room Database** for offline cache and low-connectivity access,
- **Repository policy** to decide online-first/offline fallback behavior.

Expected repository rule:
- If network is available: fetch latest, persist locally.
- If network is unavailable: serve cached Room data.

## 6) AI Integration (Gemini)
AI feature goal:
- explain words/phrases,
- provide pronunciation help,
- give simple example usage.

Implementation principle:
- prompt construction and API handling in ViewModel/Repository,
- UI only submits intent (e.g., “Explain this word”) and renders response state.

## 7) Current Snapshot vs Target Direction
Based on repository inspection, the codebase already has important building blocks (Compose navigation, ViewModels, Room, Firebase hooks), but should be aligned further toward the product model above.

### Priority alignment items
1. Harden authentication/security flows and remove placeholder/social-login shortcuts.
2. Strengthen repository strategy for explicit online/offline behavior.
3. Improve ViewModel-driven loading/error states for sync and AI operations.
4. Remove destructive migration strategy before production usage.
5. Continue refactoring toward cleaner DI boundaries and testability.

## 8) Working Agreement for Next Tasks
For upcoming implementation tasks, I will treat this as the canonical direction:
- Pakistan-focused multilingual communication app,
- MVVM with clear View/ViewModel/Data boundaries,
- offline-capable lesson experience,
- Gemini-assisted comprehension workflow.
