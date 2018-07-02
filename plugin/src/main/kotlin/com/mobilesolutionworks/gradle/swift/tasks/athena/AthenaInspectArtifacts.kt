package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mobilesolutionworks.gradle.swift.model.AthenaFramework
import com.mobilesolutionworks.gradle.swift.model.AthenaPackageVersion
import com.mobilesolutionworks.gradle.swift.model.AthenaUploadInfo
import com.mobilesolutionworks.gradle.swift.model.CarthageAssetLocator
import com.mobilesolutionworks.gradle.swift.model.CarthageBuildFile
import com.mobilesolutionworks.gradle.swift.model.NullAthenaPackageVersion
import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class AthenaInspectArtifacts : DefaultTask() {

    private val packages = project.file("${project.buildDir}/works-swift/athena/packages.json")
    private val target = project.file("${project.buildDir}/works-swift/athena/artifacts.json")

    init {
        group = AthenaTaskDef.group
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
            val gson = GsonBuilder().create()
            val packages: Map<String, AthenaPackageVersion> = gson.fromJson(packages.reader(),
                    object : TypeToken<Map<String, AthenaPackageVersion>>() {}.type)

            val artifacts = CarthageAssetLocator.versions(project).map { file ->
                val moduleName = FilenameUtils.getBaseName(file.name).removePrefix(".")
                val `package` = packages.getOrDefault(moduleName, NullAthenaPackageVersion)

                val buildFile = gson.fromJson(file.reader(), CarthageBuildFile::class.java)
                AthenaUploadInfo(`package`, athena.swiftVersion, buildFile.platforms.mapValues { entry ->
                    entry.value.map { AthenaFramework(it.name/*, it.hash*/) }
                })
            }

            target.writeText(gson.toJson(artifacts))
            athena.artifacts = artifacts
        }
    }
}