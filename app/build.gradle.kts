import com.google.protobuf.gradle.*

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.protobuf") version "0.8.17"
}

android {
    compileSdk = 31

    defaultConfig {
        // NOTE: This must be the same in the phone app and the wear app for the capabilities API
        applicationId = "ru.beryukhov.coffeegram"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        //useIR = true
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    packagingOptions {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }
}

dependencies {

    implementation(projects.repository)
    implementation(projects.appWearCommon)

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    debugImplementation(libs.compose.uiTooling)
    implementation(libs.compose.preview)

    implementation(libs.compose.constraint)
    implementation(libs.compose.activity)
    implementation("androidx.glance:glance-appwidget:1.0.0-alpha02")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation(libs.compose.uiTest)
    androidTestImplementation(libs.compose.uiTestJunit4)


    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.datastore.preferences)

    implementation  (libs.datastore.datastore)
    implementation  ("com.google.protobuf:protobuf-javalite:3.10.0")

    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.androidx.lifecycle) //lifecycleScope
    val playServicesWearableVersion = "17.1.0"
    val coroutinesVersion = "1.6.0"
    implementation("com.google.android.gms:play-services-wearable:$playServicesWearableVersion") //Wearable
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion") //connectedNodes.await()



}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.10.0"
    }
    generateProtoTasks {
        all().forEach{
                task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
            }
        }

    }
}
