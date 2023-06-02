enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Coffeegram"
include(":app")
include(":repository")

includeBuild("build-logic")
include(":wear")
include(":app-wear-common")
