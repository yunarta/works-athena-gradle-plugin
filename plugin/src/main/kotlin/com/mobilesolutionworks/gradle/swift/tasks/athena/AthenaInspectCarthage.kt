package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.carthage.CarthageAssetLocator
import com.mobilesolutionworks.gradle.swift.carthage.CarthageResolved
import com.mobilesolutionworks.gradle.swift.model.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class AthenaInspection(message: String) : RuntimeException(message)

internal open class AthenaInspectCarthage : DefaultTask() {

    init {
        group = Athena.group
        description = "Inspect Cartfile.resolved to get modules information to download"

        with(project) {
            // inputs outputs
            inputs.file(CarthageAssetLocator.resolved(project))
        }
    }

    @TaskAction
    fun inspect() {
        with(project) {
            val unresolved = mutableListOf<String>()
            val components = CarthageResolved.from(CarthageAssetLocator.resolved(project)) {
                val component = athena.resolvedObjects[it]
                if (component == null) {
                    unresolved.add(it)
                }

                component
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

            athena.components = components
        }
    }
}