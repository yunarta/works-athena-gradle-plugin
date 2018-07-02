package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.model.extension.xcode
import org.gradle.api.tasks.Exec

/**
 * This task activates updating for Cartfile.resolved even though the updates = false
 */
internal open class CarthageUpdate : Exec() {

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
                add("update")
                add("--cache-builds")

                add("--platform")
                add(xcode.platformsAsText)
            })

            // dependencies
            tasks.withType(ActivateUpdate::class.java) {
                this@CarthageUpdate.dependsOn(it)
            }

            tasks.withType(CartfileResolve::class.java) {
                this@CarthageUpdate.dependsOn(it)
            }
        }
    }
}