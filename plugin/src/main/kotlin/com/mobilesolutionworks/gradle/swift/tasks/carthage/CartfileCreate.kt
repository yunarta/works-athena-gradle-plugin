package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.i18n.Strings
import com.mobilesolutionworks.gradle.swift.model.carthage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Execute creation of Cartfile from DSL
 */
internal open class CartfileCreate : DefaultTask() {

    @OutputFile
    val cartfile: File = project.file("${project.projectDir}/Cartfile")

    @Input
    var content = ""

    init {
        group = Carthage.group
        description = Strings["CartfileCreate_description"]

        with(project) {
            outputs.file(cartfile)
            content = carthage.dependencies.joinToString(System.lineSeparator()) {
                it.semantic
            }
        }
    }

    @TaskAction
    fun run() {
        cartfile.apply {
            writeText(content)
        }
    }
}