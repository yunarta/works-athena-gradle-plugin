package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class CartfileReplace : DefaultTask() {

    private val workPath = project.file("${project.buildDir}/works-swift/carthage/latest")

    private val workCartfileResolved = project.file("$workPath/Cartfile.resolved")
    private var cartfileResolved = project.file("${project.carthage.destination}/Cartfile.resolved")

    init {
        group = CarthageTaskDef.group

        with(project) {

            // inputs outputs
            inputs.files(workCartfileResolved)
            outputs.files(cartfileResolved)

            // dependencies
            tasks.withType(CartfileResolve::class.java) {
                this@CartfileReplace.dependsOn(it)
            }
        }
    }

    @TaskAction
    fun run() {
        workCartfileResolved.copyTo(cartfileResolved, overwrite = true)
    }
}