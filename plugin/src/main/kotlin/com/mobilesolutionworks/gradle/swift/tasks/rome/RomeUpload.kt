package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import com.mobilesolutionworks.gradle.swift.model.extension.xcode
import org.gradle.api.tasks.Exec

internal open class RomeUpload : Exec() {

    init {
        group = RomeTaskDef.group

        with(project) {

            // task properties
            executable = "rome"
            workingDir = file(project.carthage.destination)
            args(kotlin.collections.mutableListOf<Any?>().apply {
                add("upload")

                add("--platform")
                add(xcode.platformsAsText)
            })

            // conditions
            onlyIf {
                tasks.withType(ListMissing::class.java).single().outputs.files.singleFile.readText().isNotBlank()
            }

            // dependencies
            tasks.withType(CreateRomefile::class.java) {
                this@RomeUpload.dependsOn(it)
            }

            doLast {
                tasks.withType(ListMissing::class.java).map {
                    it.outputs.files.forEach { it.delete() }
                }
            }
        }
    }
}