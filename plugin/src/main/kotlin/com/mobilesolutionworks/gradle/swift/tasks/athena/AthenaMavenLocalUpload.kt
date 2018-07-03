package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

internal open class AthenaMavenLocalUpload : DefaultTask() {

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
        val path = project.repositories.mavenLocal().url.path
        project.copy {
            it.from(project.athena.workDir)
            it.into(path)
        }
    }
}