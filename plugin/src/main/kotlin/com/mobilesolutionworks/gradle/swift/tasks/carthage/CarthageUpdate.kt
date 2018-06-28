package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.i18n.Strings
import com.mobilesolutionworks.gradle.swift.model.xcode
import org.gradle.api.tasks.Exec

/**
 * This task activates updating for Cartfile.resolved even though the updates = false
 */
internal open class CarthageUpdate : Exec() {

    init {
        group = Carthage.group
        description = Strings["CartfileResolve_description"]

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
            tasks.withType(ActivateUpdate::class.java).forEach {
                this@CarthageUpdate.dependsOn(it)
            }

            tasks.withType(CartfileResolve::class.java).forEach {
                this@CarthageUpdate.dependsOn(it)
            }
        }
    }
}