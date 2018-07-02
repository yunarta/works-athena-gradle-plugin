package com.mobilesolutionworks.gradle.swift

import com.mobilesolutionworks.gradle.swift.model.extension.AthenaSchematic
import com.mobilesolutionworks.gradle.swift.model.extension.AthenaUploadTarget
import com.mobilesolutionworks.gradle.swift.model.extension.CarthageSchematic
import com.mobilesolutionworks.gradle.swift.model.extension.PackageExtension
import com.mobilesolutionworks.gradle.swift.model.extension.RomeSchematic
import com.mobilesolutionworks.gradle.swift.model.extension.XcodeSchematic
import com.mobilesolutionworks.gradle.swift.model.extension.athena
import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import com.mobilesolutionworks.gradle.swift.model.extension.rome
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaArtifactoryUpload
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaBintrayUpload
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaCreatePackage
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaDownload
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaInspectArtifacts
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaInspectCarthage
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaListMissing
import com.mobilesolutionworks.gradle.swift.tasks.carthage.ActivateUpdate
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileCreate
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileReplace
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileResolve
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CarthageBootstrap
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CarthageTaskDef
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CarthageUpdate
import com.mobilesolutionworks.gradle.swift.tasks.rome.CreateRepositoryMap
import com.mobilesolutionworks.gradle.swift.tasks.rome.CreateRomefile
import com.mobilesolutionworks.gradle.swift.tasks.rome.ListMissing
import com.mobilesolutionworks.gradle.swift.tasks.rome.RomeDownload
import com.mobilesolutionworks.gradle.swift.tasks.rome.RomeTaskDef
import com.mobilesolutionworks.gradle.swift.tasks.rome.RomeUpload
import com.mobilesolutionworks.gradle.swift.tasks.xcode.XcodeBuildInfo
import com.mobilesolutionworks.gradle.swift.tasks.xcode.XcodeTaskDef
import org.gradle.api.Plugin
import org.gradle.api.Project

class AthenaPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("carthage", CarthageSchematic::class.java)
        project.extensions.create("rome", RomeSchematic::class.java)
        project.extensions.create("xcode", XcodeSchematic::class.java)

        val container = project.container(PackageExtension::class.java)
        project.extensions.create("athena", AthenaSchematic::class.java, project, container)

        project.configurations.create("athena")

        project.afterEvaluate {
            with(project) {
                file("${buildDir}/works-swift/rome/cache").mkdirs()

                tasks.create(XcodeTaskDef.Tasks.XcodeBuildInfo.value, XcodeBuildInfo::class.java)
                tasks.create(CarthageTaskDef.Tasks.CarthageCartfileCreate.value, CartfileCreate::class.java)
                tasks.create(CarthageTaskDef.Tasks.CarthageCartfileResolve.value, CartfileResolve::class.java)

                val replace = tasks.create(CarthageTaskDef.Tasks.CarthageCartfileReplace.value, CartfileReplace::class.java)

                tasks.create(CarthageTaskDef.Tasks.CarthageActivateUpdate.value, ActivateUpdate::class.java)
                val bootstrap = tasks.create(CarthageTaskDef.Tasks.CarthageBootstrap.value, CarthageBootstrap::class.java)
                val update = tasks.create(CarthageTaskDef.Tasks.CarthageUpdate.value, CarthageUpdate::class.java)



                if (rome.enabled) {
                    tasks.create(RomeTaskDef.Tasks.RomeCreateRepositoryMap.value, CreateRepositoryMap::class.java)
                    tasks.create(RomeTaskDef.Tasks.RomeCreateRomefile.value, CreateRomefile::class.java)

                    val list = tasks.create(RomeTaskDef.Tasks.RomeListMissing.value, ListMissing::class.java)
                    val download = tasks.create(RomeTaskDef.Tasks.RomeDownload.value, RomeDownload::class.java)
                    val upload = tasks.create(RomeTaskDef.Tasks.RomeUpload.value, RomeUpload::class.java)

                    download.dependsOn(replace)

                    list.dependsOn(replace)
                    list.shouldRunAfter(download)

                    arrayOf(bootstrap, update).forEach {
                        it.dependsOn(download, list)
//                        it.finalizedBy(upload)

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

                    val upload = if (athena.upload.ordinal == AthenaUploadTarget.Bintray.ordinal) {
                        tasks.create("athenaUpload", AthenaBintrayUpload::class.java)
                    } else {
                        tasks.create("athenaUpload", AthenaArtifactoryUpload::class.java)
                    }

                    tasks.create(RomeTaskDef.Tasks.RomeCreateRepositoryMap.value, CreateRepositoryMap::class.java)
                    tasks.create(RomeTaskDef.Tasks.RomeCreateRomefile.value, CreateRomefile::class.java)

                    val list = tasks.create("athenaListMissing", AthenaListMissing::class.java)
//                    val download = tasks.create(RomeTaskDef.Tasks.RomeDownload.value, RomeDownload::class.java)
//                    val upload = tasks.create(RomeTaskDef.Tasks.RomeUpload.value, RomeUpload::class.java)

                    inspectCarthage.dependsOn(replace)
                    download.dependsOn(inspectCarthage)


                    arrayOf(bootstrap, update).forEach {
                        it.dependsOn(download, list)
//                        it.finalizedBy(upload)

                        inspectCarthage.shouldRunAfter(it)

                        it.onlyIf {
                            list.outputs.files.singleFile.readText().isNotBlank()
                        }
                    }
                }
            }
        }
    }
}