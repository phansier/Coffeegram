plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
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
        isCoreLibraryDesugaringEnabled = true

        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
    namespace = "ru.beryukhov.coffeegram"
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs_minimal:2.0.4")

    implementation(projects.cmpCommon)

    implementation(libs.core.coreKtx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.jetbrains.compose.ui)
    implementation(libs.jetbrains.compose.material)

    implementation(libs.compose.activity)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.koin.android)
}
