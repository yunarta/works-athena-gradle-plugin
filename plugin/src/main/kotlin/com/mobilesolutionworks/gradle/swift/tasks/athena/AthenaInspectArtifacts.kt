package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.mobilesolutionworks.gradle.swift.athena.AthenaFramework
import com.mobilesolutionworks.gradle.swift.athena.AthenaUploadInfo
import com.mobilesolutionworks.gradle.swift.athena.NullAthenaPackageVersion
import com.mobilesolutionworks.gradle.swift.carthage.CarthageAssetLocator
import com.mobilesolutionworks.gradle.swift.carthage.CarthageBuildFile
import com.mobilesolutionworks.gradle.swift.model.athena
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class AthenaInspectArtifacts : DefaultTask() {

    init {
        group = Athena.group
        description = "Create Athena artifact information for upload process"

        with(project) {
            // inputs outputs
            inputs.file(CarthageAssetLocator.resolved(project))
            inputs.files(CarthageAssetLocator.versions(project))
        }
    }

    @TaskAction
    fun generateUploadInfo() {
        with(project) {
            val packages = athena.packages
            val gson = GsonBuilder().create()

            val artifacts = CarthageAssetLocator.versions(project).map { file ->
                val moduleName = FilenameUtils.getBaseName(file.name).removePrefix(".")

                val `package` = packages.getOrDefault(moduleName, NullAthenaPackageVersion)
                val buildFile = gson.fromJson(file.reader(), CarthageBuildFile::class.java)
                AthenaUploadInfo(`package`, athena.swiftVersion, buildFile.platforms.mapValues { entry ->
                    entry.value.map { AthenaFramework(it.name/*, it.hash*/) }
                })
            }

            athena.artifacts = artifacts
        }
    }
}