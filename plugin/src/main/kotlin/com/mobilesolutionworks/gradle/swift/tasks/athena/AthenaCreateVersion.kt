package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.mobilesolutionworks.gradle.athena.CarthageBuildFile
import com.mobilesolutionworks.gradle.swift.model.CarthageDependency
import com.mobilesolutionworks.gradle.swift.model.carthage
import com.mobilesolutionworks.gradle.swift.model.xcode
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

internal open class AthenaCreateVersion : DefaultTask() {

    init {
        group = Athena.group
    }

    @TaskAction
    fun run() {
        with(project) {
            carthage.dependencies.forEach {
                val versions = project.file("${project.rootDir}/Carthage/Build/.${it.module}.version")

                val gson = GsonBuilder().create()
                val buildFile = gson.fromJson(versions.reader(), CarthageBuildFile::class.java)
                buildFile.platforms.forEach { platform, items ->
                    println("Platform = ${platform}")
                    items.forEach {
                        println("  Build = ${it.name}, Hash = ${it.hash}")
                    }
                }

                makeAthenaModules(project, it).forEach { info ->
                    buildFile.platforms.getOrDefault(info.platform, emptyList()).filter {
                        it.name == info.module
                    }.forEach {
                        
                    }
                }
            }
        }
    }

    private fun makeAthenaModules(project: Project, dependency: CarthageDependency): List<AthenaCreatePackage.PackageInfo> {
        val options = dependency.options
        val xcode = project.xcode

        return if (options.frameworks.isNotEmpty()) {
            options.frameworks.flatMap { framework ->
                xcode.declaredPlatforms.map { platform ->
                    AthenaCreatePackage.PackageInfo(
                            org = dependency.org,
                            platform = platform,
                            module = dependency.module,
                            framework = framework,
                            version = "1.0.0"
                    )
                }
            }
        } else {
            xcode.declaredPlatforms.map { platform ->
                AthenaCreatePackage.PackageInfo(
                        org = dependency.org,
                        platform = platform,
                        module = dependency.module,
                        framework = dependency.module,
                        version = "1.0.0"
                )
            }
        }
    }

}