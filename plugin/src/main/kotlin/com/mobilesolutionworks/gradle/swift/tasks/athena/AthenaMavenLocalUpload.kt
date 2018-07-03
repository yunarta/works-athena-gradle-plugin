package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

internal open class AthenaMavenLocalUpload : DefaultTask() {

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
        project.repositories
        val path = project.repositories.mavenLocal().url.path
        project.copy {
            it.from(project.athena.workDir)
            it.into(path)
        }
    }
}