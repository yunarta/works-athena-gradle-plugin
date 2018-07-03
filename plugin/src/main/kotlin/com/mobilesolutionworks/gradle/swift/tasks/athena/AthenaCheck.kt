package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class AthenaCheck : DefaultTask() {

    init {
        group = AthenaTaskDef.group
    }

    @TaskAction
    fun check() {
        println("Swift version ${project.athena.swiftVersion}")
    }
}