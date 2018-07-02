package com.mobilesolutionworks.gradle.swift.tasks.carthage

import junit5.assertAll
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.range

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test CarthageUpdate")
class CarthageUpdateTests {

    @Test
    @DisplayName("verify carthageUpdate")
    fun test1(runner: GradleRunner) {
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
                    assertAll {
                        assert {
                            it.task(":carthageActivateUpdate")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthageActivateUpdate"
                        }
                        assert {
                            it.task(":carthageCartfileResolve")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthageCartfileResolve"
                        }
                        assert {
                            it.task(":carthageCartfileReplace")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthageCartfileReplace"
                        }
                        assert {
                            it.task(":carthagePrepareExecution")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthagePrepareExecution"
                        }
                        assert {
                            it.task(":carthageUpdate")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthageUpdate"
                        }
                    }

                    assertTrue {
                        it.tasks.map {
                            it.path
                        }.range(":carthageCartfileCreate", ":carthageCartfileResolve") {
                            it.contains(":carthageActivateUpdate")
                        }
                    }
                }
    }

    @Test
    @DisplayName("test incremental build")
    fun test2(runner: GradleRunner) {
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
                .build()

        runner.withArguments("carthageUpdate")
                .build().let {
                    assertAll {
                        +"""on second execution, while activate is running,
                        |update will be skipped as DSL is the same""".trimMargin()

                        assert {
                            it.task(":carthageActivateUpdate")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthageActivateUpdate"
                        }
                        assert {
                            it.task(":carthageCartfileResolve")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthageCartfileResolve"
                        }
                        assert {
                            it.task(":carthageCartfileReplace")?.outcome equalsTo TaskOutcome.UP_TO_DATE
                            +":carthageCartfileReplace"
                        }
                        assert {
                            it.task(":carthagePrepareExecution")?.outcome equalsTo TaskOutcome.UP_TO_DATE
                            +":carthagePrepareExecution"
                        }
                        assert {
                            it.task(":carthageUpdate")?.outcome equalsTo TaskOutcome.UP_TO_DATE
                            +":carthageUpdate"
                        }
                    }

                    assertTrue {
                        it.tasks.map {
                            it.path
                        }.range(":carthageCartfileCreate", ":carthageCartfileResolve") {
                            it.contains(":carthageActivateUpdate")
                        }
                    }
                }
    }


    @Test
    @DisplayName("DSL change should always run update")
    fun test3(runner: GradleRunner) {
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
                .build()

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = false
            }

            carthage {
                github("yunarta/NullFramework") atLeast "1.1.0"
            }
        """.trimIndent())

        runner.withArguments("carthageUpdate")
                .build().let {
                    assertAll {
                        assert {
                            it.task(":carthageActivateUpdate")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthageActivateUpdate"
                        }
                        assert {
                            it.task(":carthageCartfileResolve")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthageCartfileResolve"
                        }
                        assert {
                            it.task(":carthageCartfileReplace")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthageCartfileReplace"
                        }
                        assert {
                            it.task(":carthagePrepareExecution")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthagePrepareExecution"
                        }
                        assert {
                            it.task(":carthageUpdate")?.outcome equalsTo TaskOutcome.SUCCESS
                            +":carthageUpdate"
                        }
                    }

                    assertTrue {
                        it.tasks.map {
                            it.path
                        }.range(":carthageCartfileCreate", ":carthageCartfileResolve") {
                            it.contains(":carthageActivateUpdate")
                        }
                    }
                }
    }
}