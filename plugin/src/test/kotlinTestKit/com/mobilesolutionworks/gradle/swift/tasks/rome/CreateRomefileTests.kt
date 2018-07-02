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
@DisplayName("Test CreateRomefile")
class CreateRomefileTests {

    @Test
    @DisplayName("verify romeCreateRomefile")
    fun test1(runner: GradleRunner) {
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
                github("yunarta/NullFramework")  {
                    frameworks = setOf("NullFramework")
                } version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeCreateRomefile")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeCreateRomefile")?.outcome)

                    val text = File(runner.root, "romefile").readText()
                    assertEquals(true, text.contains("NullFramework = NullFramework"))
                }
    }

    @Test
    @DisplayName("test S3 Bucket")
    fun test2(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = true
                s3Bucket = "s3Bucket"
            }

            carthage {
                github("yunarta/NullFramework")  {
                    frameworks = setOf("NullFramework")
                } version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeCreateRomefile")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeCreateRomefile")?.outcome)

                    val text = File(runner.root, "romefile").readText()
                    assertEquals(true, text.contains("S3-Bucket = s3Bucket"))
                    assertEquals(true, text.contains("NullFramework = NullFramework"))
                }
    }
}