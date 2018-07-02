package com.mobilesolutionworks.gradle.swift.tasks.carthage

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.root
import java.io.File

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test CartfileReplace")
class CartfileReplaceTests {

    @Test
    @DisplayName("verify carthageCartfileReplace")
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

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                    assertEquals(true, File(runner.root, "Cartfile.resolved").exists())
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
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = false
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":carthageCartfileReplace")?.outcome)
                }
    }

    @Test
    @DisplayName("DSL change always redo replace")
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

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            carthage {
                github("yunarta/NullFramework") version "1.1.0"
            }
        """.trimIndent())

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }
    }

    @Test
    @DisplayName("deleting Cartfile.resolved will redo replace")
    fun test4(runner: GradleRunner) {
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

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        File(runner.root, "Cartfile.resolved").delete()

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

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    // this task is up to date because this just check whether should it resolve
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }
    }

    @Test
    @DisplayName("replace does not execute when no update found (updates = true, build = deleted)")
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

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        File(runner.root, "build").deleteRecursively()

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":carthageCartfileReplace")?.outcome)
                }
    }

    @Test
    @DisplayName("replace executed when update found (update = true, build = deleted)")
    fun test6(runner: GradleRunner) {
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

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        File(runner.root, "build").deleteRecursively()

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = false
            }

            carthage {
                updates = true
                github("yunarta/NullFramework") version "1.1.0"
            }
        """.trimIndent())

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }
    }

    @Test
    @Disabled
    @DisplayName("replacing executed due to DSL changes, even if updates = false")
    fun test7(runner: GradleRunner) {
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
                updates = false
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        File(runner.root, "build").deleteRecursively()

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = false
            }

            carthage {
                updates = false
                github("yunarta/NullFramework") version "1.1.0"
            }
        """.trimIndent())

        runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)

                }
    }
}