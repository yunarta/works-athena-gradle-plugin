package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.mobilesolutionworks.gradle.swift.athena.ArtifactInfo
import com.mobilesolutionworks.gradle.swift.carthage.CarthageAssetLocator
import com.mobilesolutionworks.gradle.swift.carthage.CarthageBuildFile
import com.mobilesolutionworks.gradle.swift.model.athena
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class AthenaGenerateArtifacts : DefaultTask() {

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
            val components = athena.components
            val gson = GsonBuilder().create()
            val artifacts = CarthageAssetLocator.versions(project).flatMap { file ->
                val moduleName = FilenameUtils.getBaseName(file.name).substring(1)
                val component = components[moduleName]

                if (component != null) {
                    val buildFile = gson.fromJson(file.reader(), CarthageBuildFile::class.java)
                    buildFile.platforms.flatMap { entry ->
                        entry.value.map {
                            ArtifactInfo(
                                    id = component,
                                    framework = it.name,
                                    version = buildFile.commitish,
                                    hash = it.hash,
                                    platform = entry.key
                            )
                        }
                    }
                } else {
                    emptyList()
                }
            }
            println(artifacts)
            athena.packages = artifacts
        }
    }
}