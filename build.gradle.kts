plugins {
    id("convention.detekt")
    id("com.autonomousapps.dependency-analysis") version "3.20.0"
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/cmp/dev")
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
        classpath(libs.hotReloadGradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/cmp/dev")
    }
}
