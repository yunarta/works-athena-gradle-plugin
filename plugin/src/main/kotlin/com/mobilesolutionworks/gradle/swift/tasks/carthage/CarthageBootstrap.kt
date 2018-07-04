package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import com.mobilesolutionworks.gradle.swift.model.extension.xcode
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class CarthageBootstrap : DefaultTask() {

    init {
        group = CarthageTaskDef.group

        with(project) {
            // inputs outputs
            inputs.file(project.file("${project.carthage.destination}/Cartfile.resolved"))
            outputs.dir("${project.carthage.destination}/Carthage")

            // dependencies
            tasks.withType(PreExecute::class.java) {
                this@CarthageBootstrap.dependsOn(it)
            }
        }
    }

    @TaskAction
    fun bootStrap() {
        project.exec { exec ->
            // task properties
            exec.executable = "carthage"
            exec.workingDir = project.carthage.destination

            exec.args(kotlin.collections.mutableListOf<Any?>().apply {
                add("bootstrap")
                add("--cache-builds")

                add("--platform")
                add(project.xcode.platformsAsText)
            })

            project.xcode.swiftToolchains?.let {
                exec.environment("TOOLCHAINS", it)
            }
        }
    }
}
