package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.gradle.testfixtures.ProjectBuilder
import java.io.ByteArrayOutputStream
import java.nio.file.Files

fun swiftVersion(): String {
    val createTempFile = Files.createTempDirectory("gradle").toFile()
    createTempFile.mkdirs()

    val project = ProjectBuilder().withProjectDir(createTempFile).build()
    return ByteArrayOutputStream().let { output ->
        project.exec { exec ->
            exec.executable = "swift"
            exec.args("-version")
            exec.standardOutput = output
        }
        createTempFile.deleteRecursively()
        output.toString()
    }.let {
        val regex = "Apple Swift version (.*) \\((.*)\\)".toRegex()
        regex.findAll(it)
    }.map {
        it.groupValues[1]
    }.single()
}