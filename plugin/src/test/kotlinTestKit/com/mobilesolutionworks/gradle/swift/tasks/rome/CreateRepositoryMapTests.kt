package com.mobilesolutionworks.gradle.swift.tasks.rome

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.root

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test CreateRepositoryMap")
class CreateRepositoryMapTests {

    @Test
    @DisplayName("verify createRepositoryMap")
    fun test1(runner: GradleRunner) {
        val project = ProjectBuilder().withProjectDir(runner.root).build()

        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework") {
                    frameworks = setOf("NullFramework")
                } version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeCreateRepositoryMap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeCreateRepositoryMap")?.outcome)

                    val mapFile = project.file("${project.buildDir}/works-swift/rome/map/NullFramework.txt")
                    assertEquals("""
                        NullFramework = NullFramework
                    """.trimIndent(), mapFile.readText())
                }
    }
}