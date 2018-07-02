package com.mobilesolutionworks.gradle.swift.tasks.carthage

import junit5.assert
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile


@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test CarthageBootstrap")
class CarthageBootstrapTests {

    @Test
    @DisplayName("verify carthageBootstrap")
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

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assert {
                        it.task(":carthageBootstrap")?.outcome equalsTo TaskOutcome.SUCCESS
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

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assert {
                        it.task(":carthageBootstrap")?.outcome equalsTo TaskOutcome.SUCCESS
                    }
                }

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assert {
                        it.task(":carthageBootstrap")?.outcome equalsTo TaskOutcome.UP_TO_DATE
                    }
                }
    }

    @Test
    @DisplayName("DSL change should always run bootstrap")
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

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assert {
                        it.task(":carthageBootstrap")?.outcome equalsTo TaskOutcome.SUCCESS
                    }
                }

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            rome {
                enabled = false
            }

            carthage {
                github("yunarta/NullFramework") compatible "1.1.0"
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assert {
                        it.task(":carthageBootstrap")?.outcome equalsTo TaskOutcome.SUCCESS
                    }
                }
    }
}