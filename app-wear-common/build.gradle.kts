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
    namespace = "ru.beryukhov.coffeegram.common"
}

dependencies {

    implementation("androidx.annotation:annotation:1.3.0")
    val playServicesWearableVersion = "17.1.0"
    // Wearable
    implementation("com.google.android.gms:play-services-wearable:$playServicesWearableVersion")
}
