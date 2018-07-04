package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import com.mobilesolutionworks.gradle.swift.model.extension.xcode
import org.gradle.api.tasks.Exec

internal open class ListMissing : Exec() {

    private val missing = project.file("${project.buildDir}/works-swift/rome/romefile/missing.txt")

    init {
        group = RomeTaskDef.group
        with(project) {
            // inputs outputs
            inputs.file("${project.carthage.destination}/Cartfile.resolved")
            outputs.file(missing)

            // task properties
            executable = "rome"
            workingDir = file(project.carthage.destination)
            args(kotlin.collections.mutableListOf<Any?>().apply {
                add("list")
                add("--missing")

                add("--platform")
                add(xcode.platformsAsText)
            })

            // dependencies
            tasks.withType(CreateRomefile::class.java) {
                this@ListMissing.dependsOn(it)
            }
        }
    }

    override fun exec() {
        standardOutput = missing.outputStream()
        super.exec()
        println(missing.readText())
    }
}