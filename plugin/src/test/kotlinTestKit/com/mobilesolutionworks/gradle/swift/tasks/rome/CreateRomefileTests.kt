package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.model.rome
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import testKit.DefaultGradleRunner
import testKit.TestWithCoverage
import java.io.File

class CreateRomefileTests {

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
            }

            carthage {
                github("yunarta/NullFramework") { options ->
                    options.map("NullFramework", listOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("romeCreateRomefile")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeCreateRomefile")?.outcome)

                    val text = File(temporaryFolder.root, "romefile").readText()
                    assertEquals(true, text.contains("NullFramework = NullFramework"))
                }
    }

    @Test
    fun `test s3Bucket`() {
        temporaryFolder.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = temporaryFolder.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = true
                s3Bucket = "s3Bucket"
            }

            carthage {
                github("yunarta/NullFramework") { options ->
                    options.map("NullFramework", listOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("romeCreateRomefile")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeCreateRomefile")?.outcome)

                    val text = File(temporaryFolder.root, "romefile").readText()
                    assertEquals(true, text.contains("S3-Bucket = s3Bucket"))
                    assertEquals(true, text.contains("NullFramework = NullFramework"))
                }
    }

}