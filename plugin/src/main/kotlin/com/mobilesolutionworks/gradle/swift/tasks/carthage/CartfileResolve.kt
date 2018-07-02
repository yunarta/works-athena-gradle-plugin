package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import com.mobilesolutionworks.gradle.swift.model.extension.xcode
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Execute separate carthage update w/o build and checkout in build directory.
 *
 * Task execution conditions
 * - When Cartfile.resolved in root dir is not created
 * - When carthageUpdate is in task queue
 */
internal open class CartfileResolve : DefaultTask() {

    private val workPath = project.file("${project.buildDir}/works-swift/carthage/latest")

    private val workCartfile = project.file("$workPath/Cartfile")
    private val workCartfileResolved = project.file("$workPath/Cartfile.resolved")

    private var cartfile = project.file("${project.rootDir}/Cartfile")
    private var cartfileResolved = project.file("${project.rootDir}/Cartfile.resolved")


    init {
        group = CarthageTaskDef.group

        with(project) {
            // inputs outputs
            inputs.files(cartfile)
            outputs.files(workCartfileResolved)
            outputs.upToDateWhen {
                if (carthage.updates) {
                    false
                } else {
                    cartfileResolved.exists()
                }
            }


            // dependencies
            tasks.withType(CartfileCreate::class.java) {
                this@CartfileResolve.dependsOn(it)
            }
        }
    }

    @TaskAction
    fun resolve() {
        cartfile.copyTo(workCartfile, overwrite = true)
        project.exec {
            it.executable = "carthage"
            it.workingDir = project.file(workPath)

            // task properties
            it.args(mutableListOf<Any?>().apply {
                add("update")
                add("--no-build")
                add("--no-checkout")

                add("--platform")
                add(project.xcode.platformsAsText)
            })
        }
    }
}