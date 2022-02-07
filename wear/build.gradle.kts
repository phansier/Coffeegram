plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 31

    defaultConfig {
        // NOTE: This must be the same in the phone app and the wear app for the capabilities API
        applicationId = "ru.beryukhov.coffeegram"
        minSdk = 30
        targetSdk = 31
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

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
}

dependencies {
    implementation(projects.appWearCommon)
    implementation(libs.androidx.wear)
    //implementation(libs.composeWear.material)
    implementation(libs.compose.material)
    //implementation(libs.composeWear.foundation)
    //implementation(libs.compose.foundation)

    implementation(libs.compose.activity)
    implementation(libs.compose.uiTooling)
    val playServicesWearableVersion = "17.1.0"
    implementation ("com.google.android.gms:play-services-wearable:$playServicesWearableVersion")
}