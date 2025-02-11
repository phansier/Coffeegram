plugins {
    id("convention.detekt")
    id("com.autonomousapps.dependency-analysis") version "2.8.2"
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
        classpath(libs.ksp)

        classpath(libs.composeGradle)
        classpath(libs.sqldelightGradle)
        classpath(libs.composeKotlinGradle)
        classpath(libs.protobufGradle)
        classpath(libs.secretsGradle)
        classpath(libs.screenshot)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
