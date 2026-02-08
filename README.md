# Task Flow

Task Flow is a modern, feature-rich task management Android application designed to help users plan, track, and optimize their daily productivity. The app is built using Jetpack Compose and Material 3, following modern Android development best practices with a clean architecture and scalable codebase.

## Overview

Task Flow focuses on simplicity, performance, and usability. It supports personal task management needs such as prioritization, categorization, reminders, and progress tracking, while maintaining an intuitive and visually consistent user experience across devices.

## Developer

**Rehema Thomas**

## Key Features

* Create, edit, and delete tasks
* Task priority levels (High, Medium, Low)
* Task categories and tags for better organization
* Subtasks support for complex tasks
* Calendar view with task indicators
* Smart reminders and notifications
* Productivity statistics and insights
* Dark mode support
* Multi-language support (English and Swahili)
* Advanced search with filters
* Export tasks to CSV format
* Material 3 compliant design system

## Tech Stack

* **Programming Language:** Kotlin 1.9.22
* **UI Framework:** Jetpack Compose (BOM 2026.01.01)
* **Architecture Pattern:** MVVM with Repository Pattern
* **Database:** Room 2.6.1
* **Asynchronous Processing:** Kotlin Coroutines and Flow
* **Navigation:** Navigation Compose
* **Background Tasks:** WorkManager
* **Local Storage:** DataStore Preferences
* **Minimum SDK:** 24 (Android 7.0)
* **Target SDK:** 34 (Android 14)

## Project Structure

```
app/
├── data/
│   ├── dao/            # Database access objects
│   ├── database/       # Room database setup and converters
│   ├── entity/         # Data entities
│   ├── preferences/    # DataStore preferences
│   └── repository/     # Data repositories
├── ui/
│   ├── components/     # Reusable UI components
│   ├── navigation/     # Navigation configuration
│   ├── screens/        # Application screens
│   └── theme/          # Material 3 theming
├── viewmodel/          # ViewModels for state management
├── utils/              # Utility and helper classes
└── work/               # Background workers
```

## Setup Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/rehemathomas/TaskFlow.git
   cd TaskFlow
   ```
2. Open the project in Android Studio (Hedgehog | 2023.1.1 or newer recommended).
3. Sync Gradle files.
4. Run the application on an emulator or physical device running API level 24 or higher.

## Building the Project

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## License

This project is licensed under the MIT License. See the LICENSE file for more details.

## Contributing

Contributions are welcome. Please refer to the CONTRIBUTING.md file for development guidelines, coding standards, and contribution procedures.

## Privacy

User data handling and privacy practices are documented in the PRIVACY.md file.
