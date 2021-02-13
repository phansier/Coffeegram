// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    //val compose_version by extra("1.0.0-alpha09")
    val jetbrains_compose_version by extra("0.3.0-build152")
    val kotlin_version by extra("1.4.30")
    val coroutines_version by extra("1.4.0")
    val appcompat_version by extra("1.2.0")
    val core_ktx_version by extra("1.3.2")

    repositories {
        // TODO: remove after new build is published
        mavenLocal().mavenContent {
            includeModule("org.jetbrains.compose", "compose-gradle-plugin")
        }
        google()
        jcenter()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        //classpath("com.android.tools.build:gradle:7.0.0-alpha03")//
        classpath("com.android.tools.build:gradle:4.0.1")
        // __LATEST_COMPOSE_RELEASE_VERSION__
        classpath("org.jetbrains.compose:compose-gradle-plugin:$jetbrains_compose_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}