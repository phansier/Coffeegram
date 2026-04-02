import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
//    id("com.autonomousapps.dependency-analysis")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.compose.hot-reload")
    `maven-publish`
}

kotlin {
    android {
        namespace = "ru.beryukhov.compose_common"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        androidResources.enable = true
        withHostTestBuilder {}
    }

    jvm()

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "cmp_common"
            isStatic = true
        }
    }

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static(rootDirPath)
                    static(projectDirPath)
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
            implementation(projects.repository)
            implementation(projects.dateTimeUtils)
            // implementation(projects.repositorySqldelight) // fs and path issue in sql.js

            implementation(libs.cmp.ui) // for explicit up to date version
            implementation(libs.cmp.runtime)
            implementation(libs.cmp.foundation)
            implementation(libs.cmp.material3) // for explicit up to date version
            implementation(libs.material.icons.core)

            implementation(libs.coroutines.core)

            implementation(libs.cmp.ui.tooling.preview)
            implementation(libs.cmp.components.resources)

            implementation(libs.kotlinx.immutableCollections)

            implementation(libs.cupertino.adaptive)
            implementation(libs.cupertino.iconsExtended)

            api(libs.decompose.core)
            api(libs.essenty)
            implementation(libs.decompose.compose)

            api(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.coil.compose)

            implementation(libs.vico.multiplatform)
            implementation(libs.vico.multiplatform.m3)
        }
        val notWasm by getting {
            dependencies {
                implementation(projects.repositorySqldelight)

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
            implementation(libs.cmp.ui.tooling)
            implementation(libs.ktor.android)
            // Wearable
            implementation(libs.playServices.wearable)
        }
        getByName("androidHostTest").dependencies {
            implementation(libs.kotlin.test.junit)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.java)
            implementation(libs.coroutines.swing)
        }
        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }
        jsMain.dependencies {
            implementation(project.dependencies.enforcedPlatform(libs.jetbrains.kotlinWrappers.kotlinWrappersBom.get()))
            implementation(libs.kotlinBrowser)
        }
    }
}

tasks.named("syncComposeResourcesForIos") {
    onlyIf { System.getenv("ARCHS") != null && System.getenv("TARGET_BUILD_DIR") != null }
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
