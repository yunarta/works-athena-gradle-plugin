package com.mobilesolutionworks.gradle.swift.tasks.carthage

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.root
import java.io.File

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test CarthageUpdate")
class CarthageUpdateTests {

    @Test
    @DisplayName("verify carthageUpdate")
    fun test1(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = false
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageUpdate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageUpdate")?.outcome)
                }
        runner.withArguments("carthageUpdate")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":carthageUpdate")?.outcome)
                }
    }

    @Test
    @DisplayName("replacing task should be executed even if updates = false")
    fun test2(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = false
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageUpdate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageUpdate")?.outcome)
                }

        File(runner.root, "build").deleteRecursively()

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = false
            }

            carthage {
                updates = false
                github("yunarta/NullFramework") atLeast "1.1.0"
            }
        """.trimIndent())

        runner.withArguments("carthageUpdate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageUpdate")?.outcome)
                }
    }
}