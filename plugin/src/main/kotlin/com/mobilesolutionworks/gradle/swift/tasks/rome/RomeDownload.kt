package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.extension.xcode
import org.gradle.api.tasks.Exec

internal open class RomeDownload : Exec() {

    init {
        group = RomeTaskDef.group

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
            tasks.withType(CreateRomefile::class.java) {
                this@RomeDownload.dependsOn(it)
            }
        }
    }
}