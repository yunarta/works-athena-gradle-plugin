package com.mobilesolutionworks.gradle.swift.tasks.carthage

import junit5.assertMany
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test CartfileResolve")
class CartfileResolveTests {

    @Test
    @DisplayName("verify carthageCartfileResolve")
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

        runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageCartfileResolve")?.outcome
                    }
                }
    }

    @Test
    @DisplayName("verify carthageCartfileResolve incremental build always run because Cartfile.resolved is not created yet")
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

        runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileResolve")?.outcome)
                }

        runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageCartfileResolve")?.outcome
                    }
                }
    }
}