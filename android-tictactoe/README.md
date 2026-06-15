# Advanced Tic-Tac-Toe 3D — Native Android (Kotlin)

Premium, production-ready Tic-Tac-Toe game with futuristic 2026 UI.

## Features

| Category | Details |
|---|---|
| **UI** | Glassmorphism + Neon effects, particle system, 60FPS animations |
| **Themes** | Cyberpunk, Neon Blue, Gold Luxury, Galaxy, Space, Fire, Ice, Royal Purple |
| **Game Modes** | vs AI, Local 2-Player, Online Multiplayer, Tournament, Quick Match |
| **Board Sizes** | 3×3, 4×4, 5×5, 6×6, Custom |
| **AI** | 5 levels: Easy → Impossible (Minimax + Alpha-Beta Pruning) |
| **Achievements** | 50+ achievements with XP & coin rewards |
| **Ranking** | Bronze → Legend rank system |
| **Social** | Leaderboard (global / country / friends), profiles |
| **Accessibility** | Colorblind mode, large text, one-handed mode |
| **Settings** | Graphics quality, sound/music/haptic controls, dark/light mode |

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + StateFlow + Hilt DI
- **Database**: Room (local), Firebase Firestore (cloud)
- **Auth**: Firebase Authentication
- **Realtime**: Firebase Realtime Database
- **AI**: Custom Minimax with Alpha-Beta Pruning
- **Animations**: Compose Animation + Canvas particle system

## Local Setup

```bash
# 1. Clone the repo
git clone https://github.com/YOUR_USERNAME/YOUR_REPO
cd YOUR_REPO/android-tictactoe

# 2. Bootstrap the Gradle wrapper jar
bash setup-wrapper.sh

# 3. Open in Android Studio (Hedgehog or later) — recommended
# OR build from CLI:
./gradlew assembleFreeDebug
```

### Firebase Setup (required for online features)

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a project
3. Add Android app with package: `com.advancedtictactoe.game`
4. Download `google-services.json` → replace `app/google-services.json`
5. Enable: **Firestore**, **Authentication** (Google + Email), **Realtime Database**

> Without Firebase, offline features (vs AI, local 2-player, achievements, stats) work fine.

## GitHub Actions — Auto APK Release

Pushing a version tag triggers the CI to build and publish a pre-release:

```bash
git tag v1.0.0
git push origin v1.0.0
```

This will:
- Build both **debug** and **release** APKs
- Create a GitHub **pre-release** with the APKs attached
- Show a full changelog

### Required GitHub Secrets (for signed release APK)

| Secret | Description |
|---|---|
| `KEYSTORE_BASE64` | Base64-encoded `.jks` keystore file |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

Generate a keystore:
```bash
keytool -genkey -v -keystore release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias tictactoe
base64 -w 0 release.jks  # → paste this as KEYSTORE_BASE64 secret
```

> Without keystore secrets, the workflow builds a **debug-signed** APK (still installable for testing).

## Build Variants

| Flavor | Build Type | Package |
|---|---|---|
| free + debug | Debug, debuggable | `com.advancedtictactoe.game.free.debug` |
| free + release | Optimized, minified | `com.advancedtictactoe.game.free` |
| premium + debug | Debug | `com.advancedtictactoe.game.premium.debug` |
| premium + release | Optimized | `com.advancedtictactoe.game.premium` |

## Requirements

- Android 8.0+ (API 26)
- ~50 MB storage
- Internet (for online multiplayer + cloud sync only)

## Project Structure

```
app/src/main/java/com/advancedtictactoe/game/
├── data/
│   ├── local/          Room database + DAOs
│   ├── model/          Data classes + AchievementsData
│   ├── preferences/    DataStore (AppPreferences)
│   └── repository/     GameRepository
├── di/                 Hilt AppModule
├── game/
│   ├── ai/             AIPlayer (Minimax + Alpha-Beta)
│   └── engine/         GameEngine (board eval, move gen)
├── ui/
│   ├── achievements/   AchievementsScreen
│   ├── game/           GameScreen + ViewModel + components
│   ├── home/           HomeScreen
│   ├── leaderboard/    LeaderboardScreen
│   ├── navigation/     AppNavigation + Screen routes
│   ├── onboarding/     OnboardingScreen (7 pages)
│   ├── profile/        ProfileScreen
│   ├── settings/       SettingsScreen
│   ├── splash/         SplashScreen
│   └── theme/          Theme + Colors + ThemeSelectorScreen
└── utils/
    ├── HapticManager.kt
    └── SoundManager.kt
```
