package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.mobilesolutionworks.gradle.swift.carthage.CarthageBuildFile
import com.mobilesolutionworks.gradle.swift.athena.ArtifactInfo
import com.mobilesolutionworks.gradle.swift.carthage.CarthageAssetLocator
import com.mobilesolutionworks.gradle.swift.carthage.CarthageResolved
import com.mobilesolutionworks.gradle.swift.model.athena
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class AthenaInspection(message: String) : RuntimeException(message) {

}

internal open class AthenaInspectCarthage : DefaultTask() {

    init {
        group = Athena.group
        with(project) {
            // inputs outputs
            inputs.file(CarthageAssetLocator.resolved(project))
            inputs.files(CarthageAssetLocator.versions(project))
        }
    }

    @TaskAction
    fun inspect() {
        with(project) {
            val unresolved = mutableListOf<String>()
            val components = CarthageResolved.from(CarthageAssetLocator.resolved(project)) {
                val component = athena.resolvedObjects[it]
                println("resolving = $it, component = $component")
                if (component == null) {
                    unresolved.add(it)
                }

                component
            }.associateBy { it.module }

            println("unresolved.isNotEmpty() = ${unresolved.isNotEmpty()}")
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

            val gson = GsonBuilder().create()
            val artifacts = CarthageAssetLocator.versions(project).flatMap { file ->
                println("file = ${file}")
                val moduleName = FilenameUtils.getBaseName(file.name).substring(1)
                val component = components[moduleName]
                println("$moduleName -> $component")

                if (component != null) {
                    val buildFile = gson.fromJson(file.reader(), CarthageBuildFile::class.java)
                    buildFile.platforms.flatMap { entry ->
                        entry.value.map {
                            ArtifactInfo(
                                    id = component,
                                    framework = it.name,
                                    version = buildFile.commitish,
                                    platform = entry.key
                            )
                        }
                    }
                } else {
                    emptyList()
                }
            }
            athena.packages = artifacts

            for (info in artifacts) {
                println("info = ${info}")
            }
        }
    }
}