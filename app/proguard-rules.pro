# ProGuard rules for Minimal OS Launcher
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Compose
-dontwarn androidx.compose.**

# Keep our models
-keep class com.zaryif.minimalos.domain.model.** { *; }
