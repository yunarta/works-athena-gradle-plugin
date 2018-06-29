package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.carthage.CarthageAssetLocator
import com.mobilesolutionworks.gradle.swift.carthage.CarthageResolved
import com.mobilesolutionworks.gradle.swift.model.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.StopExecutionException
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
            CarthageResolved.from(CarthageAssetLocator.resolved(project)) {
                val component = athena.resolvedObjects[it]
                println("resolving = $it, component = $component")
                if (component == null) {
                    unresolved.add(it)
                }

                component
            }

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

//            val gson = GsonBuilder().create()
//            athena.packages = carthage.dependencies.flatMap { dependency ->
//                val file = CarthageAssetLocator.version(project, dependency.module)
//                if (file.exists()) {
//                    throw StopExecutionException("Cannot find version file for dependency ${dependency.semantic}")
//                }
//
//                val buildFile = gson.fromJson(file.reader(), CarthageBuildFile::class.java)
//                buildFile.platforms.flatMap { entry ->
//                    entry.value.map {
//                        ArtifactInfo(
//                                organization = dependency.org,
//                                module = dependency.module,
//                                framework = it.name,
//                                version = buildFile.commitish,
//                                platform = entry.key
//                        )
//                    }
//                }
//            }
        }
    }
}