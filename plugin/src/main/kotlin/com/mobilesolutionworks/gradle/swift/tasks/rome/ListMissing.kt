package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.extension.xcode
import org.gradle.api.tasks.Exec

internal open class ListMissing : Exec() {

    private val missing = project.file("${project.buildDir}/works-swift/rome/romefile/missing.txt")

    init {
        group = Rome.group
        with(project) {
            // inputs outputs
            inputs.file("${project.rootDir}/Cartfile.resolved")
            outputs.file(missing)

            // task properties
            executable = "rome"
            workingDir = file(rootDir)
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