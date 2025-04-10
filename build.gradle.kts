plugins {
    id("convention.detekt")
    id("com.autonomousapps.dependency-analysis") version "2.15.0"
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        classpath(libs.androidGradle)
        classpath(libs.kotlinGradle)
        classpath(libs.roomGradle)
        classpath(libs.kspGradle)

        classpath(libs.composeGradle)
        classpath(libs.serializationGradle)
        classpath(libs.sqldelightGradle)
        classpath(libs.composeKotlinGradle)
        classpath(libs.protobufGradle)
        classpath(libs.secretsGradle)
        classpath(libs.screenshotGradle)
        classpath(libs.hotReloadGradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
