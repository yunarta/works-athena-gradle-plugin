package com.mobilesolutionworks.gradle.swift.tasks.rome

import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import testKit.DefaultGradleRunner
import testKit.TestWithCoverage

class RomeDownloadTests {

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
                    options.map("NullFramework", setOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("romeDownload")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeDownload")?.outcome)
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
            }

            carthage {
                github("yunarta/NullFramework") { options ->
                    options.map("NullFramework", setOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("romeDownload")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeDownload")?.outcome)
                }

        gradle.runner.withArguments("romeDownload")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":romeDownload")?.outcome)
                }
    }

}