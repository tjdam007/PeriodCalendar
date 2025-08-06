# Period Calendar Database Schema

## Overview
The Period Calendar app uses Room ORM with a comprehensive database schema designed for tracking menstrual cycles, moods, symptoms, and user preferences.

## Database Version: 2

## Entities

### 1. CycleEntry
Primary entity for tracking daily cycle information.

```kotlin
@Entity(tableName = "cycle_entries")
data class CycleEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDate,
    val isPeriod: Boolean,
    val flowLevel: String, // "none", "light", "medium", "heavy", "very_heavy"
    val mood: String, // "happy", "sad", "angry", "anxious", "calm", "energetic", "tired"
    val cramps: String // "none", "mild", "moderate", "severe"
)
```

**Fields:**
- `id`: Auto-incrementing primary key
- `date`: The date for this entry (LocalDate)
- `isPeriod`: Whether this is a period day
- `flowLevel`: Flow intensity level (enum values as strings)
- `mood`: User's mood for the day (enum values as strings)
- `cramps`: Cramps severity level (enum values as strings)

### 2. UserSettings
Single-row table for user preferences and settings.

```kotlin
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1, // Always 1 to ensure single row
    val avgCycleLength: Int = 28,
    val periodDuration: Int = 5,
    val notifBeforePeriod: Int = 1,
    val notifOvulation: Boolean = true,
    val notifFertileWindow: Boolean = true,
    val themeMode: String = "system"
)
```

**Fields:**
- `id`: Always 1 (single row constraint)
- `avgCycleLength`: User's average cycle length in days (default: 28)
- `periodDuration`: Average period duration in days (default: 5)
- `notifBeforePeriod`: Days before period to send notifications (default: 1)
- `notifOvulation`: Enable ovulation day notifications (default: true)
- `notifFertileWindow`: Enable fertile window notifications (default: true)
- `themeMode`: App theme ("light", "dark", "system", default: "system")

## Data Access Objects (DAOs)

### CycleEntryDao
Provides comprehensive data access methods for cycle entries.

**Key Methods:**
- `getAllEntries()`: Flow of all entries ordered by date
- `getEntriesInRange(startDate, endDate)`: Entries within date range
- `getPeriodEntries()`: Flow of period-only entries
- `getEntryByDate(date)`: Single entry for specific date
- `getLastPeriodEntry()`: Most recent period entry
- `getAverageCycleLength()`: Calculated average cycle length
- `getAveragePeriodLength()`: Calculated average period duration
- `insertEntry(entry)`: Insert or replace entry
- `updateEntry(entry)`: Update existing entry
- `deleteEntry(entry)`: Delete entry

### UserSettingsDao
Manages user settings with both reactive and one-time access methods.

**Key Methods:**
- `getUserSettings()`: Flow of current settings
- `getUserSettingsOnce()`: Single-time settings access
- `insertOrUpdateSettings(settings)`: Insert or update settings
- `updateAvgCycleLength(length)`: Update only cycle length
- `updateThemeMode(mode)`: Update only theme mode
- Individual update methods for all settings fields

## Repository Layer

### CycleEntryRepository
Business logic layer for cycle entries with convenience methods.

**Key Features:**
- Reactive data access with Flow
- Convenience methods for common operations:
  - `addOrUpdateDayEntry()`: Smart insert/update for daily entries
  - `markPeriodDays()`: Mark date range as period days
  - `updateMoodForDate()`: Update mood for specific date
  - `updateCrampsForDate()`: Update cramps for specific date
- Statistics calculations
- Data validation

### UserSettingsRepository
Manages user settings with validation and convenience methods.

**Key Features:**
- Automatic default settings creation
- Individual setting updates
- Theme and notification helpers
- Cycle calculation utilities
- Settings validation

## Data Models & Enums

### FlowLevel
```kotlin
enum class FlowLevel(val displayName: String, val value: String) {
    NONE("No Flow", "none"),
    LIGHT("Light", "light"),
    MEDIUM("Medium", "medium"),
    HEAVY("Heavy", "heavy"),
    VERY_HEAVY("Very Heavy", "very_heavy")
}
```

### Mood
```kotlin
enum class Mood(val displayName: String, val value: String, val emoji: String) {
    NONE("Not Specified", "", ""),
    HAPPY("Happy", "happy", "😊"),
    SAD("Sad", "sad", "😢"),
    ANGRY("Angry", "angry", "😠"),
    ANXIOUS("Anxious", "anxious", "😰"),
    CALM("Calm", "calm", "😌"),
    ENERGETIC("Energetic", "energetic", "⚡"),
    TIRED("Tired", "tired", "😴")
}
```

### CrampsLevel
```kotlin
enum class CrampsLevel(val displayName: String, val value: String) {
    NONE("No Cramps", "none"),
    MILD("Mild", "mild"),
    MODERATE("Moderate", "moderate"),
    SEVERE("Severe", "severe")
}
```

### ThemeMode
```kotlin
enum class ThemeMode(val displayName: String, val value: String) {
    LIGHT("Light", "light"),
    DARK("Dark", "dark"),
    SYSTEM("System Default", "system")
}
```

## Extension Functions

### CycleEntry Extensions
- `getFlowLevelEnum()`: Convert string to enum
- `getMoodEnum()`: Convert string to enum
- `getCrampsLevelEnum()`: Convert string to enum
- `withFlowLevel()`: Create copy with new flow level
- `getDisplayMood()`: Get mood with emoji
- `isValid()`: Validate entry data
- `hasAnySymptoms()`: Check if entry has any tracked data

### UserSettings Extensions
- `getThemeModeEnum()`: Convert theme string to enum
- `calculateNextPeriodDate()`: Predict next period start
- `calculateOvulationDate()`: Predict ovulation day
- `calculateFertileWindow()`: Calculate fertile window dates
- `getCycleSummary()`: Human-readable cycle info
- `getNotificationSummary()`: Summary of enabled notifications

## Type Converters

Room requires type converters for LocalDate:

```kotlin
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()
    
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? = 
        dateString?.let { LocalDate.parse(it) }
}
```

## Dependency Injection (Hilt)

The `DatabaseModule` provides all necessary dependencies:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun providePeriodDatabase(@ApplicationContext context: Context): PeriodDatabase
    
    @Provides
    fun provideCycleEntryDao(database: PeriodDatabase): CycleEntryDao
    
    @Provides
    fun provideUserSettingsDao(database: PeriodDatabase): UserSettingsDao
}
```

## Usage Examples

### Recording a Period Day
```kotlin
// Using repository
val repository: CycleEntryRepository = // injected
repository.addOrUpdateDayEntry(
    date = LocalDate.now(),
    isPeriod = true,
    flowLevel = FlowLevel.MEDIUM.value,
    mood = Mood.TIRED.value,
    cramps = CrampsLevel.MILD.value
)
```

### Updating User Settings
```kotlin
// Using repository
val settingsRepo: UserSettingsRepository = // injected
settingsRepo.updateAvgCycleLength(30)
settingsRepo.updateThemeMode(ThemeMode.DARK.value)
```

### Observing Data Changes
```kotlin
// In ViewModel
class CalendarViewModel @Inject constructor(
    private val cycleEntryRepository: CycleEntryRepository,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {
    
    val cycleEntries = cycleEntryRepository.getAllEntries()
    val userSettings = userSettingsRepository.getUserSettings()
    
    fun updateMood(date: LocalDate, mood: Mood) {
        viewModelScope.launch {
            cycleEntryRepository.updateMoodForDate(date, mood.value)
        }
    }
}
```

## Migration Strategy

The database includes both new entities and legacy entities for smooth migration:
- **New**: CycleEntry, UserSettings (recommended for new features)
- **Legacy**: PeriodEntity, CycleEntity (maintained for compatibility)

For production deployment, implement proper migration strategy instead of `fallbackToDestructiveMigration()`.

## Performance Considerations

1. **Indexes**: Consider adding indexes on frequently queried columns:
   - `CREATE INDEX index_cycle_entries_date ON cycle_entries(date)`
   - `CREATE INDEX index_cycle_entries_is_period ON cycle_entries(isPeriod)`

2. **Queries**: All complex queries use proper SQL with LIMIT clauses where appropriate

3. **Background Threading**: All database operations are suspension functions for proper coroutine usage

## Validation Rules

### CycleEntry
- `flowLevel` must be one of the FlowLevel enum values
- `mood` can be empty or one of the Mood enum values  
- `cramps` must be one of the CrampsLevel enum values
- `date` should not be in the future

### UserSettings
- `avgCycleLength`: 15-45 days (reasonable range)
- `periodDuration`: 1-10 days (reasonable range)
- `notifBeforePeriod`: 0-7 days
- `themeMode` must be one of the ThemeMode enum values

This schema provides a robust foundation for tracking menstrual health data while maintaining flexibility for future enhancements.