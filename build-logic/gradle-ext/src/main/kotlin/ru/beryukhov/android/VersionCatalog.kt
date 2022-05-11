package ru.beryukhov.android

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

/**
 * workaround to make version catalog accessible in convention plugins
 * https://github.com/gradle/gradle/issues/15383
 */
fun Project.withVersionCatalog(block: (libs: LibrariesForLibs) -> Unit) {
    if (project.name != "gradle-kotlin-dsl-accessors") {
        val libs = the<LibrariesForLibs>()
        block.invoke(libs)
    }
}
