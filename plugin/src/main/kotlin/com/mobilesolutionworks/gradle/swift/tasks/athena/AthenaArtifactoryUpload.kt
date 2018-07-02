package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

internal open class AthenaArtifactoryUpload : DefaultTask() {

    @Option(option = "upload-dry-run", description = "Test dry run for upload")
    var dryRun = false


    init {
        group = AthenaTaskDef.group

        with(project) {
            tasks.withType(AthenaCreatePackage::class.java) {
                dependsOn(it)
            }
        }
    }

    @TaskAction
    fun upload() {
        project.exec {
            it.executable = "jfrog"
            it.workingDir = project.file("Athena")
            it.args("rt")
            it.args("u")

            println("dryRun = $dryRun")
            if (dryRun) {
                it.args("--dry-run")
            }

            it.args("--flat=false")
            it.args("*.*", "athena")
        }
    }
}