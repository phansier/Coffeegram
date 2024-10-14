plugins {
    id("com.android.application")
    id("com.autonomousapps.dependency-analysis")
    kotlin("android")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ru.beryukhov.coffeegram.desktop"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile("proguard-rules.pro")
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
    namespace = "ru.beryukhov.coffeegram"
}

dependencies {

    implementation(projects.cmpCommon)

    implementation(compose.runtime)
    implementation(compose.foundation)

    implementation(libs.material)

    implementation(libs.jetbrains.compose.ui)

    implementation(libs.compose.activity)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.koin.android)
}
