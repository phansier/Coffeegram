enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Coffeegram"
include(":app")
include(":repository-room")

includeBuild("build-logic")
include(":wear")
include(":app-wear-common")

include("cmp-app")
include("cmp-common")
include("repository")
include("repository-sqldelight")

include("date-time-utils")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}
