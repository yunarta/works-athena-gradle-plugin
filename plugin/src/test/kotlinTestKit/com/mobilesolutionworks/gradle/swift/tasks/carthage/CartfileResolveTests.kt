package com.mobilesolutionworks.gradle.swift.tasks.carthage

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.root

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
                id("com.mobilesolutionworks.gradle.swift")
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
    }

    @Test
    @DisplayName("incremental will always run because Cartfile.resolved is not created yet")
    fun test2(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
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
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileResolve")?.outcome)
                }
    }

    @Test
    @DisplayName("DSL change should redo resolve")
    fun test3(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
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

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = false
            }

            carthage {
                github("yunarta/NullFramework") version "1.1.0"
            }
        """.trimIndent())

        runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileResolve")?.outcome)
                }
    }

    @Test
    @DisplayName("deletion of build dir should redo resolve")
    fun test4(runner: GradleRunner) {
        val project = ProjectBuilder().withProjectDir(runner.root).build()

        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
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

        project.buildDir.deleteRecursively()

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
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
    }

    @Test
    @DisplayName("resolve always runs when update = true")
    fun test5(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = false
            }

            carthage {
                updates = true
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileResolve")?.outcome)
                }

        runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileResolve")?.outcome)
                }
    }
}