package com.mobilesolutionworks.gradle.swift.tasks.carthage

import junit5.assertAll
import junit5.assertMany
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
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
                        -":carthageActivateUpdate"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageActivateUpdate")?.outcome

                        -":carthageCartfileResolve"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageCartfileResolve")?.outcome

                        -":carthageCartfileReplace"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageCartfileReplace")?.outcome

                        -":carthagePrepareExecution"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthagePrepareExecution")?.outcome

                        -":carthageUpdate"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageUpdate")?.outcome
                    }

                    assertMany {
                        isTrue {
                            it.tasks.map {
                                it.path
                            }.range(":carthageCartfileCreate", ":carthageCartfileResolve") {
                                it.contains(":carthageActivateUpdate")
                            }
                        }
                    }
                }
    }

    @Test
    @DisplayName("verify carthageUpdate incremental build")
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

                        -":carthageActivateUpdate"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageActivateUpdate")?.outcome

                        -":carthageCartfileResolve"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageCartfileResolve")?.outcome

                        -":carthageCartfileReplace"
                        TaskOutcome.UP_TO_DATE expectedFrom it.task(":carthageCartfileReplace")?.outcome

                        -":carthagePrepareExecution"
                        TaskOutcome.UP_TO_DATE expectedFrom it.task(":carthagePrepareExecution")?.outcome

                        -":carthageUpdate"
                        TaskOutcome.UP_TO_DATE expectedFrom it.task(":carthageUpdate")?.outcome
                    }

                    assertMany {
                        isTrue {
                            it.tasks.map {
                                it.path
                            }.range(":carthageCartfileCreate", ":carthageCartfileResolve") {
                                it.contains(":carthageActivateUpdate")
                            }
                        }
                    }
                }
    }


    @Test
    @DisplayName("verify carthageUpdate incremental build")
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
                        -":carthageActivateUpdate"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageActivateUpdate")?.outcome

                        -":carthageCartfileResolve"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageCartfileResolve")?.outcome

                        -":carthageCartfileReplace"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageCartfileReplace")?.outcome

                        -":carthagePrepareExecution"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthagePrepareExecution")?.outcome

                        -":carthageUpdate"
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageUpdate")?.outcome
                    }

                    assertMany {
                        isTrue {
                            it.tasks.map {
                                it.path
                            }.range(":carthageCartfileCreate", ":carthageCartfileResolve") {
                                it.contains(":carthageActivateUpdate")
                            }
                        }
                    }
                }
    }

    @Test
    @DisplayName("verify carthageUpdate with different toolchain")
    fun test4(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            xcode {
                swiftToolchains = "com.apple.dt.toolchain.XcodeDefault"
            }

            rome {
                enabled = false
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageUpdate", "-i")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":carthageUpdate")?.outcome
                    }
                }
    }

}