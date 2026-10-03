# Project specific ProGuard rules for Justra (Pocket Lawyer)

-keep class com.google.ai.client.generativeai.** { *; }
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @com.squareup.moshi.* <fields>;
    @com.squareup.moshi.* <methods>;
}
-keep class androidx.security.crypto.** { *; }
-keep class androidx.biometric.** { *; }

# Keep Moshi JSON DTOs and reflection models
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keep class com.example.data.** { *; }
-keep class com.example.domain.model.** { *; }

# Keep Retrofit Service interfaces and annotations
-keepclassmembers com.example.data.api.** { *; }
-keep interface com.example.data.api.** { *; }
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep Room DAOs and Entities
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Preserve line numbers for stack traces
-keepattributes SourceFile,LineNumberTable
