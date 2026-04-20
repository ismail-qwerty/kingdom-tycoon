# LibGDX specific rules
-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication
-dontwarn com.badlogic.gdx.utils.GdxBuild
-dontwarn com.badlogic.gdx.physics.box2d.utils.Box2DBuild
-dontwarn com.badlogic.gdx.jnigen.BuildTarget*

# Keep all LibGDX classes
-keep class com.badlogic.gdx.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep serialization classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readReplace();
}

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all @Serializable classes
-keep,includedescriptorclasses class com.ismail.kingdom.**$$serializer { *; }
-keepclassmembers class com.ismail.kingdom.** {
    *** Companion;
}
-keepclasseswithmembers class com.ismail.kingdom.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# FIX: Keep android launcher and all android-package classes
-keep class com.ismail.kingdom.android.** { *; }

# FIX: Keep all screen and UI classes (R8 strips these in release without this)
-keep class com.ismail.kingdom.screens.** { *; }
-keep class com.ismail.kingdom.ui.** { *; }
-keep class com.ismail.kingdom.ScreenNavigator { *; }
-keep class com.ismail.kingdom.KingdomTycoonGame { *; }
-keep class com.ismail.kingdom.LoadingScreen { *; }
-keep class com.ismail.kingdom.MainMenuScreen { *; }
-keep class com.ismail.kingdom.GameScreen { *; }
-keep class com.ismail.kingdom.MapScreen { *; }
-keep class com.ismail.kingdom.EventScreen { *; }
-keep class com.ismail.kingdom.PrestigeScreen { *; }
-keep class com.ismail.kingdom.SettingsScreen { *; }
-keep class com.ismail.kingdom.HallOfLegendsScreen { *; }
-keep class com.ismail.kingdom.WarScreen { *; }

# Keep game state classes
-keep class com.ismail.kingdom.models.** { *; }
-keep class com.ismail.kingdom.data.** { *; }

# FIX: Keep all systems and factories
-keep class com.ismail.kingdom.systems.** { *; }
-keep class com.ismail.kingdom.factories.** { *; }

# AdMob
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.common.** { *; }
-dontwarn com.google.android.gms.**

# User Messaging Platform (GDPR)
-keep class com.google.android.ump.** { *; }
-dontwarn com.google.android.ump.**

# WorkManager
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# Play Core
-keep class com.google.android.play.core.** { *; }
-dontwarn com.google.android.play.core.**

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile