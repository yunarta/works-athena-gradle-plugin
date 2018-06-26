package com.mobilesolutionworks.gradle.swift

import com.mobilesolutionworks.gradle.swift.model.CarthageSchematic
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileCreate
import com.mobilesolutionworks.gradle.swift.tasks.carthage.ActivateUpdate
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileReplace
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileResolve
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CarthageBootstrap
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CarthageUpdate
import com.mobilesolutionworks.gradle.swift.tasks.rome.CreateRepositoryMap
import com.mobilesolutionworks.gradle.swift.tasks.rome.CreateRomefile
import org.gradle.api.Plugin
import org.gradle.api.Project

class SwiftPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("carthage", CarthageSchematic::class.java)
        project.afterEvaluate {
            with(it) {
                file("${project.buildDir}/works-swift/rome/cache").mkdirs()

                tasks.create("carthageCartfileCreate", CartfileCreate::class.java)
                tasks.create("carthageCartfileResolve", CartfileResolve::class.java)
                tasks.create("carthageCartfileReplace", CartfileReplace::class.java)

                tasks.create("carthageActivateUpdate", ActivateUpdate::class.java)

                tasks.create("carthageBootstrap", CarthageBootstrap::class.java)
                tasks.create("carthageUpdate", CarthageUpdate::class.java)

                tasks.create("romeCreateRepositoryMap", CreateRepositoryMap::class.java)
                tasks.create("romeCreateRomefile", CreateRomefile::class.java)
            }
        }
    }
}