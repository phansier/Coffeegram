import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("com.android.library")
//    id("com.autonomousapps.dependency-analysis")
    kotlin("native.cocoapods")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")

    `maven-publish`
}

kotlin {
    androidTarget()
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    // Apply the default hierarchy again. It'll create, for example, the iosMain source set:
    applyDefaultHierarchyTemplate()

    sourceSets {
        val notWasm by creating {
            dependsOn(commonMain.get())
        }

        iosMain.get().dependsOn(notWasm)
        jvmMain.get().dependsOn(notWasm)
        androidMain.get().dependsOn(notWasm)
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.cmpRepository)
            implementation(projects.dateTimeUtils)

            implementation(compose.runtime)
            implementation(compose.foundation)

            implementation(compose.material3) // for explicit up to date version
            implementation(libs.coroutines.core)

            implementation(compose.components.uiToolingPreview)
            implementation(compose.components.resources)

            implementation(libs.kotlinx.immutableCollections)

            implementation(libs.cupertino.adaptive)
            implementation(libs.cupertino.iconsExtended)

            api(libs.koin.core)
        }
        val notWasm by getting {
            dependencies {
                implementation(projects.cmpSqldelight)

                implementation(libs.datastore.preferencesCore)
                implementation(libs.datastore.coreOkio)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.annotations)
            implementation(libs.koin.test)
            implementation(libs.coroutines.test)
        }
        androidMain.dependencies {
            implementation(compose.uiTooling)
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.kotlin.test.junit)
            }
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }

    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "https://github.com/phansier/Coffeegram"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../cmp-iosApp/Podfile")
        framework {
            baseName = "cmp_common"
            isStatic = true
        }
    }
}

tasks.withType<KotlinJsCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf("-Xwasm-use-traps-instead-of-exceptions")
}

android {
    namespace = "ru.beryukhov.compose_common"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

compose.desktop {
    application {
        mainClass = "Main_desktopKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Coffeegram"
            packageVersion = "1.0.0"
            modules("jdk.crypto.ec")

            val iconsRoot = project.file("../cmp-common/src/jvmMain/resources/images")
            macOS {
                iconFile.set(iconsRoot.resolve("icon-mac.icns"))
                packageName = "Coffeegram"
                dockName = "Coffeegram"
            }
            windows {
                iconFile.set(iconsRoot.resolve("icon-windows.ico"))
                menuGroup = "Compose Examples"
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "18159995-d967-4CD2-8885-77BFA97CFA9F"
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon-linux.png"))
            }
        }
    }
}
