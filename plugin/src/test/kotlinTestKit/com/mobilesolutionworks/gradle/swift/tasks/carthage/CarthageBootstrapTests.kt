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

class CarthageBootstrapTests {

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

            carthage {
                updates = false
                platforms = listOf("iOS")
                github("NullFramework", "yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageBootstrap")?.outcome)
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

            carthage {
                platforms = listOf("iOS")
                github("NullFramework", "yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageBootstrap")?.outcome)
                }
        val resolved100 = File(temporaryFolder.root, "Cartfile.resolved").readText()
        File(temporaryFolder.root, "build").deleteRecursively()

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            carthage {
                updates = false
                platforms = listOf("iOS")
                github("NullFramework", "yunarta/NullFramework") version "1.1.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap", "-i")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":carthageBootstrap")?.outcome)
                    assertEquals(resolved100, File(temporaryFolder.root, "Cartfile.resolved").readText())
                }
    }
}