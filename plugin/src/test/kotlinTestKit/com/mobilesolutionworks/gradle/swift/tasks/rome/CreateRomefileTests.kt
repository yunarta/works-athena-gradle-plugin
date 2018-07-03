package com.mobilesolutionworks.gradle.swift.tasks.rome

import junit5.assertMany
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
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
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":romeCreateRomefile")?.outcome

                        isTrue {
                            val text = File(runner.root, "romefile").readText()
                            text.contains("NullFramework = NullFramework")
                        }
                    }
                }
    }

    @Test
    @DisplayName("verify romeCreateRomefile incremental build")
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
            }

            carthage {
                github("yunarta/NullFramework")  {
                    frameworks = setOf("NullFramework")
                } version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeCreateRomefile")
                .build()

        runner.withArguments("romeCreateRomefile")
                .build().let {
                    assertMany {
                        TaskOutcome.UP_TO_DATE expectedFrom it.task(":romeCreateRomefile")?.outcome
                    }
                }
    }

    @Test
    @DisplayName("test S3 Bucket")
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
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":romeCreateRomefile")?.outcome

                        val text = File(runner.root, "romefile").readText()
                        isTrue { text.contains("S3-Bucket = s3Bucket") }
                        isTrue { text.contains("NullFramework = NullFramework") }
                    }
                }
    }
}