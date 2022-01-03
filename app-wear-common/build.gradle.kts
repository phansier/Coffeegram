plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 31

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}