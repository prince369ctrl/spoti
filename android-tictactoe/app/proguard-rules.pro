-keep class com.advancedtictactoe.game.** { *; }
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Kotlin serialization
-keepattributes *Annotation*
-keepclassmembers class ** { @com.google.gson.annotations.SerializedName <fields>; }

# Room
-keep class * extends androidx.room.RoomDatabase { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class * { @androidx.compose.runtime.Composable *; }

# Lottie
-keep class com.airbnb.lottie.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
