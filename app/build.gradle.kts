import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.autonomousapps.dependency-analysis")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.protobuf")
    id("com.github.triplet.play") version "3.12.1"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.android.compose.screenshot")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        // NOTE: This must be the same in the phone app and the wear app for the capabilities API
        applicationId = "ru.beryukhov.coffeegram"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = (100000000 + Instant.now().toEpochMilli() / 1000).toInt()
        versionName = "1.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("download.jks")
            storePassword = KeyHelper.getValue(KeyHelper.KEY_STORE_PASS)
            keyAlias = KeyHelper.getValue(KeyHelper.KEY_ALIAS)
            keyPassword = KeyHelper.getValue(KeyHelper.KEY_PASS)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
        if (project.findProperty("myapp.enableComposeCompilerReports") == "true") {
            freeCompilerArgs += listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                    project.buildDir.absolutePath + "/compose_metrics"
            )
            freeCompilerArgs += listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                    project.buildDir.absolutePath + "/compose_metrics"
            )
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }
    namespace = "ru.beryukhov.coffeegram"

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {

    implementation(projects.cmpCommon)
    implementation(projects.repository)
    implementation(projects.repositoryRoom)
    implementation(projects.dateTimeUtils)

    implementation(libs.core.coreKtx)
    implementation(libs.material)

    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    debugImplementation(libs.compose.uiTooling)
    screenshotTestImplementation(libs.compose.uiTooling)

    implementation(libs.compose.preview)
    implementation(libs.compose.activity)
    implementation(libs.glance.appwidget)
    implementation(libs.glance.preview)
    implementation(libs.glance.appwidget.preview)

    implementation(libs.google.maps.compose)
    implementation(libs.google.maps.utils)

    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.logging)

    implementation(libs.kotlinx.immutableCollections)

    implementation(libs.coroutines.core)
    runtimeOnly(libs.coroutines.android)

    implementation(libs.datastore.preferences)

    implementation(libs.datastore.datastore)
    implementation(libs.protobuf.javalite)

    implementation(libs.koin.android)
    implementation(libs.koin.android.compose) // lifecycleScope

    implementation(libs.vico.multiplatform)
    implementation(libs.vico.multiplatform.m3)

    // Wearable
    implementation(libs.playServices.wearable)
    // for connectedNodes.await()
    implementation(libs.coroutines.play)

    implementation(libs.lottie.compose)

    testImplementation(libs.robolectric)
    testImplementation(libs.compose.uiTestJunit4)
    testImplementation(libs.kakao.compose)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.annotations)
    testRuntimeOnly(libs.kotlin.test.junit)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
            }
        }
    }
}

object KeyHelper {

    const val KEY_STORE_FILE = "storeFile"
    const val KEY_STORE_PASS = "storePassword"
    const val KEY_ALIAS = "keyAlias"
    const val KEY_PASS = "keyPassword"

    private val properties by lazy {
        try {
            Properties().apply { load(FileInputStream(File("key.properties"))) }
        } catch (e: Exception) {
            Properties().apply {
                setProperty("storePassword", "")
                setProperty("keyAlias", "")
                setProperty("keyPassword", "")
            }
        }
    }

    fun getValue(key: String): String {
        return properties.getProperty(key)
    }
}

play {
    serviceAccountCredentials.set(file("../play_config.json"))
    track.set("alpha")
    defaultToAppBundles.set(true)
}
secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}
