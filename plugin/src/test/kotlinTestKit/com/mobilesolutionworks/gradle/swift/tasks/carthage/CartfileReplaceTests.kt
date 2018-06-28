package com.mobilesolutionworks.gradle.swift.tasks.carthage

import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import testKit.DefaultGradleRunner
import testKit.TestWithCoverage
import java.io.File

class CartfileReplaceTests {

    val temporaryFolder = TemporaryFolder()

    var gradle = DefaultGradleRunner(temporaryFolder)

    @JvmField
    @Rule
    val rule = RuleChain.outerRule(temporaryFolder)
            .around(TestWithCoverage(temporaryFolder))
            .around(gradle)

    @Test
    fun execution() {
        temporaryFolder.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = temporaryFolder.newFile("build.gradle.kts")
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

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                    assertEquals(true, File(temporaryFolder.root, "Cartfile.resolved").exists())
                }
    }

    @Test
    fun incremental() {
        temporaryFolder.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = temporaryFolder.newFile("build.gradle.kts")
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

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":carthageCartfileReplace")?.outcome)
                }
    }

    @Test
    fun `DSL change always redo replace`() {
        temporaryFolder.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = temporaryFolder.newFile("build.gradle.kts")
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

        gradle.runner.withArguments("carthageCartfileReplace")
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

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }
    }

    @Test
    fun `deleting Cartfile-dot-resolved will redo replace`() {
        temporaryFolder.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = temporaryFolder.newFile("build.gradle.kts")
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

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        File(temporaryFolder.root, "Cartfile.resolved").delete()

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

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    // this task is up to date because this just check whether should it resolve
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }
    }

    @Test
    fun `replace does not execute when no update found (updates = true, build = deleted)`() {
        temporaryFolder.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = temporaryFolder.newFile("build.gradle.kts")
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

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        File(temporaryFolder.root, "build").deleteRecursively()

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":carthageCartfileReplace")?.outcome)
                }
    }

    @Test
    fun `replace executed when update found (update = true, build = deleted)`() {
        temporaryFolder.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = temporaryFolder.newFile("build.gradle.kts")
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

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        File(temporaryFolder.root, "build").deleteRecursively()

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

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }
    }

    //    @Test
    fun `replacing executed due to DSL changes, even if updates = false`() {
        temporaryFolder.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = temporaryFolder.newFile("build.gradle.kts")
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

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)
                }

        File(temporaryFolder.root, "build").deleteRecursively()

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

        gradle.runner.withArguments("carthageCartfileReplace")
                .build().let {
                    File(temporaryFolder.root, "Cartfile.resolved").readText().also {
                        println(it)
                    }
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileReplace")?.outcome)

                }
    }

}