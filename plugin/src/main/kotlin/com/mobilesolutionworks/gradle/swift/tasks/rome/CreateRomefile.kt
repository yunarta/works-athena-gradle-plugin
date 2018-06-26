package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileCreate
import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class CreateRomefile : DefaultTask() {

    //
//        val target = file("$projectDir/Works-Swift/romefile")
//
//        dependsOn(romeCreateRepositoryMap.outputs.files)
//
//        outputs.file(target)
//
//        doFirst {
//            target.apply {
//                val repositoryMap = romeCreateRepositoryMap.outputs.files.singleFile.readText()
//                val romeText = """
//[Cache]
//S3-Bucket = ios-dev-bucket
//local = $buildDir/romeOptions/
//
//[RepositoryMap]
//$repositoryMap
//            """.trimIndent()
//                writeText(romeText)
//            }
//        }

    private val workPath = project.file("${project.buildDir}/works-swift/rome/romefile")
    private val romefile = project.file("${project.rootDir}/romefile")

    init {
        group = Rome.group

        with(project) {
            tasks.withType<CreateRepositoryMap> {
                this@CreateRomefile.dependsOn(this)
                this@CreateRomefile.inputs.files(this.outputs)
            }
            outputs.file(romefile)
        }
    }

    @TaskAction
    fun run() {
        val repositoryMap = inputs.files.map {
            it.readText()
        }.joinToString(System.lineSeparator())

        val romeText = """
[Cache]
S3-Bucket = ios-dev-bucket
local = ${project.buildDir}/works-swift/rome/romefile"

[RepositoryMap]
$repositoryMap
            """.trimIndent()
        romefile.writeText(romeText)
    }
}