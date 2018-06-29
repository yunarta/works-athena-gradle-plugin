package com.mobilesolutionworks.gradle.athena

import com.mobilesolutionworks.gradle.swift.model.CarthageDependency
import com.mobilesolutionworks.gradle.swift.model.carthage
import com.mobilesolutionworks.gradle.swift.model.xcode
import org.gradle.api.Project

internal class AthenaPackager {

    fun createPackagingJobs(project: Project): List<String> {
        val tasks = project.tasks
        return project.carthage.dependencies.flatMap {
            val org = it.group
            makeAthenaModules(project, it).map { modules ->
                "athenaCreatePackage-$org.$modules".also {
                }
            }
        }
    }

    private fun makeAthenaModules(project: Project, it: CarthageDependency): List<String> {
        val xcode = project.xcode
        return if (it.frameworks.isNotEmpty()) {
            it.frameworks.flatMap { framework ->
                xcode.platforms.map { platform ->
                    "$framework-$platform"
                }
            }
        } else {
            xcode.platforms.map { platform ->
                "${it.module}-$platform"
            }
        }
    }
}