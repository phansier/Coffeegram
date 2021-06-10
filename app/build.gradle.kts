plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "ru.beryukhov.coffeegram"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile("proguard-rules.pro")
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(project(":common"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("androidx.core:core-ktx:${rootProject.extra["core_ktx_version"]}")
    implementation("androidx.appcompat:appcompat:${rootProject.extra["appcompat_version"]}")
    implementation("com.google.android.material:material:1.3.0")

    implementation("org.jetbrains.compose.ui:ui:${rootProject.extra["jetbrains_compose_version"]}")
    implementation("org.jetbrains.compose.material:material:${rootProject.extra["jetbrains_compose_version"]}")

    implementation("androidx.activity:activity-compose:1.3.0-beta01")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")


    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutines_version"]}")

}