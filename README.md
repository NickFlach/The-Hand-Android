# The Hand - Android

A private ledger for recording what you built, who you helped, and what you learned.

**No scores. No streaks. No audience.**

---

## What This Is

The Hand is a personal record. It exists for one purpose: to hold what you did, so you don't have to.

You built something. You helped someone. You learned a hard truth. Write it down. Move on.

This is not a productivity app. There are no goals to hit, no habits to track, no graphs climbing upward. The Hand doesn't care how often you use it. It won't remind you. It won't congratulate you.

It will remember.

---

## Features

- **Three Entry Types**: Built, Helped, Learned
- **24-Hour Edit Window**: Edit entries for 24 hours, then they lock
- **Addendums**: Add context after the 24-hour window
- **Responsibility Threads**: Connect entries across time to ongoing work
- **Trusted Hands** (V3): Share specific entries with up to 3 witnesses
- **Archive**: Browse entries by year and month
- **Patterns**: See distribution of entry types over time
- **Export**: Your data as plain text or JSON
- **Local-First**: All data stored on device, no cloud sync
- **Private by Default**: No accounts, no servers, no tracking

---

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM with Hilt dependency injection
- **Database**: Room (SQLite)
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)

---

## Project Structure

```
app/src/main/java/com/thehand/android/
├── data/
│   ├── dao/              # Room DAOs
│   ├── model/            # Data models
│   ├── repository/       # Repository pattern
│   ├── TheHandDatabase.kt
│   └── Converters.kt
├── di/                   # Hilt dependency injection
├── ui/
│   ├── screens/          # Compose screens
│   ├── viewmodel/        # ViewModels
│   ├── theme/            # Material 3 theme
│   └── TheHandApp.kt
├── MainActivity.kt
└── TheHandApplication.kt
```

---

## Building

### Prerequisites

- Android Studio Ladybug | 2024.2.1 or later
- JDK 17
- Android SDK 35

### Build & Run

```bash
# Clone the repository
git clone https://github.com/NickFlach/The-Hand-Android.git
cd The-Hand-Android

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or open in Android Studio and run
```

---

## Design Principles

The Hand follows these rules without exception:

- **Private by default**: Nothing leaves your device unless you explicitly share it
- **Local-first**: No accounts. No servers holding your data. No sync.
- **No behavioral nudges**: No "You haven't logged in 3 days!" No push notifications.
- **Neutral tone**: The app never praises you. It never scolds you. It simply records.
- **Silence is acceptable**: Going weeks without an entry is fine. The app won't guilt you.

---

## Data Privacy

- ✅ **All data stored locally** using Room database
- ✅ **No network requests** (except for Trusted Hands feature if implemented)
- ✅ **No analytics or tracking**
- ✅ **No cloud backup** (by design)
- ✅ **Export anytime** - your data belongs to you

Cloud backup and device transfer are explicitly disabled in `data_extraction_rules.xml`.

---

## Version

**1.0.0** - Native Android implementation

Based on the original React Native version by NickFlach.

---

## Final Note

The Hand exists because some things deserve to be recorded even when no one else will see them.

You don't need an audience to do meaningful work. You don't need streaks to build discipline. You don't need points to prove your value.

You just need a place to put it down.

This is that place.

---

*A private ledger. Nothing more. Nothing less.*
