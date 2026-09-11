import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.autonomousapps.dependency-analysis")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.github.triplet.play") version "4.1.1"
}

val resolvedVersionName: String = run {
    val fallback = "1.9.1"
    val execResult = providers.exec {
        commandLine("git", "describe", "--tags", "--abbrev=0", "--match", "v*-android")
        isIgnoreExitValue = true
    }
    if (execResult.result.get().exitValue != 0) {
        fallback
    } else {
        execResult.standardOutput.asText.get().trim()
            .removePrefix("v").removeSuffix("-android")
            .takeIf { it.matches(Regex("""\d+\.\d+\.\d+""")) } ?: fallback
    }
}

val resolvedVersionCode: Int = run {
    val anchorEpochSeconds = 1_789_000_000L
    val wearRangeStart = 2_000_000_000L
    val minutesSinceAnchor = (Instant.now().epochSecond - anchorEpochSeconds) / 60
    (wearRangeStart + minutesSinceAnchor).toInt()
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        // NOTE: This must be the same in the phone app and the wear app for the capabilities API
        applicationId = "ru.beryukhov.coffeegram"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        // Wear shares the phone's applicationId, so Play requires a non-colliding versionCode range.
        versionCode = resolvedVersionCode
        versionName = resolvedVersionName
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
        targetCompatibility = JavaVersion.VERSION_21
        sourceCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }

    namespace = "ru.beryukhov.coffeegram.wear"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(projects.cmpCommon)
    implementation(libs.composeWear.material)
    implementation(libs.composeWear.foundation)

    implementation(libs.compose.activity)
    implementation(libs.compose.uiTooling)
    implementation(libs.playServices.wearable)
    implementation(libs.coroutines.play)
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
    track.set("wear:alpha")
    defaultToAppBundles.set(true)
}
