package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.xcode
import org.gradle.api.tasks.Exec

internal open class RomeDownload : Exec() {

    init {
        group = Rome.group

        with(project) {
            // inputs outputs
            outputs.dir("$rootDir/Carthage")

            // task properties
            executable = "rome"
            workingDir = file(rootDir)
            args(kotlin.collections.mutableListOf<Any?>().apply {
                add("download")

                add("--platform")
                add(xcode.platformsAsText)
            })

            // dependencies
            tasks.withType(CreateRomefile::class.java).forEach {
                this@RomeDownload.dependsOn(it)
            }
        }
    }
}