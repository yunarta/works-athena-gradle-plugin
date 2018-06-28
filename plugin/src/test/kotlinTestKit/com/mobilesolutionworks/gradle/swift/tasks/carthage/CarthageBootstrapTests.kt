package com.mobilesolutionworks.gradle.swift.tasks.carthage

import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import testKit.DefaultGradleRunner
import testKit.TestWithCoverage

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

            rome {
                enabled = false
            }

            carthage {
                updates = false
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageBootstrap")?.outcome)
                }
    }

    @Test
    fun `DSL change should always run bootstrap (updates = false)`() {
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

        gradle.runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageBootstrap")?.outcome)
                }

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = false
            }

            carthage {
                github("yunarta/NullFramework") compatible "1.1.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageBootstrap")?.outcome)
                }
    }
}