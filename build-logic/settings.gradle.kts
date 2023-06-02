rootProject.name = "build-logic"

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }

    repositories {
        google()
        mavenCentral()
    }
}

include("checks")
/**
 * renamed from 'gradle' to prevent IDE resolution conflict:
 * usages of "typesafe project accessors", e.g. `projects.gradle.someProject` was red in IDE
 * build was fine however
 */
include("gradle-ext")
