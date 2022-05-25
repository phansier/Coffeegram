plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
