# HealthMate
Personal Health & Wellness Tracker App

Native Android app (Kotlin + Jetpack Compose) for recording daily steps, calories burned, water intake and sleep, stored locally in SQLite via Room.

## Tech stack
| Area | Choice |
| --- | --- |
| Language / UI | Kotlin, Jetpack Compose, Material 3 |
| Architecture | MVVM, feature-based packages |
| State management | ViewModel + Kotlin StateFlow |
| Database | SQLite via Room (`health_records` table) |
| Navigation | Navigation Compose |
| Settings | DataStore Preferences |
| Min / target SDK | 26 (Android 8.0) / 37 |

## Project structure
```
app/src/main/java/com/esandi/healthmate/
├── HealthMateApplication.kt   App entry point; creates the AppContainer
├── MainActivity.kt            Single activity hosting all Compose screens
├── di/                        AppContainer: shared database + repository (manual dependency injection)
├── data/
│   ├── local/                 Room entity, DAO and database (SQLite)
│   └── repository/            Repository: the single source of truth the ViewModels use
├── navigation/                Routes for every screen and the navigation graph
├── ui/
│   ├── theme/                 Colours, typography and shapes (light + dark)
│   └── components/            Reusable UI building blocks (cards, rings, buttons)
└── feature/                   One package per feature, each with its Screen + ViewModel
    ├── dashboard/             Home: today's summary
    ├── history/               Past records with date search and filters
    ├── record/                Add / edit / view a health record
    ├── progress/              Charts and averages
    ├── profile/               Profile, goals and settings
    └── onboarding/            First-launch setup
```
