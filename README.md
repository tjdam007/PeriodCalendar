# Period Calendar 📅

A modern Android period tracking application built with Jetpack Compose and Material 3 design.

## 🚀 Features

- **Period Tracking**: Track menstrual cycles with flow intensity and symptoms
- **Cycle Statistics**: View average cycle and period lengths
- **Calendar View**: Interactive calendar with period, fertile, and ovulation day indicators
- **Material 3 Design**: Modern UI with dynamic theming support
- **Light/Dark Theme**: Automatic theme switching based on system preferences

## 🛠️ Tech Stack

### Architecture
- **MVVM**: Model-View-ViewModel pattern
- **Repository Pattern**: Data access layer abstraction
- **Dependency Injection**: Hilt for DI
- **Reactive Programming**: StateFlow for state management

### UI & Design
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material 3**: Latest Material Design components
- **Dynamic Theming**: Android 12+ Material You support
- **Custom Components**: Reusable themed components

### Data & Storage
- **Room Database**: Local data persistence
- **Kotlin Coroutines**: Asynchronous programming
- **LocalDate**: Java 8 time API for date handling

### Build & Dependencies
- **Version Catalogs**: TOML-based dependency management
- **Gradle KTS**: Kotlin DSL build scripts
- **ProGuard**: Code optimization for release builds

## 📁 Project Structure

```
app/src/main/java/com/mjandroiddev/periodcalendar/
├── data/
│   ├── database/        # Room entities, DAOs, and database
│   └── repository/      # Repository implementations
├── di/                  # Hilt dependency injection modules
├── ui/
│   ├── components/      # Reusable UI components
│   ├── navigation/      # Navigation setup
│   ├── screens/         # Screen composables (Parent-Child pattern)
│   ├── theme/          # Material 3 theme configuration
│   └── viewmodel/      # ViewModels with Hilt injection
├── MainActivity.kt      # Main activity with Compose setup
└── PeriodCalendarApplication.kt  # Hilt application class
```

## 🎨 UI Components

### Custom Components
- **PeriodButton**: Gradient button with Material ripple effects
- **CardWithTitle**: Elevated card with icon and title
- **CalendarDayCell**: Adaptive day cell for different cycle phases

### Theme Support
- **Dynamic Colors**: Automatic Material You colors on Android 12+
- **Custom Palette**: Period-themed colors for older devices
- **Light/Dark Variants**: Full theme support with proper contrast ratios

## 🔧 Version Catalog (TOML)

This project uses Gradle Version Catalogs for centralized dependency management:

### Structure
```toml
[versions]          # Version numbers
[libraries]         # Individual dependencies  
[bundles]          # Grouped dependencies
[plugins]          # Gradle plugins
```

### Key Dependencies
- **Compose BOM**: `2024.02.00`
- **Hilt**: `2.48.1`
- **Room**: `2.6.1`
- **Navigation Compose**: `2.7.6`
- **WorkManager**: `2.9.0`

### Benefits
- ✅ **Centralized Management**: All versions in one place
- ✅ **Type Safety**: Gradle validates references
- ✅ **IDE Support**: Auto-completion and refactoring
- ✅ **Consistency**: Same versions across modules
- ✅ **Bundle Support**: Group related dependencies

## 🏗️ Build Configuration

### Requirements
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Java**: 8
- **Kotlin**: 1.9.22

### Build Features
- **Compose**: Enabled
- **ProGuard**: Enabled for release builds
- **Vector Drawables**: Support library enabled

## 🚦 Getting Started

### Prerequisites
1. **Android Studio**: Hedgehog (2023.1.1) or later
2. **JDK**: 8 or higher
3. **Android SDK**: API 34

### Setup
1. Clone the repository
2. Copy `local.properties.template` to `local.properties`
3. Set your Android SDK path in `local.properties`
4. Open project in Android Studio
5. Sync project with Gradle files
6. Run the app

### Building
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## 📱 Screenshots

*Screenshots will be added once the app is running*

## 🎯 Future Enhancements

- [ ] **Notifications**: Period and ovulation reminders
- [ ] **Data Export**: CSV/PDF export functionality  
- [ ] **Sync**: Cloud backup and synchronization
- [ ] **Widgets**: Home screen widgets
- [ ] **Statistics**: Advanced analytics and insights
- [ ] **Internationalization**: Multi-language support

## 🤝 Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Material Design**: Google's design system
- **Jetpack Compose**: Modern Android UI toolkit
- **Android Architecture Components**: MVVM and lifecycle-aware components