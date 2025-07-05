# Samoan Bible App

A beautiful, bilingual Bible app featuring the Samoan Bible with English translations. This app provides daily verse notifications, easy navigation through books and chapters, and sharing functionality.

## Features

### 📖 Core Features
- **Daily Verse**: Beautiful daily verse display with both Samoan and English translations
- **Share & Copy**: Easy sharing and copying of verses with formatted text
- **Bilingual Support**: Seamless integration of Samoan and English Bible texts
- **Intuitive Navigation**: Easy browsing through books, chapters, and verses

### 📱 User Interface
- **Beautiful Design**: Material Design with custom Bible-themed colors
- **Traditional XML Layouts**: Classic Android UI implementation
- **Responsive**: Optimized for different screen sizes
- **Accessibility**: Proper content descriptions and touch targets

### 🔔 Notification System
- **Three Daily Notifications**: 
  - Morning Blessing (8:00 AM)
  - Afternoon Reflection (12:00 PM)
  - Evening Grace (8:00 PM)
- **WorkManager Integration**: Reliable scheduling even after device restart
- **Customizable**: Users can enable/disable notifications

### 📚 Bible Structure
- **All Books**: Complete Bible with 66 books
- **Old Testament**: 39 books (Genesis to Malachi)
- **New Testament**: 27 books (Matthew to Revelation)
- **Chapter Navigation**: Easy browsing by chapters
- **Verse Navigation**: Swipe between chapters in verse view

## Technical Architecture

### 🏗️ Architecture Components
- **MVVM Pattern**: Model-View-ViewModel architecture
- **Repository Pattern**: Clean data layer abstraction
- **Room Database**: Local SQLite database with prepackaged data
- **Navigation Component**: Type-safe navigation between fragments
- **ViewBinding**: Type-safe view binding
- **LiveData**: Reactive data observation

### 🛠️ Technologies Used
- **Kotlin**: 100% Kotlin implementation
- **Gradle Groovy**: Build configuration
- **Room Database**: Local data persistence
- **WorkManager**: Background task scheduling
- **Material Components**: Modern UI components
- **Navigation Component**: Fragment navigation
- **ViewPager2**: Tab-based navigation

### 📦 Dependencies
```gradle
// Core Android
implementation "androidx.core:core-ktx:1.12.0"
implementation "androidx.appcompat:appcompat:1.6.1"
implementation "com.google.android.material:material:1.10.0"

// Navigation
implementation "androidx.navigation:navigation-fragment-ktx:2.7.3"
implementation "androidx.navigation:navigation-ui-ktx:2.7.3"

// Lifecycle
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.7.0"

// Room Database
implementation "androidx.room:room-runtime:2.5.0"
implementation "androidx.room:room-ktx:2.5.0"
kapt "androidx.room:room-compiler:2.5.0"

// WorkManager
implementation "androidx.work:work-runtime-ktx:2.8.1"
```

## Project Structure

```
app/
├── src/main/
│   ├── assets/databases/          # Prepackaged Bible databases
│   │   ├── samoan_bible.db       # Samoan Bible (SMO.db)
│   │   └── english_bible.db      # English Bible (KJVA.SQLite3)
│   ├── java/com/elian/samoanbible/
│   │   ├── data/                 # Data layer
│   │   │   ├── dao/             # Database access objects
│   │   │   ├── database/        # Room database classes
│   │   │   ├── entity/          # Database entities
│   │   │   └── repository/      # Repository pattern implementation
│   │   ├── notifications/       # Notification system
│   │   │   ├── DailyVerseWorker.kt
│   │   │   └── DailyVerseReceiver.kt
│   │   ├── ui/                  # UI layer
│   │   │   ├── books/           # Books fragment and components
│   │   │   ├── chapters/        # Chapters fragment
│   │   │   ├── dailyverse/      # Daily verse fragment
│   │   │   ├── verses/          # Verses fragment
│   │   │   └── versetext/       # Verse text fragment
│   │   ├── MainActivity.kt      # Main activity
│   │   └── SamoanBibleApplication.kt
│   └── res/
│       ├── drawable/            # Vector drawables and icons
│       ├── layout/              # XML layouts
│       ├── menu/               # Navigation menus
│       ├── navigation/         # Navigation graph
│       ├── values/             # Resources (strings, colors, styles)
│       └── xml/                # Backup and data extraction rules
```

## Database Schema

### Verses Table
```sql
CREATE TABLE verses (
    id INTEGER PRIMARY KEY,
    bookNumber INTEGER NOT NULL,
    bookName TEXT NOT NULL,
    chapter INTEGER NOT NULL,
    verse INTEGER NOT NULL,
    text TEXT NOT NULL,
    highlight TEXT NOT NULL,
    title TEXT NOT NULL,
    favourite TEXT NOT NULL,
    date TEXT NOT NULL,
    intro TEXT NOT NULL
);
```

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Kotlin 1.9.10 or later
- Android SDK 24 (API level 24) or higher
- Gradle 8.1

### Installation
1. Clone the repository
2. Open in Android Studio
3. Build and run the project
4. The app will automatically copy the Bible databases on first launch

### Configuration
The app includes:
- Samoan Bible (SMO.db) - Original Samoan translation
- English Bible (KJVA.SQLite3) - King James Version with Apocrypha

## App Flow

### 📱 Navigation Flow
1. **Daily Verse** (Home) → Shows today's verse with share/copy options
2. **Books** → Three tabs: All Books, Old Testament, New Testament
3. **Chapters** → Grid of chapters for selected book
4. **Verses** → List of verses in selected chapter
5. **Verse Text** → Full verse display with swipe navigation

### 🔔 Notification Flow
1. App schedules three daily notifications on startup
2. WorkManager handles notification delivery
3. Notifications include random verses from database
4. Tapping notification opens the app to daily verse screen

## Customization

### Adding New Bible Translations
1. Create new Room database with same schema
2. Add new DAO and database class
3. Update repository to handle multiple translations
4. Modify UI to show translation selection

### Modifying Notification Times
Update the scheduling in `DailyVerseWorker.kt`:
```kotlin
// Schedule morning notification (8:00 AM)
scheduleNotification(context, TYPE_MORNING, 8, 0)

// Schedule afternoon notification (12:00 PM)
scheduleNotification(context, TYPE_AFTERNOON, 12, 0)

// Schedule evening notification (8:00 PM)
scheduleNotification(context, TYPE_EVENING, 20, 0)
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Samoan Bible text from the original Samoan translation
- English Bible text from the King James Version with Apocrypha
- Material Design guidelines for UI inspiration
- Android Architecture Components for technical implementation

## Support

For support or questions, please create an issue in the GitHub repository.

---

**Package Name**: com.elian.samoanbible  
**App Name**: Samoan Bible  
**Version**: 1.0  
**Target SDK**: 34  
**Minimum SDK**: 24