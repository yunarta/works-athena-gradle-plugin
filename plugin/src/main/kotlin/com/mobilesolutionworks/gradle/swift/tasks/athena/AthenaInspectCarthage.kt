package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.athena.CarthageBuildFile
import com.mobilesolutionworks.gradle.swift.athena.ArtifactInfo
import com.mobilesolutionworks.gradle.swift.carthage.CarthageAssetLocator
import com.mobilesolutionworks.gradle.swift.model.athena
import com.mobilesolutionworks.gradle.swift.model.carthage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.google.gson.GsonBuilder

class AthenaInspectCarthage : DefaultTask() {

    init {
        group = Athena.group
        with(project) {
            // inputs outputs

            inputs.files(CarthageAssetLocator.versions(project))
        }
    }

//    @TaskAction
//    fun inspect() {
//        with(project) {
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
//        }
//    }
}