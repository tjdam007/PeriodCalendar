# Cycle Prediction Utility Examples

## Overview
The `CyclePredictionUtil` provides accurate menstrual cycle predictions based on medical standards and java.time.LocalDate handling.

## Basic Usage

### 1. Basic Cycle Prediction
```kotlin
import com.mjandroiddev.periodcalendar.utils.CyclePredictionUtil
import java.time.LocalDate

// Last period started January 1, 2024
val lastPeriodDate = LocalDate.of(2024, 1, 1)
val avgCycleLength = 28

// Get next period date
val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, avgCycleLength)
// Result: 2024-01-29

// Get ovulation date
val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
// Result: 2024-01-15 (14 days before next period)

// Get fertile window
val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)
// Result: (2024-01-10, 2024-01-15) - 6 days total
```

### 2. Comprehensive Prediction
```kotlin
// Get all predictions at once
val prediction = CyclePredictionUtil.predictCycle(lastPeriodDate, avgCycleLength)

println(prediction.getSummary())
// Output:
// Next Period: 2024-01-29 (in 28 days)
// Ovulation: 2024-01-15 (in 14 days) 
// Fertile Window: 2024-01-10 to 2024-01-15
```

### 3. Multiple Future Cycles
```kotlin
// Predict next 3 cycles
val multiplePredictions = CyclePredictionUtil.predictMultipleCycles(
    lastPeriodDate = lastPeriodDate,
    avgCycleLength = 28,
    numberOfCycles = 3
)

multiplePredictions.forEach { prediction ->
    println("Period: ${prediction.nextPeriodDate}, Ovulation: ${prediction.ovulationDate}")
}
// Output:
// Period: 2024-01-29, Ovulation: 2024-01-15
// Period: 2024-02-26, Ovulation: 2024-02-12  
// Period: 2024-03-25, Ovulation: 2024-03-11
```

## Different Cycle Lengths

### Regular 28-Day Cycle
```kotlin
val lastPeriod = LocalDate.of(2024, 1, 1)
val prediction = CyclePredictionUtil.predictCycle(lastPeriod, 28)

// Next period: January 29 (1 + 28)
// Ovulation: January 15 (29 - 14)  
// Fertile window: January 10-15
```

### Short 21-Day Cycle
```kotlin
val prediction = CyclePredictionUtil.predictCycle(lastPeriod, 21)

// Next period: January 22 (1 + 21)
// Ovulation: January 8 (22 - 14)
// Fertile window: January 3-8
```

### Long 35-Day Cycle  
```kotlin
val prediction = CyclePredictionUtil.predictCycle(lastPeriod, 35)

// Next period: February 5 (1 + 35)
// Ovulation: January 22 (Feb 5 - 14)
// Fertile window: January 17-22
```

### Irregular Cycles
```kotlin
val irregularLengths = listOf(25, 32, 28, 35, 30)
val lastPeriod = LocalDate.of(2024, 1, 1)

irregularLengths.forEach { length ->
    val prediction = CyclePredictionUtil.predictCycle(lastPeriod, length)
    println("Cycle $length days: Next period ${prediction.nextPeriodDate}")
}
```

## Utility Functions

### Check Fertile Days
```kotlin
val date = LocalDate.of(2024, 1, 12)
val isInFertileWindow = CyclePredictionUtil.isInFertileWindow(
    date = date,
    lastPeriodDate = lastPeriod, 
    avgCycleLength = 28
)
// Result: true (January 12 is within January 10-15 fertile window)
```

### Check Ovulation Day
```kotlin
val isOvulationDay = CyclePredictionUtil.isOvulationDay(
    date = LocalDate.of(2024, 1, 15),
    lastPeriodDate = lastPeriod,
    avgCycleLength = 28  
)
// Result: true
```

### Current Cycle Day
```kotlin
val cycleDay = CyclePredictionUtil.getCurrentCycleDay(
    lastPeriodDate = LocalDate.of(2024, 1, 1),
    currentDate = LocalDate.of(2024, 1, 15)
)
// Result: 15 (15th day of current cycle)
```

## Integration with Database

### Using with UserSettings
```kotlin
val userSettings = UserSettings(avgCycleLength = 30, periodDuration = 5)
val lastPeriod = LocalDate.of(2024, 1, 1)

// Extension function usage
val prediction = userSettings.getNextPeriodPrediction(lastPeriod)
val isInFertileWindow = userSettings.isDateInFertileWindow(LocalDate.now(), lastPeriod)
val currentDay = userSettings.getCurrentCycleDay(lastPeriod)
```

### Using with Cycle Entries
```kotlin
val cycleEntries = listOf(
    CycleEntry(date = LocalDate.of(2024, 1, 1), isPeriod = true),
    CycleEntry(date = LocalDate.of(2024, 1, 2), isPeriod = true),
    // ... more entries
)

// Get last period start date from entries
val lastPeriodDate = cycleEntries.getLastPeriodStartDate()

// Calculate actual cycle lengths from historical data
val actualCycleLengths = cycleEntries.calculateActualCycleLengths()

// Get enhanced predictions using historical data
val enhancedPredictions = userSettings.getEnhancedPredictions(cycleEntries, 3)
```

### Comprehensive Cycle Summary
```kotlin
val summary = userSettings.createCycleSummary(cycleEntries)

println("Last Period: ${summary.lastPeriodDate}")
println("Current Cycle Day: ${summary.currentCycleDay}")
println("Average Cycle Length: ${summary.averageCycleLength} days")
println("Is Regular: ${summary.isRegular}")
println("Next Period: ${summary.nextPrediction?.nextPeriodDate}")
```

## Edge Cases Handled

### Month Boundaries
```kotlin
// Period at end of January
val lastPeriod = LocalDate.of(2024, 1, 20)
val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriod, 28)
// Result: 2024-02-17 (crosses into February)
```

### Year Boundaries  
```kotlin
// Period at end of December
val lastPeriod = LocalDate.of(2023, 12, 20)
val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriod, 28)
// Result: 2024-01-17 (crosses into next year)
```

### Leap Year
```kotlin
// February 2024 (leap year)
val lastPeriod = LocalDate.of(2024, 2, 15)
val prediction = CyclePredictionUtil.predictCycle(lastPeriod, 28)
val ovulation = prediction.ovulationDate
// Result: 2024-02-29 (leap day ovulation!)
```

## Validation and Error Handling

### Cycle Length Validation
```kotlin
try {
    CyclePredictionUtil.getNextPeriodDate(lastPeriod, 50) // Too long
} catch (e: IllegalArgumentException) {
    println("Cycle length must be between 15 and 45 days")
}

try {
    CyclePredictionUtil.getNextPeriodDate(lastPeriod, 10) // Too short  
} catch (e: IllegalArgumentException) {
    println("Cycle length must be between 15 and 45 days")
}
```

### Multiple Cycles Validation
```kotlin
try {
    CyclePredictionUtil.predictMultipleCycles(lastPeriod, 28, 0) // Invalid count
} catch (e: IllegalArgumentException) {
    println("Number of cycles must be positive")
}

try {
    CyclePredictionUtil.predictMultipleCycles(lastPeriod, 28, 15) // Too many
} catch (e: IllegalArgumentException) {
    println("Number of cycles should not exceed 12")
}
```

## Medical Accuracy

The predictions are based on standard medical knowledge:

- **Luteal Phase**: Always 14 days (ovulation to next period)
- **Fertile Window**: 6 days (5 days before ovulation + ovulation day)
- **Cycle Length Range**: 15-45 days (medically valid range)

### Why 14 Days for Luteal Phase?
The luteal phase length is relatively consistent across women (~14 days), while the follicular phase (period to ovulation) varies. This is why we subtract 14 days from the next period to predict ovulation.

### Why 6-Day Fertile Window?
- Sperm can survive up to 5 days in the reproductive tract
- The egg survives ~24 hours after ovulation  
- This gives a practical fertile window of 6 days total

## Testing

The utility includes comprehensive unit tests covering:

- ✅ Regular cycles (21, 28, 30, 35 days)
- ✅ Short cycles (15, 18 days)  
- ✅ Long cycles (40, 45 days)
- ✅ Irregular cycle patterns
- ✅ Month and year boundary crossing
- ✅ Leap year calculations
- ✅ Input validation and error cases
- ✅ Utility function accuracy

Run tests with:
```bash
./gradlew test
```

## Performance

All calculations use java.time.LocalDate which is:
- ✅ **Thread-safe and immutable**
- ✅ **Efficient for date arithmetic**  
- ✅ **Handles timezones and DST correctly**
- ✅ **Precise for medical calculations**

The utility methods are optimized for:
- Single predictions: ~O(1) time complexity
- Multiple predictions: ~O(n) where n = number of cycles
- Historical analysis: ~O(m) where m = number of entries