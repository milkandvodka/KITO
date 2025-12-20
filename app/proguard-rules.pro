# ######## Enhanced ProGuard rules for the Kito app ########

# Add the missing rule from R8
-dontwarn javax.lang.model.element.Modifier

# Keep all activities, services, receivers, and providers
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep custom views
-keep public class * extends android.view.View
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep class androidx.compose.ui.node.LayoutNode
-keep class androidx.compose.ui.Modifier
-keep class androidx.compose.runtime.Composable

# Keep classes in the app package with aggressive obfuscation
-keep class com.kito.** { *; }
-keep class com.kito.sap.** { *; }

# Keep the main application classes
-keep class com.kito.MainActivity
-keep class com.kito.AttendanceApp
-keep class com.kito.AttendanceViewModel
-keep class com.kito.PreferencesKt
-keep class com.kito.ui.legacy.viewmodel.AttendanceViewModelFactory

# Keep the SAP portal client with all methods and fields
-keep class com.kito.sap.legacy.SapPortalClient { *; }
-keep class com.kito.sap.legacy.AttendanceResult { *; }
-keep class com.kito.sap.legacy.AttendanceData { *; }
-keep class com.kito.sap.legacy.SubjectAttendance { *; }
-keep class com.kito.sap.legacy.PersistentCookieJar { *; }

# Keep necessary classes for HTTP requests (OkHttp)
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep necessary classes for HTML parsing (Jsoup)
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# Keep DataStore classes
-keep class androidx.datastore.** { *; }

# Keep Compose classes
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material.** { *; }
-keep class androidx.compose.material3.** { *; }

# Keep annotations
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }

# Keep parameter names and other important attributes
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes SourceFile,LineNumberTable

# Keep the BuildConfig class
-keep class com.kito.BuildConfig { *; }

# Keep resource references
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep methods that might be used by reflection
-keepclassmembers class * {
    public void **(**);
}

# Optimize settings
-optimizations !code/simplification/arithmetic,!code/simplification/advanced,!class/merging/*
-optimizationpasses 5

# Don't skip non-public library classes
-dontskipnonpubliclibraryclasses
-dontpreverify

# Verbose output for debugging
-verbose