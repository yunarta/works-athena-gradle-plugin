package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.gradle.api.tasks.Exec

internal open class AthenaUpload : Exec() {

    init {
        group = Athena.group

        with(project) {
            tasks.withType(AthenaCreatePackage::class.java).whenTaskAdded {
                dependsOn(it)
            }

            executable = "tree"
            workingDir = project.buildDir
        }
    }
}