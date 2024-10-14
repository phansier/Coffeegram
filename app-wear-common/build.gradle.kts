plugins {
    id("com.android.library")
    id("com.autonomousapps.dependency-analysis")
    kotlin("android")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    namespace = "ru.beryukhov.coffeegram.common"
}

dependencies {

    implementation(libs.androidx.annotation)
    // Wearable
    implementation(libs.playServices.wearable)
}
