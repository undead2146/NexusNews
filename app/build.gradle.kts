plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)  // **New: For Room/Hilt/Moshi processors**
    alias(libs.plugins.hilt.android)  // **New: Hilt plugin**
}

android {
    namespace = "com.example.nexusnews"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.nexusnews"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Existing (keep these)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.retrofit)  // Already there – great for APIs!
    
    // **New: Add these for full project**
    
    // MVVM + Compose Extras
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    
    // Room DB Bundle (SQLite read/write)
    implementation(libs.bundles.room)
    kapt(libs.room.compiler)
    
    // DataStore & Encrypted Keys
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto)
    
    // Retrofit Extras (JSON + Logging; since Retrofit is already added)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.bundles.moshiBundle)
    kapt(libs.moshi.kotlin.codegen)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    
    // Hilt DI
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)
    
    // Scraping (HBVL/GvA)
    implementation(libs.jsoup)
    
    // Images (Coil for article images)
    implementation(libs.coil.compose)
    
    // Background Tasks (WorkManager for sync)
    implementation(libs.work.runtime.ktx)
    
    // Logging (Timber)
    implementation(libs.timber)
    
    // **Existing Testing (keep)**
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    // **New Testing Extras**
    testImplementation(libs.bundles.testingUnit)
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.compiler)  // kapt for tests
}
