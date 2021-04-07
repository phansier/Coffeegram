// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val jetbrains_compose_version by extra("0.4.0-build179")
    val kotlin_version by extra("1.4.32")
    val coroutines_version by extra("1.4.0")
    val appcompat_version by extra("1.3.0-beta01")
    val core_ktx_version by extra("1.3.2")

    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath("org.jetbrains.compose:compose-gradle-plugin:$jetbrains_compose_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlinx/")//for org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.3
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}