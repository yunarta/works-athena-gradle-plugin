package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.i18n.Strings
import com.mobilesolutionworks.gradle.swift.model.xcode
import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.tasks.Exec

internal open class CarthageBootstrap : Exec() {

    init {
        group = Carthage.group
        description = Strings["CartfileResolve_description"]

        with(project) {
            // inputs outputs
            outputs.dir("$rootDir/Carthage")

            // task properties
            executable = "carthage"
            workingDir = file(rootDir)

            args(kotlin.collections.mutableListOf<Any?>().apply {
                add("bootstrap")
                if (xcode.hasDeclaredPlatforms) {
                    add("--platform")
                    add(xcode.declaredPlatforms)
                }
            })

            // dependencies
            tasks.withType<CartfileReplace> {
                this@CarthageBootstrap.dependsOn(this)
            }
        }
    }
}
