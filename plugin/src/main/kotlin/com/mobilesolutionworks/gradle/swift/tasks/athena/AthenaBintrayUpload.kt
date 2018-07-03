package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mobilesolutionworks.gradle.swift.model.AthenaPackageVersion
import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

internal open class AthenaBintrayUpload : DefaultTask() {

    private val packages = project.file("${project.buildDir}/works-swift/athena/packages.json")

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
        val gson = GsonBuilder().create()
        val packages: Map<String, AthenaPackageVersion> = gson.fromJson(packages.reader(),
                object : TypeToken<Map<String, AthenaPackageVersion>>() {}.type)

        val athena = project.athena

        packages.values.map { version ->
            project.exec {
                it.executable = "jfrog"
                it.workingDir = project.athena.workDir

                it.args("bt")
                it.args("u")
                it.args("--flat=false")
                it.args("--publish", "--override")

                if (dryRun) {
                    it.args("--dry-run")
                }

                val source = version.source(athena.swiftVersion)
                val target = version.bintrayTarget(athena.swiftVersion)

                it.args("$source/*", "${athena.organization}/${athena.repository}/$target")
            }
        }
    }
}