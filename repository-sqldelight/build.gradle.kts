plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.autonomousapps.dependency-analysis")
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create("CoffeeDb") {
            packageName.set("ru.beryukhov.repository")
//            generateAsync.set(true)
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

    sourceSets {
        commonMain.dependencies {
            implementation(projects.repository)

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
    }
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    namespace = "ru.beryukhov.sqldelight"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
