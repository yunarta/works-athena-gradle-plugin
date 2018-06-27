package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.i18n.Strings
import com.mobilesolutionworks.gradle.swift.model.carthage
import com.mobilesolutionworks.gradle.swift.model.xcode
import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.tasks.Exec

/**
 * Execute separate carthage update w/o build and checkout in build directory.
 *
 * Task execution conditions
 * - When Cartfile.resolved in root dir is not created
 * - When carthageUpdate is in task queue
 */
internal open class CartfileResolve : Exec() {

    private val workPath = project.file("${project.buildDir}/works-swift/carthage/latest")

    private val workCartfile = project.file("$workPath/Cartfile")
    private val workCartfileResolved = project.file("$workPath/Cartfile.resolved")

    private var cartfile = project.file("${project.rootDir}/Cartfile")
    private var cartfileResolved = project.file("${project.rootDir}/Cartfile.resolved")


    init {
        group = Carthage.group
        description = Strings["CartfileResolve_description"]


        with(project) {

            // inputs outputs
            inputs.files(cartfile)
            outputs.files(workCartfileResolved)
            outputs.upToDateWhen {
                !carthage.updates
            }

            executable = "carthage"
            workingDir = file(workPath)

            // task properties
            args(mutableListOf<Any?>().apply {
                add("update")
                add("--no-build")
                add("--no-checkout")

                if (xcode.hasDeclaredPlatforms) {
                    add("--platform")
                    add(xcode.declaredPlatforms)
                }
            })

            // dependencies
            tasks.withType<CartfileCreate> {
                this@CartfileResolve.dependsOn(this)
            }

            // conditions
            onlyIf {
                if (workCartfile.exists()) {
                    val previousCartfile = workCartfile.readText()
                    return@onlyIf !tasks.withType(CartfileCreate::class.java).map {
                        it.content
                    }.contains(previousCartfile)
                } else {
                    carthage.updates || !cartfileResolved.exists()
                }
            }
        }
    }

    override fun exec() {
        cartfile.copyTo(workCartfile, overwrite = true)
        super.exec()
    }
}