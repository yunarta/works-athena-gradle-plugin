package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mobilesolutionworks.gradle.swift.model.AthenaPackageVersion
import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact

internal open class AthenaListMissing : DefaultTask() {

    private val packages = project.file("${project.buildDir}/works-swift/athena/packages.json")
    private val target = project.file("${project.buildDir}/works-swift/athena/missing.json")

    init {
        group = AthenaTaskDef.group

        with(project) {
            inputs.file("$buildDir/works-swift/athena/packages.json")
            outputs.file(target)

            // dependencies
            tasks.withType(AthenaInspectCarthage::class.java) {
                this@AthenaListMissing.dependsOn(it)
            }
        }
    }

    @TaskAction
    fun listMissing() {
        with(project) {
            val gson = GsonBuilder().create()
            val packages: Map<String, AthenaPackageVersion> = gson.fromJson(packages.reader(),
                    object : TypeToken<Map<String, AthenaPackageVersion>>() {}.type)

            val result = packages.values.map {
                project.dependencies.createArtifactResolutionQuery()
                        .forModule(it.group, it.module, "${it.version}-Swift${athena.swiftVersion}")
                        .withArtifacts(MavenModule::class.java, MavenPomArtifact::class.java)
                        .execute()
            }.flatMap {
                it.resolvedComponents.map { it.id.displayName }
            }

            packages.values.map { info ->
                info.component(athena.swiftVersion)
            }.toMutableList().apply {
                removeAll(result)
            }.joinToString(System.lineSeparator()) {
                "Missing $it"
            }.let {
                println(it)
                target.writeText(it)
            }
        }
    }
}