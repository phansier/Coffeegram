plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        // NOTE: This must be the same in the phone app and the wear app for the capabilities API
        applicationId = "ru.beryukhov.coffeegram"
        minSdk = libs.versions.wearMinSdk.get().toInt()
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
    composeOptions {
//        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "ru.beryukhov.coffeegram.wear"
}

composeCompiler {
    // TODO: Remove workaround for https://youtrack.jetbrains.com/issue/KT-67216
    suppressKotlinVersionCompatibilityCheck = libs.versions.kotlin.get()
}

dependencies {
    implementation(projects.appWearCommon)
    implementation(libs.androidx.wear)
    implementation(libs.composeWear.material)
    implementation(libs.composeWear.foundation)

    implementation(libs.compose.activity)
    implementation(libs.compose.uiTooling)
    implementation(libs.playServices.wearable)
}
