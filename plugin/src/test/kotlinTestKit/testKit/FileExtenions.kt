package testKit

import org.gradle.testkit.runner.GradleRunner
import java.io.File


val GradleRunner.root: File
    get() = projectDir

fun GradleRunner.newFile(name: String): File {
    return File(this.projectDir, name).apply {
        createNewFile()
    }
}

fun File.newFile(name: String): File {
    return File(this, name).apply {
        createNewFile()
    }
}