package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

internal open class AthenaArtifactoryUpload : DefaultTask() {

    @Option(option = "upload-dry-run", description = "Test dry run for upload")
    var dryRun = false

    @Option(option = "force-upload", description = "Force upload")
    var forceUpload = false

    init {
        group = AthenaTaskDef.group

        with(project) {
            if (!forceUpload) {
                onlyIf {
                    val listMissing = tasks.withType(AthenaListMissing::class.java).single()
                    listMissing.outputs.files.singleFile.readText().isNotBlank()
                }
            }

            tasks.withType(AthenaCreatePackage::class.java) {
                dependsOn(it)
            }
        }
    }

    @TaskAction
    fun upload() {
        project.exec {
            it.executable = "jfrog"
            it.workingDir = project.athena.workDir
            it.args("rt")
            it.args("u")

            if (dryRun) {
                it.args("--dry-run")
            }

            it.args("--flat=false")
            it.args("*.*", project.athena.repository)
        }
    }
}