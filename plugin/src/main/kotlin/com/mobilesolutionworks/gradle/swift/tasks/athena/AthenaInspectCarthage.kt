package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.mobilesolutionworks.gradle.swift.model.CarthageAssetLocator
import com.mobilesolutionworks.gradle.swift.model.CarthageResolved
import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class AthenaInspection(message: String) : RuntimeException(message)

internal open class AthenaInspectCarthage : DefaultTask() {

    private val target = project.file("${project.buildDir}/works-swift/athena/packages.json")

    init {
        group = AthenaTaskDef.group
        description = "Inspect Cartfile.resolved to get modules information to download"

        with(project) {
            // inputs outputs
            inputs.file(CarthageAssetLocator.resolved(project))
            outputs.file(target)
        }
    }

    @TaskAction
    fun inspect() {
        with(project) {
            val unresolved = mutableListOf<String>()
            val packages = CarthageResolved.from(CarthageAssetLocator.resolved(project)) {
                val `package` = athena.resolve(it)
                if (`package` == null) {
                    unresolved.add(it)
                }

                `package`
            }.associateBy { it.module }

            if (unresolved.isNotEmpty()) {
                val resolutions = unresolved.map {
                    """|    "$it" {
                    |        group = "…"
                    |        module = "…"
                    |    }"""
                }.joinToString(System.lineSeparator()).let {
                    """|resolutions {
                       $it
                       |}"""
                }

                throw AthenaInspection("""
                    |
                    |Found non github source in Cartfile.
                    |Athena need to have this resolved in order to have it uploaded
                    |Please add this items in Athena resolutions
                    |
                    $resolutions
                """.trimMargin())
            }

            target.writeText(GsonBuilder().create().toJson(packages))
            athena.packages = packages
        }
    }
}