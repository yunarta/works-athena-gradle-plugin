package com.mobilesolutionworks.gradle.swift.tasks.rome

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.root

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test CreateRepositoryMap")
class ListMissingTests {

    @Test
    @DisplayName("verify romeListMissing")
    fun execution(runner: GradleRunner) {
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
                cachePath = file("${"$"}{project.buildDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeListMissing")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeListMissing")?.outcome)
                    val missing = project.file("${project.buildDir}/works-swift/rome/romefile/missing.txt")
                    assertEquals("NullFramework 1.0.0 : -iOS -Mac -tvOS -watchOS", missing.readText().trimMargin())
                }

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            xcode {
                platforms = setOf("ios", "macos")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.buildDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeListMissing")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeListMissing")?.outcome)
                    val missing = project.file("${project.buildDir}/works-swift/rome/romefile/missing.txt")
                    assertEquals("NullFramework 1.0.0 : -iOS -Mac", missing.readText().trimMargin())
                }
    }

    @Test
    @DisplayName("verify incremental build")
    fun incremental(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.buildDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeListMissing")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeListMissing")?.outcome)
                }

        runner.withArguments("romeListMissing")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":romeListMissing")?.outcome)
                }
    }

    @Test
    @DisplayName("DSL modification will redo list missing")
    fun test3(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.buildDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeListMissing")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeListMissing")?.outcome)
                }

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.buildDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.1.0"
            }
        """.trimIndent())


        runner.withArguments("romeListMissing")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeListMissing")?.outcome)
                }
    }
}