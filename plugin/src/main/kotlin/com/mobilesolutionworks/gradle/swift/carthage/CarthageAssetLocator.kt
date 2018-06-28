package com.mobilesolutionworks.gradle.swift.carthage

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree

object CarthageAssetLocator {

    fun versions(project: Project): ConfigurableFileTree {
        return project.fileTree(mapOf(
                "dir" to project.file("Carthage/Build/"),
                "include" to "*.version"
        ))
    }
}