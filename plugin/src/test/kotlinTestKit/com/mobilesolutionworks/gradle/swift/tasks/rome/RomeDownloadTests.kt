package com.mobilesolutionworks.gradle.swift.tasks.rome

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import testKit.GradleRunnerProvider
import testKit.newFile

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test RomeDownload")
@ResourceLock(value = "rome", mode = ResourceAccessMode.READ_WRITE)
class RomeDownloadTests {

    @Test
    @DisplayName("verify romeDownload")
    fun test1(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeDownload")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeDownload")?.outcome)
                }
    }

    @Test
    @DisplayName("verify incremental build")
    fun test2(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeDownload")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeDownload")?.outcome)
                }

        runner.withArguments("romeDownload")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":romeDownload")?.outcome)
                }
    }
}