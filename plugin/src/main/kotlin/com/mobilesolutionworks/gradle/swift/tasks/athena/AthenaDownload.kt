package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mobilesolutionworks.gradle.swift.model.AthenaPackageVersion
import com.mobilesolutionworks.gradle.swift.model.CarthageAssetLocator
import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact

internal open class AthenaDownload : DefaultTask() {

    private val packages = project.file("${project.buildDir}/works-swift/athena/packages.json")

    init {
        group = AthenaTaskDef.group

        with(project) {
            inputs.file(packages)
            outputs.file(CarthageAssetLocator.resolved(project))

            tasks.withType(AthenaInspectCarthage::class.java) {
                dependsOn(it)
            }
        }
    }

    @TaskAction
    fun download() {
        with(project) {
            val gson = GsonBuilder().create()
            val packages: Map<String, AthenaPackageVersion> = gson.fromJson(packages.reader(),
                    object : TypeToken<Map<String, AthenaPackageVersion>>() {}.type)


            packages.values.map {
                project.dependencies.createArtifactResolutionQuery()
                        .forModule(it.group, it.module, "${it.version}-Swift${athena.swiftVersion}")
                        .withArtifacts(MavenModule::class.java, MavenPomArtifact::class.java)
                        .execute()
            }.flatMap {
                it.resolvedComponents.map { it.id.displayName }
            }.map {
                dependencies.add("athena", it)
            }

            val configuration = project.configurations.getByName("athena")
            configuration.resolve().forEach {
                zipTree(it).visit {
                    it.copyTo(project.file(it.relativePath))
                }
            }
        }
    }
}