import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create("CoffeeDb") {
            packageName.set("ru.beryukhov.repository")
            generateAsync.set(true)
        }
    }
    // linkSqlite = true
}

version = "1.0"

kotlin {
    androidTarget()

    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {}
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutinesExt)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.androidDriver)
            implementation(libs.sqldelight.coroutinesExt)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.nativeDriver)
        }

        jvmMain.dependencies {
            implementation(libs.sqldelight.sqliteDriver)
        }

        wasmJsMain.dependencies {
            implementation(libs.sqldelight.webWorkerDriver)
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
        }
    }
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    namespace = "ru.beryukhov.repository"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
