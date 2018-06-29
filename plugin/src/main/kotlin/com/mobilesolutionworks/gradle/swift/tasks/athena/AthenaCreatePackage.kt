package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.cocoa.Platform
import com.mobilesolutionworks.gradle.swift.model.CarthageDependency
import com.mobilesolutionworks.gradle.swift.model.carthage
import com.mobilesolutionworks.gradle.swift.model.xcode
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.internal.file.IdentityFileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.process.internal.ExecActionFactory
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerExecutor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.Serializable
import javax.inject.Inject


internal open class AthenaCreatePackage @Inject constructor(val workerExecutor: WorkerExecutor) : DefaultTask() {

    init {
        group = Athena.group

        with(project) {
            tasks.withType(AthenaCreateVersion::class.java) {
                dependsOn(it)
            }
        }
    }

    @Inject
    open fun getExecActionFactory(): ExecActionFactory {
        throw UnsupportedOperationException()
    }

    class ArchiveWorker @Inject constructor(val info: PackageInfo, val workingDir: File) : Runnable {

        override fun run() {
            val execActionFactory = DefaultExecActionFactory(IdentityFileResolver())
            var executor = execActionFactory.newExecAction()


            executor.executable = "xcrun"
            executor.workingDir = workingDir
            executor.args("dwarfdump", "--uuid",
                    "Carthage/Build/${info.platform}/${info.framework}.framework/${info.framework}")

            val stream = ByteArrayOutputStream()

            executor.standardOutput = stream
            executor.execute()

            val output = stream.toString()
            val uuids = output.lines().filter { it.isNotBlank() }.map {
                it.substring(6).substringBefore(" ")
            }

            val outputdir = File(workingDir, "build/out/${info.org}-${info.module}/${info.framework}-${info.platform}-${info.version}")
            outputdir.mkdirs()


            uuids.forEach { uuid ->
                val file = File(workingDir, "Carthage/Build/${info.platform}/${uuid}.bcsymbolmap")
                if (file.exists()) {
                    executor = execActionFactory.newExecAction()
                    executor.executable = "zip"
                    executor.workingDir = workingDir

                    executor.args("-r")
                    executor.args(File(outputdir, "${uuid}-${info.version}.zip").absolutePath)
                    executor.args(file.absolutePath)
                    executor.execute()
                }
            }

            executor = execActionFactory.newExecAction()
            executor.executable = "zip"
            executor.workingDir = workingDir

            executor.args("-r")
            executor.args(File(outputdir, "${info.framework}.framework-${info.version}.zip").absolutePath)
            executor.args(File(workingDir, "Carthage/Build/${info.platform}/${info.framework}.framework").absolutePath)
            executor.execute()

            executor = execActionFactory.newExecAction()
            executor.executable = "zip"
            executor.workingDir = workingDir

            executor.args("-r")
            executor.args(File(outputdir, "${info.framework}.framework.dSYM-${info.version}.zip").absolutePath)
            executor.args(File(workingDir, "Carthage/Build/${info.platform}/${info.framework}.framework.dSYM").absolutePath)
            executor.execute()
        }
    }

    @TaskAction
    fun run() {
        with(project) {
            project.carthage.dependencies.flatMap {
                makeAthenaModules(project, it).map { info ->
                    workerExecutor.submit(ArchiveWorker::class.java) {
                        it.isolationMode = IsolationMode.NONE
                        it.params(info, project.rootDir)
                    }
                }
            }
        }
    }

    private fun makeAthenaModules(project: Project, dependency: CarthageDependency): List<PackageInfo> {
        val options = dependency.options
        val xcode = project.xcode

        return if (options.frameworks.isNotEmpty()) {
            options.frameworks.flatMap { framework ->
                xcode.declaredPlatforms.map { platform ->
                    PackageInfo(
                            org = dependency.group,
                            platform = platform,
                            module = dependency.module,
                            framework = framework,
                            version = "1.0.0"
                    )
                }
            }
        } else {
            xcode.declaredPlatforms.map { platform ->
                PackageInfo(
                        org = dependency.group,
                        platform = platform,
                        module = dependency.module,
                        framework = dependency.module,
                        version = "1.0.0"
                )
            }
        }
    }

    class PackageInfo(var org: String = "", var platform: Platform, var module: String, var framework: String, var version: String) : Serializable
}
