plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

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
