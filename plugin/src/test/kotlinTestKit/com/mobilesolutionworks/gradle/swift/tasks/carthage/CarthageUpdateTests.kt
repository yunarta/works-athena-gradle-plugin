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

class CarthageUpdateTests {

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

        gradle.runner.withArguments("carthageUpdate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageUpdate")?.outcome)
                }
    }

    @Test
    fun `replacing task should be executed even if updates = false`() {
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

        gradle.runner.withArguments("carthageUpdate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageUpdate")?.outcome)
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

        gradle.runner.withArguments("carthageUpdate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageUpdate")?.outcome)
                }
    }

}