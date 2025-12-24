import java.io.FileInputStream
import java.util.Properties
import kotlin.apply

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.kotlin.serialization)
}

val localProps = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        load(FileInputStream(localPropsFile))
    }
}

android {
    namespace = "com.kito"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kito"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "PORTAL_BASE",
            "\"https://kiitportal.kiituniversity.net\"",

        )
        buildConfigField(
            "String",
            "WD_PATH",
            "\"/sap/bc/webdynpro/sap/ZWDA_HRIQ_ST_ATTENDANCE\""
        )
        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${localProps.getProperty("SUPABASE_URL")}\""
        )

        buildConfigField(
            "String",
            "SUPABASE_ANON_KEY",
            "\"${localProps.getProperty("SUPABASE_ANON_KEY")}\""
        )
    }

    buildTypes {
        debug {
            resValue("string", "app_name", "Kito (Debug)")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            resValue("string", "app_name", "Kito")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Dependencies for SAP portal integration
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jsoup:jsoup:1.17.2")

    // Compose dependencies for modern UI
    implementation(platform("androidx.compose:compose-bom-alpha:2025.12.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")

    // if using Compose BOM, keep versions aligned
    implementation("androidx.compose.material:material-icons-extended")

    // DataStore for persistent preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation(libs.androidx.navigation.compose)

    //Dagger - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Lottie Animation
    implementation("com.airbnb.android:lottie-compose:6.6.7")

    //HazeEffect(Frosted Glass)
    implementation(libs.haze)
    implementation(libs.haze.materials)

    //Glance
    implementation("androidx.glance:glance-appwidget:1.1.1")
    // For interop APIs with Material 3
    implementation("androidx.glance:glance-material3:1.1.1")
    // For interop APIs with Material 2
    implementation("androidx.glance:glance-material:1.1.1")

    //composeNavigation
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    //splashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Room (Database)
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-paging:2.6.1")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}