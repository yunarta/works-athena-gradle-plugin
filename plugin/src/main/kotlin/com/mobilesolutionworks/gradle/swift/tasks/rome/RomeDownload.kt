package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.xcode
import com.mobilesolutionworks.gradle.swift.util.withType
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

                if (xcode.hasDeclaredPlatforms) {
                    add("--platform")
                    add(xcode.declaredPlatforms)
                }
            })

            // dependencies
            tasks.withType<CreateRomefile> {
                this@RomeDownload.dependsOn(this)
            }
        }
    }
}