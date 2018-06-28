package com.mobilesolutionworks.gradle.swift

import com.mobilesolutionworks.gradle.swift.model.CarthageSchematic
import com.mobilesolutionworks.gradle.swift.model.RomeSchematic
import com.mobilesolutionworks.gradle.swift.model.XcodeSchematic
import com.mobilesolutionworks.gradle.swift.model.rome
import com.mobilesolutionworks.gradle.swift.tasks.carthage.ActivateUpdate
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileCreate
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileReplace
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileResolve
import com.mobilesolutionworks.gradle.swift.tasks.carthage.Carthage
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CarthageBootstrap
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CarthageUpdate
import com.mobilesolutionworks.gradle.swift.tasks.rome.CreateRepositoryMap
import com.mobilesolutionworks.gradle.swift.tasks.rome.CreateRomefile
import com.mobilesolutionworks.gradle.swift.tasks.rome.ListMissing
import com.mobilesolutionworks.gradle.swift.tasks.rome.Rome
import com.mobilesolutionworks.gradle.swift.tasks.rome.RomeDownload
import com.mobilesolutionworks.gradle.swift.tasks.rome.RomeUpload
import com.mobilesolutionworks.gradle.swift.tasks.xcode.Xcode
import com.mobilesolutionworks.gradle.swift.tasks.xcode.XcodeBuildInfo
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPlugin

class SwiftPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("carthage", CarthageSchematic::class.java)
        project.extensions.create("rome", RomeSchematic::class.java)
        project.extensions.create("xcode", XcodeSchematic::class.java)

        project.pluginManager.apply(ArtifactoryPlugin::class.java)

        project.afterEvaluate {
            with(it) {
                file("${project.buildDir}/works-swift/rome/cache").mkdirs()

                tasks.create(Xcode.Tasks.XcodeBuildInfo.value, XcodeBuildInfo::class.java)
                tasks.create(Rome.Tasks.RomeCreateRepositoryMap.value, CreateRepositoryMap::class.java)
                tasks.create(Rome.Tasks.RomeCreateRomefile.value, CreateRomefile::class.java)

                val list = tasks.create(Rome.Tasks.RomeListMissing.value, ListMissing::class.java)
                val download = tasks.create(Rome.Tasks.RomeDownload.value, RomeDownload::class.java)
                val upload = tasks.create(Rome.Tasks.RomeUpload.value, RomeUpload::class.java)

                tasks.create(Carthage.Tasks.CarthageCartfileCreate.value, CartfileCreate::class.java)
                tasks.create(Carthage.Tasks.CarthageCartfileResolve.value, CartfileResolve::class.java)

                val replace = tasks.create(Carthage.Tasks.CarthageCartfileReplace.value, CartfileReplace::class.java)

                tasks.create(Carthage.Tasks.CarthageActivateUpdate.value, ActivateUpdate::class.java)
                val bootstrap = tasks.create(Carthage.Tasks.CarthageBootstrap.value, CarthageBootstrap::class.java)
                val update = tasks.create(Carthage.Tasks.CarthageUpdate.value, CarthageUpdate::class.java)

                download.dependsOn(replace)

                list.dependsOn(replace)
                list.shouldRunAfter(download)

                if (rome.enabled) {
                    arrayOf(bootstrap, update).forEach {
                        it.dependsOn(download, list)
                        it.finalizedBy(upload)

                        it.onlyIf {
                            list.outputs.files.singleFile.readText().isNotBlank()
                        }
                    }
                }
            }
        }
    }
}