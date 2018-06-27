package com.mobilesolutionworks.gradle.swift.tasks.carthage

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import testKit.DefaultGradleRunner
import testKit.TestWithCoverage

class CartfileResolveTests {

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

        gradle.runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileResolve")?.outcome)
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

        gradle.runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileResolve")?.outcome)
                }

        gradle.runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SKIPPED, it.task(":carthageCartfileResolve")?.outcome)
                }
    }

    @Test
    fun modification() {
        val project = ProjectBuilder().withProjectDir(temporaryFolder.root).build()

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
                github("NullFramework", "yunarta/NullFramework") { rome ->
                    rome.map("NullFramework", listOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageCartfileResolve")
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
                github("NullFramework", "yunarta/NullFramework") { rome ->
                    rome.map("NullFramework", listOf("NullFramework"))
                } version "1.1.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileResolve")?.outcome)
                }

        with(project.file("${project.buildDir}/works-swift/carthage/latest/Cartfile.resolved")) {
            org.junit.Assert.assertTrue(exists())
            delete()
        }

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = false
            }

            carthage {
                github("NullFramework", "yunarta/NullFramework") { rome ->
                    rome.map("NullFramework", listOf("NullFramework"))
                } version "1.1.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SKIPPED, it.task(":carthageCartfileResolve")?.outcome)
                }
    }

    @Test
    fun `task resolve will be skipped due to Cartfile-dot-resolve exists and update = false`() {
        val project = ProjectBuilder().withProjectDir(temporaryFolder.root).build()

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
                github("NullFramework", "yunarta/NullFramework") { rome ->
                    rome.map("NullFramework", listOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageCartfileResolve")?.outcome)
                }

        with(project.file("${project.buildDir}/works-swift/carthage/latest/Cartfile.resolved")) {
            org.junit.Assert.assertTrue(exists())
            delete()
        }

        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            rome {
                enabled = false
            }

            carthage {
                github("NullFramework", "yunarta/NullFramework") { rome ->
                    rome.map("NullFramework", listOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageCartfileResolve")
                .build().let {
                    assertEquals(TaskOutcome.SKIPPED, it.task(":carthageCartfileResolve")?.outcome)
                }
    }
}