package testKit

import org.gradle.testfixtures.ProjectBuilder
import java.io.File
import java.nio.file.Files

fun <T, R> List<T>.range(begin: T, end: T, closure: (List<T>) -> R): R {

    val from = indexOf(begin)
    val to = indexOf(end)

    return closure(when {
        to < from || from == -1 || to == -1 -> emptyList()
        else -> subList(from, to + 1)
    })
}

fun cleanMavenLocalForTest() {
    val createTempFile = Files.createTempDirectory("gradle").toFile()
    createTempFile.mkdirs()

    val project = ProjectBuilder().withProjectDir(createTempFile).build()

    val mavenLocal = project.repositories.mavenLocal().url.path
    File(mavenLocal, "yunarta").deleteRecursively()
}