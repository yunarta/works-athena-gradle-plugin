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

class CartfileCreateTests {

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
                github("NullFramework", "yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileCreate")?.outcome)
                    assertEquals("""
                        github "yunarta/NullFramework" == 1.0.0
                    """.trimIndent(), File(temporaryFolder.root, "Cartfile").readText())

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
                github("NullFramework", "yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileCreate")?.outcome)
                }

        gradle.runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":carthageCartfileCreate")?.outcome)
                }
    }

    @Test
    fun modification() {
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
                github("NullFramework", "yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileCreate")?.outcome)
                    assertEquals("""
                        github "yunarta/NullFramework" == 1.0.0
                    """.trimIndent(), File(temporaryFolder.root, "Cartfile").readText())

                }

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = false
            }

            carthage {
                github("NullFramework", "yunarta/NullFramework") version "1.1.0"
            }
        """.trimIndent())


        gradle.runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileCreate")?.outcome)
                    assertEquals("""
                        github "yunarta/NullFramework" == 1.1.0
                    """.trimIndent(), File(temporaryFolder.root, "Cartfile").readText())
                }

        File(temporaryFolder.root, "Cartfile").delete()

        gradle.runner.withArguments("carthageCartfileCreate")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileCreate")?.outcome)
                    assertEquals("""
                        github "yunarta/NullFramework" == 1.1.0
                    """.trimIndent(), File(temporaryFolder.root, "Cartfile").readText())
                }
    }
}