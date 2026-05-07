plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
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
    android {
        namespace = "ru.beryukhov.sqldelight"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
    }

    jvm()

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
