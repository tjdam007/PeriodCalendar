# Period Calendar ProGuard Rules
# Optimized for Jetpack Compose, Room, Hilt, WorkManager, and java.time

# Enable debugging in release builds
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# General Android optimizations
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ========== Jetpack Compose Rules ==========
# Keep all Compose compiler classes
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.animation.** { *; }

# Keep composable functions (annotated with @Composable)
-keep @androidx.compose.runtime.Composable class **
-keep class **$$serializer { *; }
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable *;
}

# Keep Navigation Compose
-keep class androidx.navigation.compose.** { *; }

# ========== Hilt Dependency Injection ==========
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent
-keep class **_HiltModules { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# Keep Hilt generated classes
-keep class **_Impl { *; }
-keep class **Hilt** { *; }

# Keep classes annotated with Hilt annotations
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @dagger.Module class *
-keep @dagger.hilt.InstallIn class *

# ========== Room Database ==========
# Keep all Room entities and DAOs
-keep class com.mjandroiddev.periodcalendar.data.database.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep Room generated classes
-keep class **_Impl { *; }
-keep class **$Companion { *; }

# Keep Room TypeConverters
-keep class * {
    @androidx.room.TypeConverter <methods>;
}

# ========== WorkManager ==========
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker { *; }

# Keep our custom Workers
-keep class com.mjandroiddev.periodcalendar.notifications.** { *; }

# ========== Java Time API ==========
-keep class java.time.** { *; }
-keep class java.time.format.** { *; }
-dontwarn java.time.**

# ========== Data Classes and Models ==========
# Keep all data models and enums
-keep class com.mjandroiddev.periodcalendar.data.model.** { *; }
-keepclassmembers enum * { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ========== Lifecycle Components ==========
-keep class androidx.lifecycle.** { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }

# ========== Material Design ==========
-keep class com.google.android.material.** { *; }

# ========== Reflection and Annotations ==========
# Keep annotation classes
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations

# Keep generic signatures for better debugging
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ========== Kotlin Specific ==========
# Keep Kotlin metadata
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

# Keep companion objects
-keepclassmembers class ** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    ** Companion;
}

# Keep kotlin.Metadata annotations
-keep @kotlin.Metadata class *

# ========== App Specific Rules ==========
# Keep main application class
-keep class com.mjandroiddev.periodcalendar.MainActivity { *; }
-keep class com.mjandroiddev.periodcalendar.PeriodCalendarApplication { *; }

# Keep all ViewModels
-keep class com.mjandroiddev.periodcalendar.ui.viewmodel.** { *; }

# Keep utility classes
-keep class com.mjandroiddev.periodcalendar.utils.** { *; }

# Keep broadcast receivers
-keep class * extends android.content.BroadcastReceiver { *; }

# ========== Firebase Rules ==========
# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.android.gms.measurement.** { *; }

# Firebase Crashlytics
-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.crashlytics.** { *; }
-keep class com.google.firebase.crash.** { *; }
-keepattributes SourceFile,LineNumberTable,*Annotation*

# Firebase Cloud Messaging
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.iid.** { *; }

# Keep Firebase configuration
-keep class com.google.firebase.FirebaseOptions { *; }
-keep class com.google.firebase.FirebaseApp { *; }

# Keep our Firebase classes
-keep class com.mjandroiddev.periodcalendar.firebase.** { *; }

# Keep classes with Firebase annotations
-keep @com.google.firebase.annotations.PublicApi class *
-keepclassmembers class * {
    @com.google.firebase.annotations.PublicApi *;
}

# ========== Optimization Settings ==========
# Enable aggressive optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification