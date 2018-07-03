package com.mobilesolutionworks.gradle.swift.tasks.rome

import junit5.assertMany
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test RomeDownload")
class RomeDownloadTests {

    @Test
    @DisplayName("verify romeDownload")
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
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeDownload")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":romeDownload")?.outcome
                    }
                }
    }

    @Test
    @DisplayName("verify romeDownload incremental build")
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
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("romeDownload")
                .build()

        runner.withArguments("romeDownload")
                .build().let {
                    assertMany {
                        TaskOutcome.UP_TO_DATE expectedFrom it.task(":romeDownload")?.outcome
                    }
                }
    }
}