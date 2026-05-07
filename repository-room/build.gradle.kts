import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("com.autonomousapps.dependency-analysis")
    id("androidx.room3")
    id("com.google.devtools.ksp")
}

version = "1.0"

kotlin {
    android {
        namespace = "repository"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
    }

    jvm()

    iosArm64()
    iosSimulatorArm64()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.repository)

            implementation(libs.room.runtime)
            implementation(libs.sqlite)

            implementation(libs.coroutines.core)

            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }
        androidMain.dependencies {
            implementation(libs.sqlite.bundled)
        }
        iosMain.dependencies {
            implementation(libs.sqlite.bundled)
        }
        jvmMain.dependencies {
            implementation(libs.sqlite.bundled)
        }
        webMain.dependencies {
            implementation(libs.sqlite.web)
        }

        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
        webMain.dependencies {
            implementation(
                npm("sqlite-wasm-worker", layout.projectDirectory.dir("worker").asFile)
            )
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
        )
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
    add("kspWasmJs", libs.room.compiler)
    add("kspJs", libs.room.compiler)
}

room3 {
    schemaDirectory("$projectDir/schemas")
}
