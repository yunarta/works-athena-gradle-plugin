package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.mobilesolutionworks.gradle.swift.model.Artifactory
import com.mobilesolutionworks.gradle.swift.model.athena
import org.gradle.api.tasks.Exec
import java.io.ByteArrayOutputStream
import java.io.File

internal open class AthenaListMissing : Exec() {

    init {
        group = Athena.group

        with(project) {
            inputs.file("$buildDir/works-athena/packages.json")
            outputs.file("$buildDir/works-athena/missing.txt")

            executable = "jfrog"
            workingDir = file("$buildDir/athena")
            workingDir.mkdirs()

            args("rt", "s")

            // dependencies
            tasks.withType(AthenaInspectCarthage::class.java) {
                this@AthenaListMissing.dependsOn(it)
            }
        }
    }

    override fun exec() {
        with(project) {
            val gson = GsonBuilder().create()
            val file = File.createTempFile("search", ".spec").apply {
                writeText(athena.packages.values.map { info ->
                    info.artifactory(athena.swiftVersion)
                }.map {
                    Artifactory.FileSpecs.FileSpec("athena/$it/*.zip")
                }.let {
                    Artifactory.FileSpecs(it)
                }.let {
                    gson.toJson(it)
                })
                deleteOnExit()
            }

            args("--spec", file.absolutePath)

            val result = ByteArrayOutputStream().run {
                standardOutput = this
                super.exec()
                toString()
            }.let { json ->
                gson.fromJson(json, Array<Artifactory.ResultSpec>::class.java)
            }.map {
                it.path
            }

            athena.packages.values.map { info ->
                info.artifactoryFile(athena.swiftVersion)
            }.map {
                "athena/$it"
            }.toMutableList().apply {
                removeAll(result)
            }.let {
                project.file("$buildDir/works-swift/athena/missing.txt")
            }
        }
    }
}