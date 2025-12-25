# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep UI navigation destinations
-keep class com.kito.ui.navigation.Destinations { *; }
-keep class com.kito.ui.navigation.Destinations$* { *; }

# Keep main application class
-keep class com.kito.KitoApplication { *; }

# Keep all activities and essential UI classes
-keep class com.kito.MainActivity { *; }
-keep class com.kito.*Activity { *; }
-keep class com.kito.*ViewModel { *; }

# Keep Hilt/Dagger classes (for dependency injection)
-keep class * implements dagger.hilt.GeneratedComponent
-keep @dagger.hilt.InstallIn class *
-keep @dagger.Binds class *
-keep @dagger.Provides class *

# Keep Supabase API interface and data models for timetable functionality
-keep interface com.kito.data.remote.SupabaseApi { *; }

# Keep data classes used for API serialization/deserialization
-keep class com.kito.data.local.db.section.SectionEntity { *; }
-keep class com.kito.data.local.db.student.StudentEntity { *; }

# Keep all fields in data classes for proper serialization/deserialization
-keepclassmembers class * {
    <fields>;
}

# Keep all getter and setter methods for serialization
-keepclassmembers class * {
    public void set*(***);
    public *** get*();
    public *** is*();
}

# Keep Retrofit and related networking classes
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep Room database entities and DAOs
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}

# Keep JSoup classes for SAP portal (critical for functionality)
-keep class org.jsoup.** { *; }

# Keep serialization annotations
-keepattributes Signature
-keepattributes *Annotation*

# Don't warn about common missing dependencies
-dontwarn javax.lang.model.element.**
-dontwarn org.jsoup.**
-dontwarn okhttp3.**
-dontwarn okio.**