package com.mobilesolutionworks.gradle.swift.tasks.rome

import junit5.assertMany
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
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
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":romeCreateRepositoryMap")?.outcome

                        val file = project.file("${project.buildDir}/works-swift/rome/map/NullFramework.txt")
                        "NullFramework = NullFramework" expectedFrom file.readText()
                    }
                }
    }

    @Test
    @DisplayName("verify createRepositoryMap incremental build")
    fun test2(runner: GradleRunner) {
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
                .build()

        runner.withArguments("romeCreateRepositoryMap")
                .build().let {
                    assertMany {
                        TaskOutcome.UP_TO_DATE expectedFrom it.task(":romeCreateRepositoryMap")?.outcome
                    }
                }
    }
}