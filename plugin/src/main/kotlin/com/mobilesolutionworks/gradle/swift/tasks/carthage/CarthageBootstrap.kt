package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.model.extension.xcode
import org.gradle.api.tasks.Exec

internal open class CarthageBootstrap : Exec() {

    init {
        group = CarthageTaskDef.group

        with(project) {
            // inputs outputs
            inputs.file(project.file("${project.rootDir}/Cartfile.resolved"))
            outputs.dir("$rootDir/Carthage")

            // task properties
            executable = "carthage"
            workingDir = file(rootDir)

            args(kotlin.collections.mutableListOf<Any?>().apply {
                add("bootstrap")
                add("--cache-builds")

                add("--platform")
                add(xcode.platformsAsText)
            })

            // dependencies
            tasks.withType(PreExecute::class.java) {
                this@CarthageBootstrap.dependsOn(it)
            }
        }
    }
}
