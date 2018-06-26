package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.i18n.Strings
import com.mobilesolutionworks.gradle.swift.model.carthage
import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class CartfileReplace : DefaultTask() {

    private val workPath = project.file("${project.buildDir}/works-swift/carthage/latest")

    private val workCartfileResolved = project.file("$workPath/Cartfile.resolved")
    private var cartfileResolved = project.file("${project.rootDir}/Cartfile.resolved")

    init {
        group = Carthage.group
        description = Strings["CartfileResolve_description"]

        inputs.files(workCartfileResolved)
        outputs.files(cartfileResolved)

        with(project) {
            tasks.withType<CartfileResolve> {
                this@CartfileReplace.dependsOn(this)
            }


            onlyIf {
                if (cartfileResolved.exists()) {
                    carthage.updates && cartfileResolved.readText() != workCartfileResolved.readText()
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