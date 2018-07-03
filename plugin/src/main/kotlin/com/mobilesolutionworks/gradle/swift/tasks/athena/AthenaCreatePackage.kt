package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mobilesolutionworks.gradle.swift.model.AthenaUploadInfo
import com.mobilesolutionworks.gradle.swift.model.extension.athena
import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.IdentityFileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerExecutor
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject


internal open class AthenaCreatePackage @Inject constructor(private val workerExecutor: WorkerExecutor) : DefaultTask() {

    private val source = project.file("${project.buildDir}/works-swift/athena/artifacts.json")

    init {
        group = AthenaTaskDef.group

        with(project) {
            outputs.dir(project.athena.workDir)

            tasks.withType(AthenaInspectArtifacts::class.java) {
                dependsOn(it)
            }
        }
    }

    @TaskAction
    fun run() {
        with(project) {
            val gson = GsonBuilder().create()
            val artifacts: List<AthenaUploadInfo> = gson.fromJson(source.reader(),
                    object : TypeToken<List<AthenaUploadInfo>>() {}.type)

            artifacts.forEach { info ->
                workerExecutor.submit(ArchiveWorker::class.java) {
                    it.isolationMode = IsolationMode.NONE
                    it.params(info, project.rootDir, project.athena.workDir)
                }
            }
        }
    }

    private class ArchiveWorker @Inject constructor(
            private val info: AthenaUploadInfo,
            private val workingDir: File,
            private val outputRoot: File
    ) : Runnable {

        override fun run() {
            val execActionFactory = DefaultExecActionFactory(IdentityFileResolver())

            val files = info.frameworks.flatMap { entry ->
                val platform = entry.key
                val platformOutputDir = "Carthage/Build/$platform"

                entry.value.flatMap { it ->
                    val executor = execActionFactory.newExecAction()
                    executor.executable = "xcrun"
                    executor.workingDir = workingDir
                    executor.args("dwarfdump", "--uuid",
                            "$platformOutputDir/${it.name}.framework/${it.name}")

                    val mutableList = mutableListOf(
                            "${it.name}.framework",
                            "${it.name}.framework.dSYM"
                    )

                    val stream = ByteArrayOutputStream()
                    executor.standardOutput = stream
                    executor.execute()

                    val output = stream.toString()
                    output.lines().filter { it.isNotBlank() }.map {
                        it.substring(6 until it.length).substringBefore(" ")
                    }.filter {
                        File(workingDir, "$platformOutputDir/$it.bcsymbolmap").exists()
                    }.map {
                        mutableList.add("$it.bcsymbolmap")
                    }

                    mutableList.map {
                        "Carthage/Build/$platform/$it"
                    }
                }
            }

            val artifactVersion = "${info.version.version}-Swift${info.swiftVersion}"
            val outputDir = File(outputRoot, "${info.version.group}/${info.version.module}/$artifactVersion")
            outputDir.mkdirs()

            val target = File(outputDir, "${info.version.module}-$artifactVersion.zip")

            val pom = """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>${info.version.group}</groupId>
                  <artifactId>${info.version.module}</artifactId>
                  <version>$artifactVersion</version>
                  <packaging>zip</packaging>
                  <licenses>
                    <license>
                      <name>The Apache Software License, Version 2.0</name>
                      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
                      <distribution>repo</distribution>
                    </license>
                  </licenses>
                </project>
            """.trimIndent()
            File(outputDir, "${info.version.module}-$artifactVersion.pom").writeText(pom)

            val executor = execActionFactory.newExecAction()
            executor.executable = "zip"
            executor.workingDir = workingDir

            executor.args("-r", "-q")
            executor.args(target.absolutePath)
            executor.args("Carthage/Build/.${info.version.module}.version")
            files.forEach {
                executor.args(it)
            }
            executor.execute()
        }
    }
}
