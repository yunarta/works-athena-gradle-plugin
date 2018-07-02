package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class ActivateUpdate : DefaultTask() {

    init {
        group = CarthageTaskDef.group

        with(project) {
            tasks.withType(CartfileCreate::class.java) {
                this@ActivateUpdate.dependsOn(it)
                this@ActivateUpdate.shouldRunAfter(it)
            }
        }
    }

    @TaskAction
    fun run() {
        project.carthage.updates = true
    }
}