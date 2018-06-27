package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.i18n.Strings
import com.mobilesolutionworks.gradle.swift.model.xcode
import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.tasks.Exec

/**
 * This task activates updating for Cartfile.resolved even though the updates = false
 */
internal open class CarthageUpdate : Exec() {

    init {
        group = Carthage.group
        description = Strings["CartfileResolve_description"]


        with(project) {
            // task properties
            executable = "carthage"
            workingDir = file(rootDir)

            args(kotlin.collections.mutableListOf<Any?>().apply {
                add("update")
                if (xcode.hasDeclaredPlatforms) {
                    add("--platform")
                    add(xcode.declaredPlatforms)
                }
            })

            // dependencies
            tasks.withType<ActivateUpdate> {
                this@CarthageUpdate.dependsOn(this)
            }

            tasks.withType<CartfileResolve> {
                this@CarthageUpdate.dependsOn(this)
            }
        }
    }
}