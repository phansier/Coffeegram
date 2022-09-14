import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.protobuf") version "0.8.17"
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        // NOTE: This must be the same in the phone app and the wear app for the capabilities API
        applicationId = "ru.beryukhov.coffeegram"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = (100000000 + Instant.now().toEpochMilli() / 1000).toInt()
        versionName = "1.3"

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
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        // useIR = true
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packagingOptions {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }
    namespace = "ru.beryukhov.coffeegram"
}

dependencies {

    implementation(projects.repository)
    implementation(projects.appWearCommon)

    implementation(libs.core.coreKtx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    debugImplementation(libs.compose.uiTooling)
    implementation(libs.compose.preview)

    implementation(libs.compose.constraint)
    implementation(libs.compose.activity)
    implementation(libs.glance.appwidget)

    testImplementation(libs.junit)

    androidTestImplementation(libs.compose.uiTest)
    androidTestImplementation(libs.compose.uiTestJunit4)

    implementation(libs.threetenabp)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.datastore.preferences)

    implementation(libs.datastore.datastore)
    implementation(libs.protobuf.javalite)

    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.androidx.lifecycle) // lifecycleScope

    // Wearable
    implementation(libs.playServices.wearable)
    // for connectedNodes.await()
    implementation(libs.coroutines.play)

    implementation(libs.lottie.compose)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.17.3"
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
