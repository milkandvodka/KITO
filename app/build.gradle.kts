plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.kito"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kito"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
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

    defaultConfig {
        applicationId = "com.kito"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add build config fields for SAP portal URLs
        buildConfigField("String", "PORTAL_BASE", "\"https://kiitportal.kiituniversity.net\"")
        buildConfigField("String", "WD_PATH", "\"/sap/bc/webdynpro/sap/ZWDA_HRIQ_ST_ATTENDANCE\"")
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

//    //HazeEffect(Frosted Glass)
//    implementation(libs.haze)
//    implementation(libs.haze.ma)
}