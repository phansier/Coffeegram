plugins {
    kotlin("multiplatform")
//    kotlin("native.cocoapods")
    id("com.android.library")
    id("io.realm.kotlin")
}

version = "1.0"

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    /*cocoapods {
        summary = "Repository for Coffegram"
        homepage = "https://github.com/phansier/Coffeegram"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "repository"
        }
        // set path to your ios project podfile, e.g. podfile = project.file("../iosApp/Podfile")
    }*/

    sourceSets {
        commonMain.dependencies {

            implementation(libs.realmKotlin)
            implementation(libs.coroutines.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.junit)
            }
        }
        val commonTest by getting {}
        androidUnitTest.dependsOn(commonTest)

        /*androidTest.dependencies {
            implementation(kotlin("test-junit"))
            implementation(libs.junit)
        }*/
    }
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    namespace = "repository"
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
