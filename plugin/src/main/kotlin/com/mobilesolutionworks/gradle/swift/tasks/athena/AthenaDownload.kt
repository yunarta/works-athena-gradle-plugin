package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.athena.ArtifactInfo
import com.mobilesolutionworks.gradle.swift.athena.ComponentWithVersion
import com.mobilesolutionworks.gradle.swift.model.athena
import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.IdentityFileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultExecActionFactory

internal open class AthenaDownload : DefaultTask() {

    init {
        group = Athena.group

        with(project) {
            tasks.withType(AthenaInspectCarthage::class.java) {
                dependsOn(it)
            }
        }
    }

    @TaskAction
    fun download() {
//        with(project) {
//            val execActionFactory = DefaultExecActionFactory(IdentityFileResolver())
//            athena.components.values.forEach { info ->
//                println("packages = ${info}")
//                artifactoryString(info).let {
//                    val executor = execActionFactory.newExecAction()
//                    executor.executable = "jfrog"
//                    executor.workingDir = file("$buildDir/athena")
//
//                    executor.args("rt")
//                    executor.args("d")
////                    executor.args("--dry-run")
//                    executor.args("--flat=false")
//                    executor.args("athena/${it}", "athena")
//                    println("execute")
//                    executor.execute()
//                }
//
//                //                info.
//                //                workerExecutor.submit(AthenaCreatePackage.ArchiveWorker::class.java) {
////                    it.isolationMode = IsolationMode.NONE
////                    it.params(info, project.rootDir)
////                }
//            }
//        }
    }

//    fun artifactoryString(info: ComponentWithVersion): String {
//        return "${info.group}-${info.module}/${info.framework}-${info.platform}-${info.version}"
//    }
}