package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.mobilesolutionworks.gradle.swift.athena.ArtifactInfo
import com.mobilesolutionworks.gradle.swift.model.AthenaPackageInfo
import com.mobilesolutionworks.gradle.swift.model.athena
import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.IdentityFileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.process.internal.ExecActionFactory
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerExecutor
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject


internal open class AthenaCreatePackage @Inject constructor(val workerExecutor: WorkerExecutor) : DefaultTask() {

    init {
        group = Athena.group

        with(project) {
            tasks.withType(AthenaGenerateArtifacts::class.java) {
                dependsOn(it)
            }
        }
    }

    @Inject
    open fun getExecActionFactory(): ExecActionFactory {
        throw UnsupportedOperationException()
    }

    class ArchiveWorker @Inject constructor(val info: ArtifactInfo, val workingDir: File) : Runnable {

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

            val outputdir = File(workingDir, "build/athena/${info.id.group}-${info.id.module}/${info.framework}-${info.platform}-${info.version}")
            outputdir.mkdirs()


            uuids.forEach { uuid ->
                val file = File(workingDir, "Carthage/Build/${info.platform}/${uuid}.bcsymbolmap")
                if (file.exists()) {
                    executor = execActionFactory.newExecAction()
                    executor.executable = "zip"
                    executor.workingDir = file.parentFile

                    executor.args("-r", "-q")
                    executor.args(File(outputdir, "${uuid}-${info.version}.zip").absolutePath)
                    executor.args(file.name)
                    executor.execute()
                }
            }

            var target: File

            target = File(workingDir, "Carthage/Build/${info.platform}/${info.framework}.framework")
            executor = execActionFactory.newExecAction()
            executor.executable = "zip"
            executor.workingDir = target.parentFile

            executor.args("-r", "-q")
            executor.args(File(outputdir, "${info.framework}.framework-${info.version}.zip").absolutePath)
            executor.args(target.name)
            executor.execute()

            target = File(workingDir, "Carthage/Build/${info.platform}/${info.framework}.framework.dSYM")
            executor = execActionFactory.newExecAction()
            executor.executable = "zip"
            executor.workingDir = target.parentFile

            executor.args("-r", "-q")
            executor.args(File(outputdir, "${info.framework}.framework.dSYM-${info.version}.zip").absolutePath)
            executor.args(target.name)
            executor.execute()

            File(outputdir, "${info.framework}.json").writeText(GsonBuilder().create().toJson(AthenaPackageInfo(
                    info.framework,
                    info.platform.name,
                    info.version,
                    info.hash
            )))
        }
    }

    @TaskAction
    fun run() {
        with(project) {
            athena.packages.forEach { info ->
                workerExecutor.submit(ArchiveWorker::class.java) {
                    it.isolationMode = IsolationMode.NONE
                    it.params(info, project.rootDir)
                }
            }
        }
    }
}
