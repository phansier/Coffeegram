enableFeaturePreview("VERSION_CATALOGS")

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
