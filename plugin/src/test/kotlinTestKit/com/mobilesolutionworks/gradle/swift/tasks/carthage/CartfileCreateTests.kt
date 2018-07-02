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
@DisplayName("Test CartfileCreate")
class CartfileCreateTests {

    @Test
    @DisplayName("verify carthageCartfileCreate")
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

        runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileCreate")?.outcome)
                    assertEquals("""
                        github "yunarta/NullFramework" == 1.0.0
                    """.trimIndent(), File(runner.root, "Cartfile").readText())
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
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = false
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileCreate")?.outcome)
                }

        runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":carthageCartfileCreate")?.outcome)
                }
    }

    @Test
    @DisplayName("verify incremental build after modification")
    fun test3(runner: GradleRunner) {
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

        runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileCreate")?.outcome)
                    assertEquals("""
                        github "yunarta/NullFramework" == 1.0.0
                    """.trimIndent(), File(runner.root, "Cartfile").readText())

                }

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = false
            }

            carthage {
                github("yunarta/NullFramework") version "1.1.0"
            }
        """.trimIndent())


        runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileCreate")?.outcome)
                    assertEquals("""
                        github "yunarta/NullFramework" == 1.1.0
                    """.trimIndent(), File(runner.root, "Cartfile").readText())
                }

        File(runner.root, "Cartfile").delete()

        runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileCreate")?.outcome)
                    assertEquals("""
                        github "yunarta/NullFramework" == 1.1.0
                    """.trimIndent(), File(runner.root, "Cartfile").readText())
                }
    }
}