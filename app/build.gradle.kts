import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
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

        // Load API key from local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "NEWS_API_KEY", "\"${properties.getProperty("NEWS_API_KEY", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Added ktlint configuration for code style checking
    ktlint {
        android.set(true)
        verbose.set(true)
    }

    // Added detekt configuration for static code analysis
    detekt {
        toolVersion = libs.versions.detektGradle.get()
        buildUponDefaultConfig = true
        parallel = true
        config.setFrom(files("$rootDir/detekt.yml"))
    }
}

dependencies {
    // Core library desugaring for java.time APIs
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Existing (keep these)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.retrofit)

    // MVVM + Compose Extra
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

    // **Jetpack Compose**
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // **Navigation Compose**
    implementation(libs.androidx.navigation.compose)

    // **Image Loading (Coil)**
    implementation(libs.coil.compose)

    // **Pull-to-Refresh**
    implementation(libs.google.accompanist.swiperefresh)

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
    kaptTest(libs.hilt.compiler) // kapt for tests

    // Android Test Hilt dependencies
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler) // kapt for android tests
    androidTestImplementation(libs.okhttp.mockwebserver)
}

// Added by user to enable HTML reports for Detekt static code analysis
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
    }
}
