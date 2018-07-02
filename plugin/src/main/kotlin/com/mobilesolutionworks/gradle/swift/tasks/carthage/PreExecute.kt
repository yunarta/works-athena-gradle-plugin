package com.mobilesolutionworks.gradle.swift.tasks.carthage

import org.gradle.api.DefaultTask

internal open class PreExecute : DefaultTask() {

    init {
        group = CarthageTaskDef.group

        with(project) {
            tasks.withType(CartfileReplace::class.java) {
                this@PreExecute.dependsOn(it)
            }
        }
    }
}