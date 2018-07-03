package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class CreateRepositoryMap : DefaultTask() {


    private val workPath = project.file("${project.buildDir}/works-swift/rome/map")

    private var repositoriesMap = emptyList<RomeMap>()

    init {
        with(project) {
            repositoriesMap = carthage.dependencies.mapNotNull {
                if (it.frameworks.isNotEmpty()) {
                    RomeMap(it.module, "${it.module} = ${it.frameworks.joinToString(", ")}")
                } else {
                    RomeMap(it.module, "${it.module} = ${it.module}")
                }
            }

            repositoriesMap.forEach {
                inputs.property(it.name, it.map)
                outputs.files(file("$workPath/${it.name}.txt"))
            }
        }
    }

    @TaskAction
    fun run() {
        repositoriesMap.forEach {
            project.file("$workPath/${it.name}.txt")
                    .writeText(it.map)
        }
    }

    class RomeMap(val name: String, val map: String)
}