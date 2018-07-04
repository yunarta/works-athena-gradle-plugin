package com.mobilesolutionworks.gradle.swift.model

import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import java.io.File

internal object CarthageAssetLocator {

    fun resolved(project: Project): File = project.file("${project.carthage.destination}/Cartfile.resolved")

    fun versions(project: Project): ConfigurableFileTree = project.fileTree(mapOf(
            "dir" to project.file("${project.carthage.destination}/Carthage/Build/"),
            "include" to "*version"
    ))
}