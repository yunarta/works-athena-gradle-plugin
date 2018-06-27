package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.i18n.Strings
import com.mobilesolutionworks.gradle.swift.model.carthage
import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class ActivateUpdate : DefaultTask() {

    init {
        group = Carthage.group
        description = Strings["CartfileResolve_description"]

        with(project) {
            tasks.withType<CartfileCreate> {
                this@ActivateUpdate.shouldRunAfter(this)
            }
        }
    }

    @TaskAction
    fun run() {
        project.carthage.updates = true
    }
}