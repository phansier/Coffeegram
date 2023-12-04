plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create("CoffeeDb") {
            packageName.set("ru.beryukhov.repository")
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

    @Suppress("UnusedPrivateMember")
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutinesExt)
        }
        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
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

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    namespace = "ru.beryukhov.repository"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
