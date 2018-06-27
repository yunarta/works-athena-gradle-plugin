package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.xcode
import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.tasks.Exec

internal open class Upload : Exec() {

    init {
        group = Rome.group

        with(project) {

            // task properties
            executable = "rome"
            workingDir = file(rootDir)
            args(kotlin.collections.mutableListOf<Any?>().apply {
                add("upload")

                if (xcode.hasDeclaredPlatforms) {
                    add("--platform")
                    add(xcode.declaredPlatforms)
                }
            })

            // dependencies
            tasks.withType<CreateRomefile> {
                this@Upload.dependsOn(this)
            }
        }
    }
}