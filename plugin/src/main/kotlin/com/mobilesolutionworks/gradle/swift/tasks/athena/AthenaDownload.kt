package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.athena.AthenaPackageVersion
import com.mobilesolutionworks.gradle.swift.model.athena
import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.IdentityFileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultExecActionFactory

internal open class AthenaDownload : DefaultTask() {

    init {
        group = Athena.group

        with(project) {
            tasks.withType(AthenaInspectCarthage::class.java) {
                dependsOn(it)
            }
        }
    }

    @TaskAction
    fun download() {
        with(project) {
            val execActionFactory = DefaultExecActionFactory(IdentityFileResolver())
            athena.packages.values.map { info ->
                artifactoryString(info, athena.swiftVersion).let {
                    val executor = execActionFactory.newExecAction()
                    executor.executable = "jfrog"
                    executor.workingDir = file("$buildDir/athena")
                    executor.workingDir.mkdirs()

                    executor.args("rt")
                    executor.args("dl")
//                    executor.args("--explode")
//                    executor.args("--dry-run")
//                    executor.args("--flat=false")
                    executor.args("athena/$it/*.zip")
                    executor.execute()

                    project.fileTree(mapOf(
                            "dir" to file("$buildDir/athena/$it"),
                            "include" to "*.zip"
                    ))
                }.map {
                    val executor = execActionFactory.newExecAction()
                    executor.executable = "unzip"
                    executor.workingDir = projectDir
                    executor.args(it)
                    executor.execute()
                }
            }
        }
    }

    private fun artifactoryString(info: AthenaPackageVersion, version: String): String {
        return "${info.group}/${info.module}/${info.version}-Swift$version"
    }
}