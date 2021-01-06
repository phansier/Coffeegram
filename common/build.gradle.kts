import org.jetbrains.compose.compose

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

val compose = object {
    val foundation get() = composeDependency("org.jetbrains.compose.foundation:foundation")
    val material get() = composeDependency("org.jetbrains.compose.material:material")
    val runtime get() = composeDependency("org.jetbrains.compose.runtime:runtime")

    val desktop = object {
        val common = composeDependency("org.jetbrains.compose.desktop:desktop")
    }
}
fun composeDependency(groupWithArtifact: String) = "$groupWithArtifact:${rootProject.extra["jetbrains_compose_version"]}"

kotlin {
    android()
    jvm("desktop")
    sourceSets {
        named("commonMain") {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                //implementation("io.ktor:ktor-client-core:1.4.1")
            }
        }
        named("androidMain") {
            dependencies {
                api("androidx.appcompat:appcompat:${rootProject.extra["appcompat_version"]}")
                api("androidx.core:core-ktx:${rootProject.extra["core_ktx_version"]}")
                implementation("com.jakewharton.threetenabp:threetenabp:1.2.4")
                //implementation("io.ktor:ktor-client-cio:1.4.1")
            }
        }
        named("desktopMain") {
            dependencies {
                api(compose.desktop.common)
                //implementation("io.ktor:ktor-client-cio:1.4.1")
            }
        }
    }
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8//todo check 11
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res")
        }
    }
}
