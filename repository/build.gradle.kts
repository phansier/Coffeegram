import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

version = "1.0"

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iosTarget("ios") {}

    cocoapods {
        summary = "Repository for Coffegram"
        homepage = "https://github.com/phansier/Coffeegram"
        ios.deploymentTarget = "14.1"
        frameworkName = "repository"
        // set path to your ios project podfile, e.g. podfile = project.file("../iosApp/Podfile")
    }

    //val coroutines_version = "1.5.0"
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines_version"]}")

//                implementation("io.ktor:ktor-client-core:${rootProject.extra["$ktor_version"]}")
//                implementation("io.ktor:ktor-client-json:${rootProject.extra["$ktor_version"]}")
//                implementation("io.ktor:ktor-client-websockets:${rootProject.extra["$ktor_version"]}")

                implementation("com.squareup.sqldelight:runtime:${rootProject.extra["sqldelight_version"]}")
                implementation("com.squareup.sqldelight:coroutines-extensions:${rootProject.extra["sqldelight_version"]}")
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
                implementation("com.squareup.sqldelight:android-driver:${rootProject.extra["sqldelight_version"]}")
                implementation("com.squareup.sqldelight:coroutines-extensions:${rootProject.extra["sqldelight_version"]}")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:${rootProject.extra["sqldelight_version"]}")
            }
        }
        val iosTest by getting
        /*sourceSets.jvmMain.dependencies {
            implementation "com.squareup.sqldelight:sqlite-driver:1.5.0"
        }*/
    }
}

android {
    compileSdk = 30
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 30
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    database("AppDatabase") {
        packageName = "ru.beryukhov.repository"
    }
}