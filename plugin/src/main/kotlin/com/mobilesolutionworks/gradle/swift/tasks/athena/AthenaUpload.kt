package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.gradle.api.tasks.Exec

internal open class AthenaUpload : Exec() {

    init {
        group = Athena.group

        with(project) {
            tasks.withType(AthenaCreatePackage::class.java) {
                dependsOn(it)
            }

            executable = "jfrog"
            workingDir = file("$buildDir/athena")
            args("rt")
            args("u")
            args("--dry-run")
            args("--flat=false")
            args("*.*", "athena")
        }
    }
}