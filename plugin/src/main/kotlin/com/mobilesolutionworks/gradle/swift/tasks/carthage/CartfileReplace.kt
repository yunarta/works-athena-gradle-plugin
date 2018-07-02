package com.mobilesolutionworks.gradle.swift.tasks.carthage

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class CartfileReplace : DefaultTask() {

    private val workPath = project.file("${project.buildDir}/works-swift/carthage/latest")
    private val workCartfile = project.file("$workPath/Cartfile")

    private val workCartfileResolved = project.file("$workPath/Cartfile.resolved")
    private var cartfileResolved = project.file("${project.rootDir}/Cartfile.resolved")

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

//            // conditions
//            onlyIf {
//                // always run Cartfile.resolved is not exist
//                if (!cartfileResolved.exists()) {
//                    true
//                } else {
//                    // at this phase we would expect that CartfileResolve always produce its output
//                    if (workCartfile.exists()) {
//                        cartfileResolved.readText() != workCartfile.readText()
//                    } else {
//                        // always run when workCartfile is not exists
//                        true
//                    } || carthage.updates
//                }
//
//                when {
//                    carthage.updates -> true
//                    cartfileResolved.exists() && workCartfileResolved.exists() -> {
//
//                    }
//                    else -> {
//
//                    }
//                }
//                if (cartfileResolved.exists()) {
//
//                    carthage.updates || !workCartfileResolved.exists() || cartfileResolved.readText() != workCartfileResolved.readText()
//                } else {
//                    true
//                }
//            }
        }
    }

    @TaskAction
    fun run() {
        workCartfileResolved.copyTo(cartfileResolved, overwrite = true)
    }

}