package com.mobilesolutionworks.gradle.swift.tasks.athena

import junit5.assertAll
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.range

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test carthageUpdate with Athena")
class CarthageUpdateWithAthenaTests {

    @Test
    @DisplayName("verify athenaDownload")
    fun test1(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            repositories {
                maven {
                    url = URI("http://repo.dogeza.club:18090/artifactory/list/athena")
                }
            }

            xcode {
                platforms = setOf("iOS")
            }

            athena {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework")
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

                    Assertions.assertTrue {
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
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            repositories {
                maven {
                    url = URI("http://repo.dogeza.club:18090/artifactory/list/athena")
                }
            }

            xcode {
                platforms = setOf("iOS")
            }

            athena {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework")
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

                    Assertions.assertTrue {
                        it.tasks.map {
                            it.path
                        }.range(":carthageCartfileCreate", ":carthageCartfileResolve") {
                            it.contains(":carthageActivateUpdate")
                        }
                    }

                }
    }

    @Test
    @DisplayName("verify athenaDownload with missing artifact")
    fun test3(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            repositories {
                maven {
                    url = URI("http://repo.dogeza.club:18090/artifactory/list/athena")
                }
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

        runner.withArguments("carthageUpdate", "-x", "athenaUpload")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageUpdate")?.outcome)
                }
    }
}