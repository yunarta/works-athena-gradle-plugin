package com.mobilesolutionworks.gradle.swift.model

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import java.io.File

internal object CarthageAssetLocator {

    fun resolved(project: Project): File = project.file("Cartfile.resolved")

    fun versions(project: Project): ConfigurableFileTree = project.fileTree(mapOf(
            "dir" to project.file("Carthage/Build/"),
            "include" to "*version"
    ))
}