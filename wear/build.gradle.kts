plugins {
    id("com.android.application")
    id("com.autonomousapps.dependency-analysis")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        // NOTE: This must be the same in the phone app and the wear app for the capabilities API
        applicationId = "ru.beryukhov.coffeegram"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    namespace = "ru.beryukhov.coffeegram.wear"
}

dependencies {
    implementation(projects.cmpCommon)
    implementation(libs.composeWear.material)
    implementation(libs.composeWear.foundation)

    implementation(libs.compose.activity)
    implementation(libs.compose.uiTooling)
    implementation(libs.playServices.wearable)
}
