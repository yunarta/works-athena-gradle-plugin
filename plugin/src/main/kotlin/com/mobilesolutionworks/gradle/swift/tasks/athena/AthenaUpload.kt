package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.options.Option

internal open class AthenaUpload : Exec() {

    @Option(option = "upload-dry-run", description = "Test dry run for upload")
    var dryRun = false


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

            if (dryRun) {
                args("--dry-run")
            }
            args("--flat=false")
            args("*.*", "athena")
        }
    }
}