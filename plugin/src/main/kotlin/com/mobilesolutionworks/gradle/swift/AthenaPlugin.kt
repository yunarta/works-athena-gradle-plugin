package com.mobilesolutionworks.gradle.swift

import com.mobilesolutionworks.gradle.swift.model.Artifactory
import com.mobilesolutionworks.gradle.swift.model.extension.AthenaSchematic
import com.mobilesolutionworks.gradle.swift.model.extension.AthenaUploadTarget
import com.mobilesolutionworks.gradle.swift.model.extension.CarthageSchematic
import com.mobilesolutionworks.gradle.swift.model.extension.PackageExtension
import com.mobilesolutionworks.gradle.swift.model.extension.RomeSchematic
import com.mobilesolutionworks.gradle.swift.model.extension.XcodeSchematic
import com.mobilesolutionworks.gradle.swift.model.extension.athena
import com.mobilesolutionworks.gradle.swift.model.extension.carthage
import com.mobilesolutionworks.gradle.swift.model.extension.rome
import com.mobilesolutionworks.gradle.swift.model.extension.xcode
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaArtifactoryUpload
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaBintrayUpload
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaCheck
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaCreatePackage
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaDownload
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaInspectArtifacts
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaInspectCarthage
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaListMissing
import com.mobilesolutionworks.gradle.swift.tasks.athena.AthenaMavenLocalUpload
import com.mobilesolutionworks.gradle.swift.tasks.carthage.ActivateUpdate
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileCreate
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileReplace
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CartfileResolve
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CarthageBootstrap
import com.mobilesolutionworks.gradle.swift.tasks.carthage.CarthageUpdate
import com.mobilesolutionworks.gradle.swift.tasks.carthage.PreExecute
import com.mobilesolutionworks.gradle.swift.tasks.rome.CreateRepositoryMap
import com.mobilesolutionworks.gradle.swift.tasks.rome.CreateRomefile
import com.mobilesolutionworks.gradle.swift.tasks.rome.ListMissing
import com.mobilesolutionworks.gradle.swift.tasks.rome.RomeDownload
import com.mobilesolutionworks.gradle.swift.tasks.rome.RomeUpload
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.ByteArrayOutputStream

@Suppress("unused")
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
                file("$buildDir/works-swift/rome/cache").mkdirs()

                ByteArrayOutputStream().let { output ->
                    project.exec { exec ->
                        exec.executable = "swift"
                        exec.args("-version")
                        exec.environment("TOOLCHAINS", xcode.swiftToolchains)
                        exec.standardOutput = output
                    }
                    output.toString()
                }.let {
                    val regex = "Apple Swift version (.*) \\((.*)\\)".toRegex()
                    regex.findAll(it).forEach {
                        athena.swiftVersion = it.groupValues[1]
                    }
                }

                if (athena.upload.ordinal == AthenaUploadTarget.MavenLocal.ordinal) {
                    project.repositories.add(project.repositories.mavenLocal())
                }

                tasks.create("carthageCartfileCreate", CartfileCreate::class.java)
                tasks.create("carthageActivateUpdate", ActivateUpdate::class.java)
                tasks.create("carthageCartfileResolve", CartfileResolve::class.java)

                val replace = tasks.create("carthageCartfileReplace", CartfileReplace::class.java)

                val preExecute = tasks.create("carthagePrepareExecution", PreExecute::class.java)

                val bootstrap = tasks.create("carthageBootstrap", CarthageBootstrap::class.java)
                tasks.create("carthageUpdate", CarthageUpdate::class.java)


                if (rome.enabled) {
                    tasks.create("romeCreateRepositoryMap", CreateRepositoryMap::class.java)
                    tasks.create("romeCreateRomefile", CreateRomefile::class.java)

                    val download = tasks.create("romeDownload", RomeDownload::class.java)
                    val list = tasks.create("romeListMissing", ListMissing::class.java)
                    val upload = tasks.create("romeUpload", RomeUpload::class.java)

                    list.dependsOn(replace)
                    download.dependsOn(replace)

                    preExecute.dependsOn(list, download)
                    upload.dependsOn(bootstrap)
                }

                tasks.create("athenaCheck", AthenaCheck::class.java)
                if (athena.enabled) {
                    carthage.dependencies.filter { it.group.isNotBlank() }.map { dependency ->
                        athena.resolutions.create(dependency.repo) {
                            it.group = dependency.group
                            it.module = dependency.module
                        }
                    }

                    val inspectCarthage = tasks.create("athenaInspectCarthage", AthenaInspectCarthage::class.java)
                    val download = tasks.create("athenaDownload", AthenaDownload::class.java)

                    val list = tasks.create("athenaListMissing", AthenaListMissing::class.java)

                    val inspectArtifacts = tasks.create("athenaInspectArtifacts", AthenaInspectArtifacts::class.java)
                    tasks.create("athenaCreatePackage", AthenaCreatePackage::class.java)
                    val upload = if (athena.upload.ordinal == AthenaUploadTarget.Bintray.ordinal) {
                        tasks.create("athenaUpload", AthenaBintrayUpload::class.java)
                    } else if (athena.upload.ordinal == AthenaUploadTarget.Artifactory.ordinal) {
                        tasks.create("athenaUpload", AthenaArtifactoryUpload::class.java)
                    } else {
                        tasks.create("athenaUpload", AthenaMavenLocalUpload::class.java)
                    }

                    inspectCarthage.dependsOn(replace)

                    download.dependsOn(list)

                    preExecute.dependsOn(download)
                    inspectArtifacts.dependsOn(bootstrap)
                }
            }
        }
    }
}