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
    /*kotlinOptions {
        jvmTarget = "11"
        //useIR = true
        freeCompilerArgs = listOf("-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check", "-Xopt-in=kotlin.RequiresOptIn")
    }
    buildFeatures {
        compose = true
    }*/
//    composeOptions {
//        kotlinCompilerExtensionVersion = "${rootProject.extra["compose_version"]}"
//        kotlinCompilerVersion = "${rootProject.extra["kotlin_version"]}"
//    }
}

dependencies {

    implementation(project(":common"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("androidx.core:core-ktx:${rootProject.extra["core_ktx_version"]}")
    implementation("androidx.appcompat:appcompat:${rootProject.extra["appcompat_version"]}")
    implementation("com.google.android.material:material:1.3.0")

    //implementation("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
    //implementation("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
    //implementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")

    implementation("org.jetbrains.compose.ui:ui:${rootProject.extra["jetbrains_compose_version"]}")
    implementation("org.jetbrains.compose.material:material:${rootProject.extra["jetbrains_compose_version"]}")
    //implementation("org.jetbrains.compose.ui:ui-tooling:${rootProject.extra["jetbrains_compose_version"]}")

    implementation("androidx.activity:activity-compose:1.3.0-alpha05")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    //androidTestImplementation("androidx.compose.ui:ui-test:${rootProject.extra["compose_version"]}")
    //androidTestImplementation("androidx.compose.ui:ui-test-junit4:${rootProject.extra["compose_version"]}")


    implementation("com.jakewharton.threetenabp:threetenabp:1.2.4")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutines_version"]}")

}