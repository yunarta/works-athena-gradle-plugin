package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.model.extension.xcode
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * This task activates updating for Cartfile.resolved even though the updates = false
 */
internal open class CarthageUpdate : DefaultTask() {

    init {
        group = CarthageTaskDef.group

        with(project) {
            // inputs outputs
            inputs.file(project.file("${project.rootDir}/Cartfile.resolved"))
            outputs.dir("$rootDir/Carthage")

            // dependencies
            tasks.withType(ActivateUpdate::class.java) {
                this@CarthageUpdate.dependsOn(it)
            }

            tasks.withType(PreExecute::class.java) {
                this@CarthageUpdate.dependsOn(it)
            }
        }
    }

    @TaskAction
    fun update() {
        project.exec { exec ->
            // task properties
            exec.executable = "carthage"
            exec.workingDir = project.rootDir

            exec.args(kotlin.collections.mutableListOf<Any?>().apply {
                add("update")
                add("--cache-builds")

                add("--platform")
                add(project.xcode.platformsAsText)
            })

            project.xcode.swiftToolchain?.let {
                exec.environment("TOOLCHAINS", it)
            }
        }
    }
}