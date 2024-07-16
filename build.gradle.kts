plugins {
    id("convention.detekt")
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
        classpath(libs.realmGradle)
        classpath(libs.roomGradle)
        classpath(libs.ksp)

        classpath(libs.composeGradle)
        classpath(libs.sqldelightGradle)
        classpath(libs.composeKotlinGradle)
        classpath(libs.protobufGradle)
        classpath(libs.secretsGradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
