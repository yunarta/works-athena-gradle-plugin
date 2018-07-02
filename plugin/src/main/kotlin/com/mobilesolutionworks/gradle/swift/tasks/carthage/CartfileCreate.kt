package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Execute creation of Cartfile from DSL
 */
internal open class CartfileCreate : DefaultTask() {

    @OutputFile
    val cartfile: File = project.file("${project.projectDir}/Cartfile")

    private var content = ""

    init {
        group = Carthage.group

        with(project) {

            content = carthage.dependencies.joinToString(System.lineSeparator()) {
                it.semantic
            }

            // inputs outputs
            inputs.property("content", content)
            outputs.file(cartfile)
        }
    }

    @TaskAction
    fun run() {
        cartfile.apply {
            writeText(content)
        }
    }
}