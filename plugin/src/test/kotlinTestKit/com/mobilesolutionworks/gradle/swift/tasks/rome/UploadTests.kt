package com.mobilesolutionworks.gradle.swift.tasks.rome

import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import testKit.DefaultGradleRunner
import testKit.TestWithCoverage
import java.io.File

class UploadTests {

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
                enabled = true
                cachePath = file("${"$"}{project.buildDir}/romeCache")
            }

            carthage {
                github("NullFramework", "yunarta/NullFramework") { rome ->
                    rome.map("NullFramework", listOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeUpload")?.outcome)
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
                enabled = true
                cachePath = file("${"$"}{project.buildDir}/romeCache")
            }

            carthage {
                github("NullFramework", "yunarta/NullFramework") { rome ->
                    rome.map("NullFramework", listOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeUpload")?.outcome)
                }

        gradle.runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SKIPPED, it.task(":carthageBootstrap")?.outcome)
                    assertEquals(TaskOutcome.SKIPPED, it.task(":romeUpload")?.outcome)
                }
    }

    @Test
    fun `simulate clean`() {
        temporaryFolder.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = temporaryFolder.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.buildDir}/romeCache")
            }

            carthage {
                github("NullFramework", "yunarta/NullFramework") { rome ->
                    rome.map("NullFramework", listOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeUpload")?.outcome)
                }

        File(temporaryFolder.root, "Carthage").deleteRecursively()
        File(temporaryFolder.root, "build").deleteRecursively()

        gradle.runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SKIPPED, it.task(":carthageBootstrap")?.outcome)
                    assertEquals(TaskOutcome.SKIPPED, it.task(":romeUpload")?.outcome)
                }
    }

}