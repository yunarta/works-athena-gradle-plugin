package com.mobilesolutionworks.gradle.swift

import com.mobilesolutionworks.gradle.swift.model.*
import com.mobilesolutionworks.gradle.swift.tasks.athena.*
import com.mobilesolutionworks.gradle.swift.tasks.carthage.*
import com.mobilesolutionworks.gradle.swift.tasks.rome.*
import com.mobilesolutionworks.gradle.swift.tasks.xcode.Xcode
import com.mobilesolutionworks.gradle.swift.tasks.xcode.XcodeBuildInfo
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec

class SwiftPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("carthage", CarthageSchematic::class.java)
        project.extensions.create("rome", RomeSchematic::class.java)
        project.extensions.create("xcode", XcodeSchematic::class.java)

        val container = project.container(PackageExtension::class.java)
        project.extensions.create("athena", AthenaSchematic::class.java, container)

        project.afterEvaluate {
            with(project) {
                file("${buildDir}/works-swift/rome/cache").mkdirs()

                tasks.create(Xcode.Tasks.XcodeBuildInfo.value, XcodeBuildInfo::class.java)
                tasks.create(Carthage.Tasks.CarthageCartfileCreate.value, CartfileCreate::class.java)
                tasks.create(Carthage.Tasks.CarthageCartfileResolve.value, CartfileResolve::class.java)

                val replace = tasks.create(Carthage.Tasks.CarthageCartfileReplace.value, CartfileReplace::class.java)

                tasks.create(Carthage.Tasks.CarthageActivateUpdate.value, ActivateUpdate::class.java)
                val bootstrap = tasks.create(Carthage.Tasks.CarthageBootstrap.value, CarthageBootstrap::class.java)
                val update = tasks.create(Carthage.Tasks.CarthageUpdate.value, CarthageUpdate::class.java)



                if (rome.enabled) {
                    tasks.create(Rome.Tasks.RomeCreateRepositoryMap.value, CreateRepositoryMap::class.java)
                    tasks.create(Rome.Tasks.RomeCreateRomefile.value, CreateRomefile::class.java)

                    val list = tasks.create(Rome.Tasks.RomeListMissing.value, ListMissing::class.java)
                    val download = tasks.create(Rome.Tasks.RomeDownload.value, RomeDownload::class.java)
                    val upload = tasks.create(Rome.Tasks.RomeUpload.value, RomeUpload::class.java)

                    download.dependsOn(replace)

                    list.dependsOn(replace)
                    list.shouldRunAfter(download)

                    arrayOf(bootstrap, update).forEach {
                        it.dependsOn(download, list)
                        it.finalizedBy(upload)

//                        it.onlyIf {
//                            list.outputs.files.singleFile.readText().isNotBlank()
//                        }
                    }
                }

                if (athena.enabled) {
                    carthage.dependencies.filter { it.group.isNotBlank() }.map { dependency ->
                        athena.resolutions.create(dependency.repo) {
                            it.group = dependency.group
                            it.module = dependency.module
                        }
                    }

                    val inspectCarthage = tasks.create("athenaInspectCarthage", AthenaInspectCarthage::class.java)

                    val download = tasks.create("athenaDownload", AthenaDownload::class.java)
                    val generate = tasks.create("athenaInspectArtifacts", AthenaInspectArtifacts::class.java)
                    val create = tasks.create("athenaCreatePackage", AthenaCreatePackage::class.java)
                    val upload = tasks.create("athenaUpload", AthenaUpload::class.java)

                    tasks.create(Rome.Tasks.RomeCreateRepositoryMap.value, CreateRepositoryMap::class.java)
                    tasks.create(Rome.Tasks.RomeCreateRomefile.value, CreateRomefile::class.java)

                    val list = tasks.create("athenaListMissing", AthenaListMissing::class.java)
//                    val download = tasks.create(Rome.Tasks.RomeDownload.value, RomeDownload::class.java)
//                    val upload = tasks.create(Rome.Tasks.RomeUpload.value, RomeUpload::class.java)

                    inspectCarthage.dependsOn(replace)
                    download.dependsOn(inspectCarthage)


                    val tree = tasks.create("tree", Exec::class.java) {
                        it.executable = "tree"
                        it.workingDir = file("${project.rootDir}/")
                        it.args("-a")
                    }

                    download.finalizedBy(tree)
                    create.finalizedBy(tree)

                    arrayOf(bootstrap, update).forEach {
                        it.dependsOn(download, list)
                        it.finalizedBy(upload)

                        inspectCarthage.shouldRunAfter(it)

//                        it.onlyIf {
//                            list.outputs.files.singleFile.readText().isNotBlank()
//                        }
                    }
                }
            }
        }
    }
}