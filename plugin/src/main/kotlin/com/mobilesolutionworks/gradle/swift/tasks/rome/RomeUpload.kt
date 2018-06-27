package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.xcode
import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.tasks.Exec

internal open class RomeUpload : Exec() {

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

            // conditions
            onlyIf {
                tasks.withType(ListMissing::class.java).map {
                    it.outputs.files.singleFile.readText().isNotBlank()
                }.reduce { a, b -> a && b }
            }

            // dependencies
            tasks.withType<CreateRomefile> {
                this@RomeUpload.dependsOn(this)
            }

            doLast {
                tasks.withType(ListMissing::class.java).map {
                    it.outputs.files.forEach { it.delete() }
                }
            }
        }
    }
}