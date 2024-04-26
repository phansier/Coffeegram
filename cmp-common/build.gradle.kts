import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
//    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")

    id("co.touchlab.kmmbridge") version "0.5.5"
    `maven-publish`
}

fun composeDependency(groupWithArtifact: String) = "$groupWithArtifact:${libs.versions.jetbrainsCompose}"

kotlin {
    androidTarget()
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

//    cocoapods {
//        version = "1.0.0"
//        summary = "Some description for the Shared Module"
//        homepage = "https://github.com/phansier/Coffeegram"
//        ios.deploymentTarget = "14.1"
//        podfile = project.file("../cmp-iosApp/Podfile")
//        framework {
//            baseName = "cmp_common"
//            isStatic = true
//        }
//        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
//        extraSpecAttributes["resources"] = "['src/commonMain/resources/**']"
//    }

    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)

            implementation(libs.kotlinx.immutableCollections)

            implementation(libs.jetbrains.compose.componentsResources)

            implementation(projects.cmpRepository)
            implementation(libs.kotlinx.datetime)

            implementation(libs.cupertino.adaptive)
            implementation(libs.cupertino.iconsExtended)

            api(libs.datastore.preferencesCore)
            api(libs.datastore.coreOkio)

            api(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(libs.koin.test)
            implementation(libs.coroutines.test)
        }
        androidMain.dependencies {
            api(libs.androidx.appcompat)
            api(libs.core.coreKtx)
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

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
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

kmmbridge {
    mavenPublishArtifacts()
    spm()
    // cocoapods("git@github.com:phansier/PodSpecs.git")
    addGithubPackagesRepository()
}
