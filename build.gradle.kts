// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val compose_version by extra("1.1.0-alpha02")
    val kotlin_version by extra("1.5.21")
    val coroutines_version by extra("1.5.1-native-mt")
    val realm_version by extra("0.5.0")
    val datastore_version by extra("1.0.0")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha10")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("io.realm.kotlin:gradle-plugin:$realm_version")
    }
}


allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}