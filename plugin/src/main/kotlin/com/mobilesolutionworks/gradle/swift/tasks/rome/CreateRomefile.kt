package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.extension.rome
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class CreateRomefile : DefaultTask() {

    private val workPath = project.file("${project.buildDir}/works-swift/rome/romefile")
    private val romefile = project.file("${project.rootDir}/romefile")

    init {
        group = Rome.group

        with(project) {
            tasks.withType(CreateRepositoryMap::class.java) {
                this@CreateRomefile.dependsOn(it)
                this@CreateRomefile.inputs.files(it.outputs)
            }
            outputs.file(romefile)
        }
    }

    @TaskAction
    fun run() {
        val repositoryMap = inputs.files.map {
            it.readText()
        }.joinToString(System.lineSeparator())


        val s3Line = project.rome.s3Bucket.let {
            return@let if (it != null) "S3-Bucket = $it${System.lineSeparator()}" else null
        }

        val romeText = """
[Cache]
local = ${project.rome.cachePath}
${s3Line}

[RepositoryMap]
$repositoryMap
            """.trimIndent()
        romefile.writeText(romeText)
    }
}