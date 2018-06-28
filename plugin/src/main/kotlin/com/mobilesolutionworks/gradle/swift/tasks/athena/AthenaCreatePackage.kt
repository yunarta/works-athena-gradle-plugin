package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.athena.AthenaPackager
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject


internal open class AthenaCreatePackage @Inject constructor(val workerExecutor: WorkerExecutor) : DefaultTask() {

    init {
        group = Athena.group

    }

    class ArchiveWorker @Inject constructor(val module: String) : Runnable {

        override fun run() {
            println("module = $module")
        }
    }

    @TaskAction
    fun run() {
        with(project) {
            AthenaPackager().createPackagingJobs(project).forEach { module ->
                workerExecutor.submit(ArchiveWorker::class.java) {
                    it.isolationMode = IsolationMode.NONE
                    it.params(module)
                }
            }
        }
    }
}
