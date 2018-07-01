package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test AthenaListMissing")
class AthenaListMissingTests {

    @Test
    @DisplayName("verify athenaListMissing")
    fun test1(runner: GradleRunner) {
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

            athena {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework")
                github("ReactiveX/RxSwift")
            }
        """.trimIndent())

        runner.withArguments("athenaListMissing")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":athenaListMissing")?.outcome)
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

            athena {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework")
                github("ReactiveX/RxSwift")
            }
        """.trimIndent())

        runner.withArguments("athenaListMissing")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":athenaListMissing")?.outcome)
                }
        runner.withArguments("athenaListMissing")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":athenaListMissing")?.outcome)
                }
    }
}