package com.mobilesolutionworks.gradle.swift.tasks.rome

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.root
import java.io.File

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test RomeUpload")
class RomeUploadTests {

    @Test
    @DisplayName("verify romeUpload")
    fun execution(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            xcode {
                platforms = setOf("iOS")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeUpload")?.outcome)
                }
    }

    @Test
    @DisplayName("test incremental build")
    fun test2(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            xcode {
                platforms = setOf("iOS")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeUpload")?.outcome)
                }

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":carthageBootstrap")?.outcome)
                    assertEquals(TaskOutcome.SKIPPED, it.task(":romeUpload")?.outcome)
                }
    }

    @Test
    @DisplayName("test simulate clean")
    fun test3(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            xcode {
                platforms = setOf("iOS")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeUpload")?.outcome)
                }

        File(runner.root, "Carthage").deleteRecursively()
        File(runner.root, "build").deleteRecursively()

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageBootstrap")?.outcome)
                    assertEquals(TaskOutcome.SKIPPED, it.task(":romeUpload")?.outcome)
                }
    }
}