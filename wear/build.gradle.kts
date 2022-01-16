plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "ru.beryukhov.coffeegram.wear"
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
        kotlinCompilerExtensionVersion = "1.1.0-rc02"//libs.versions.compose.get()
    }
}

dependencies {
    implementation(projects.appWearCommon)
    implementation(libs.androidx.wear)
    implementation(libs.composeWear.material)
    implementation(libs.composeWear.foundation)

    implementation(libs.compose.activity)
    implementation(libs.compose.uiTooling)
    val playServicesWearableVersion = "17.1.0"
    implementation ("com.google.android.gms:play-services-wearable:$playServicesWearableVersion")
}