import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.autonomousapps.dependency-analysis")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.github.triplet.play") version "4.1.1"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.android.compose.screenshot")
    id("com.google.protobuf")
}

val mapsApiKey = project.findProperty("MAPS_API_KEY") as String?

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
    val phoneRangeStart = 1_900_000_000L
    val minutesSinceAnchor = (Instant.now().epochSecond - anchorEpochSeconds) / 60
    (phoneRangeStart + minutesSinceAnchor).toInt()
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        // NOTE: This must be the same in the phone app and the wear app for the capabilities API
        applicationId = "ru.beryukhov.coffeegram"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = resolvedVersionCode
        versionName = resolvedVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey ?: ""
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
        buildConfig = true
    }

    packaging {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }
    namespace = "ru.beryukhov.coffeegram"

    configurations.configureEach {
        exclude(group = "androidx.appcompat", module = "appcompat")
        exclude(group = "androidx.appcompat", module = "appcompat-resources")
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.RequiresOptIn")
        freeCompilerArgs.addAll(
            "-Xexplicit-backing-fields",
        )
    }
    jvmToolchain(21)
}

dependencies {

    implementation(projects.cmpCommon)
    implementation(projects.repository)
    implementation(projects.repositoryRoom)
    implementation(projects.dateTimeUtils)

    implementation(libs.core.coreKtx)

    implementation(libs.cmp.ui)
    implementation(libs.cmp.material3)
    implementation(libs.cmp.components.resources)
    implementation(libs.compose.icons.core)

    debugImplementation(libs.compose.uiTooling)
    screenshotTestImplementation(libs.compose.uiTooling)
    screenshotTestImplementation(libs.screenshot.validation.api)

    implementation(libs.compose.preview)
    implementation(libs.compose.activity)
    implementation(libs.glance.appwidget)
    implementation(libs.glance.preview)
    implementation(libs.glance.appwidget.preview)

    implementation(libs.kotlinx.immutableCollections)

    implementation(libs.coroutines.core)
    runtimeOnly(libs.coroutines.android)

    implementation(libs.datastore.preferences)

    implementation(libs.datastore.datastore)
    implementation(libs.protobuf.javalite)

    implementation(libs.koin.android)
    implementation(libs.koin.android.compose) // lifecycleScope

    // Decompose for navigation
    implementation(libs.decompose.core)
    implementation(libs.decompose.compose)

    // Wearable
    implementation(libs.playServices.wearable)
    // for connectedNodes.await()
    implementation(libs.coroutines.play)

    implementation(libs.lottie.compose)

    testImplementation(libs.robolectric)
    testImplementation(libs.compose.uiTestJunit4)
    testImplementation(libs.kakao.compose)
    testImplementation(libs.glance.appwidget.testing)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.kotlin.test.annotations)
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
