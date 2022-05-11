plugins {
    id("convention.detekt")
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.androidGradle)
        classpath(libs.kotlinGradle)
        classpath(libs.realmGradle)
    }
}


allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

//task<Delete>("clean") {
//    delete(rootProject.buildDir)
//}