package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

internal open class AthenaArtifactoryUpload : DefaultTask() {

    @Option(option = "server-id", description = "Artifactory Server ID")
    var serverId = ""

    @Option(option = "upload-dry-run", description = "Test dry run for upload")
    var dryRun = false

    @Option(option = "force-upload", description = "Force upload")
    var forceUpload = false

    init {
        group = AthenaTaskDef.group

        with(project) {

            onlyIf {
                if (!forceUpload) {
                    val listMissing = tasks.withType(AthenaListMissing::class.java).single()
                    listMissing.outputs.files.singleFile.readText().isNotBlank()
                } else {
                    true
                }
            }

            tasks.withType(AthenaCreatePackage::class.java) {
                dependsOn(it)
            }
        }
    }

    @TaskAction
    fun upload() {
        val jfrogExecutable: String? = project.extensions.extraProperties["jfrogExecutable"]?.toString()
        project.exec {
            it.executable = jfrogExecutable ?: "jfrog"
            it.workingDir = project.athena.workDir
            it.args("rt")
            it.args("u")


            project.logger.quiet("""
            Uploading to Artifactory
            --------------------
            Dry run = $dryRun
            Force upload = $forceUpload
            Server ID = $serverId
        """.trimIndent())

            if (dryRun) {
                it.args("--dry-run")
            }

            if (serverId.isNotBlank()) {
                it.args("--server-id=${serverId}")
            }

            it.args("--flat=false")
            it.args("*.*", project.athena.repository)
        }
    }
}