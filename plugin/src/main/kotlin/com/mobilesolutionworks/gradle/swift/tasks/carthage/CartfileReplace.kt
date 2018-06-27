package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.i18n.Strings
import com.mobilesolutionworks.gradle.swift.model.carthage
import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class CartfileReplace : DefaultTask() {

    private val workPath = project.file("${project.buildDir}/works-swift/carthage/latest")
    private val workCartfile = project.file("$workPath/Cartfile")

    private val workCartfileResolved = project.file("$workPath/Cartfile.resolved")
    private var cartfileResolved = project.file("${project.rootDir}/Cartfile.resolved")

    init {
        group = Carthage.group
        description = Strings["CartfileResolve_description"]

        with(project) {

            // inputs outputs
            inputs.files(workCartfileResolved)
            outputs.files(cartfileResolved)

            // dependencies
            tasks.withType<CartfileResolve> {
                this@CartfileReplace.dependsOn(this)
            }

            // conditions
            onlyIf {
                if (cartfileResolved.exists()) {
                    carthage.updates || !workCartfileResolved.exists() || cartfileResolved.readText() != workCartfileResolved.readText()
                } else {
                    true
                }
            }
        }
    }

    @TaskAction
    fun run() {
        workCartfileResolved.copyTo(cartfileResolved, overwrite = true)
    }

}