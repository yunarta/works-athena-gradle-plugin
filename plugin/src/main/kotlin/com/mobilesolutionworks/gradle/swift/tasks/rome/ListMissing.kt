package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.options.Option

internal open class ListMissing : Exec() {

    private val missing = project.file("${project.buildDir}/works-swift/rome/romefile/missing.txt")

    // Specify outputFile property as
    // command line option.
    // Use as --outputFile filename.
    @Option(option = "skipLocalCache",
            description = "Skip local cache during list check")
    var skipLocalCache = false

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
            })

            // dependencies
            tasks.withType<CreateRomefile> {
                this@ListMissing.dependsOn(this)
            }
        }
    }

    override fun exec() {
        standardOutput = missing.outputStream()
        super.exec()
        println(missing.readText())
    }
}