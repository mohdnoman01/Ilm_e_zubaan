# Quick Project Review – Ilm-e-Zubaan

## High-level impression
- The app has a clean baseline architecture for an Android Compose project: Room, ViewModels, repositories, navigation, and Firebase integration are all present.
- Main risks are around **authentication/security**, **dependency/version management consistency**, and **error handling/observability**.

## What looks good
- Clear layered separation across UI, ViewModels, local data, and repositories.
- Room is used as a local source of truth for concepts/metadata.
- Compose navigation graph is readable and organized around major screens.
- User streak/xp progression logic is simple and understandable.

## Key issues observed

### 1) Authentication is currently not production-safe (High)
- Credentials are being stored in SharedPreferences as plaintext (`putString(identifier, password)`), which is insecure for real users.
- Google sign-in uses a placeholder server client ID (`"YOUR_SERVER_CLIENT_ID"`), so that flow is not production-ready.
- Facebook button currently shortcuts directly to success (`onClick = { onLoginSuccess() }`).

**Impact:** Security/compliance risk and potentially misleading login UX.

**Suggested fix:** Move to Firebase Auth or encrypted credential storage (Jetpack Security), hash/salt passwords if local auth remains, and wire real OAuth providers.

### 2) Dependency/version drift and hardcoded versions (Medium)
- Version catalog exists, but some dependencies are hardcoded directly in `app/build.gradle.kts` (Navigation, Room, Firebase BOM, coroutines play-services).
- Duplicate Gson entries exist in the version catalog (`gson` and `google-gson`).

**Impact:** Harder upgrades and inconsistent dependency governance.

**Suggested fix:** Centralize all versions in `libs.versions.toml` and remove duplicates.

### 3) Error handling and logging are minimal (Medium)
- Firestore sync catches broad exceptions and only prints stack trace.
- No retry/backoff or surfaced UI state for sync failures.

**Impact:** Failures are silent for users and hard to monitor.

**Suggested fix:** Expose sync state/errors through ViewModel state, and add structured logging (e.g., Timber/Crashlytics).

### 4) Data migration strategy is destructive (Medium)
- Room builder uses `fallbackToDestructiveMigration()`.

**Impact:** Local data loss on schema changes.

**Suggested fix:** Add explicit migrations before production release.

### 5) Small code quality signals (Low)
- There are unused imports and a placeholder repository file (`LessonRepository.kt` is effectively deleted content).
- Some business dependencies (database/repositories) are instantiated inside composable graph instead of DI container.

**Impact:** Technical debt and harder testability over time.

**Suggested fix:** Introduce Hilt/Koin (or manual DI composition root) and clean dead files/imports.

## Prioritized next steps (1–2 sprints)
1. Replace current login with secure auth flow (Firebase Auth + proper OAuth setup).
2. Introduce dependency injection and move object creation out of composables.
3. Normalize dependency versions through `libs.versions.toml` only.
4. Replace destructive migrations with explicit Room migrations.
5. Add unit tests for streak logic and repository sync mapping edge-cases.

## Validation note
- Attempted to run unit tests with Gradle wrapper, but dependency download was blocked by proxy/network policy in this environment.
