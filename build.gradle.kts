// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val compose_version by extra("1.0.0-rc01")
    val kotlin_version by extra("1.5.10")
    val coroutines_version by extra("1.5.0")
    val sqldelight_version by extra("1.5.0")
    //val ktor_version by extra("")


    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        //for sqldelight
        maven(url= "https://www.jetbrains.com/intellij-repository/releases" )
        maven(url= "https://cache-redirector.jetbrains.com/intellij-dependencies" )
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.squareup.sqldelight:gradle-plugin:$sqldelight_version")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven ( url= "https://dl.bintray.com/kotlin/kotlinx")

    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}