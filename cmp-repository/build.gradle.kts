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
    android()

    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    @Suppress("UnusedPrivateMember")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)

                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutinesExt)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.androidDriver)
                implementation(libs.sqldelight.coroutinesExt)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.sqldelight.nativeDriver)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.sqldelight.sqliteDriver)
            }
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
