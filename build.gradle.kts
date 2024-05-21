plugins {
    id("convention.detekt")
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://oss.sonatype.org/content/repositories/snapshots") // realm snapshot
    }
    dependencies {
        classpath(libs.androidGradle)
        classpath(libs.kotlinGradle)
        classpath(libs.realmGradle)

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
        maven("https://oss.sonatype.org/content/repositories/snapshots") // realm snapshot
//        maven("https://androidx.dev/storage/compose-compiler/repository/") // only for prerelease versions of compose compiler
    }
}
