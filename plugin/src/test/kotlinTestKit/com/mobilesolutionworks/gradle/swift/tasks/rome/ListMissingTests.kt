package com.mobilesolutionworks.gradle.swift.tasks.rome

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import testKit.DefaultGradleRunner
import testKit.TestWithCoverage

class ListMissingTests {

    val temporaryFolder = TemporaryFolder()

    var gradle = DefaultGradleRunner(temporaryFolder)

    @JvmField
    @Rule
    val rule = RuleChain.outerRule(temporaryFolder)
            .around(TestWithCoverage(temporaryFolder))
            .around(gradle)

    @Test
    fun execution() {
        val project = ProjectBuilder().withProjectDir(temporaryFolder.root).build()

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

        gradle.runner.withArguments("romeListMissing", "--skipLocalCache")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeCreateRomefile")?.outcome)
                    val missing = project.file("${project.buildDir}/works-swift/rome/romefile/missing.txt")
                    assertEquals("NullFramework 1.0.0 : -iOS -Mac -tvOS -watchOS", missing.readText().trimMargin())
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

        gradle.runner.withArguments("romeListMissing", "--skipLocalCache")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeCreateRomefile")?.outcome)
                }

        gradle.runner.withArguments("romeListMissing", "--skipLocalCache")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":romeCreateRomefile")?.outcome)
                }
    }

    @Test
    fun `DSL modification will redo list missing`() {
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

        gradle.runner.withArguments("romeListMissing", "--skipLocalCache")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeListMissing")?.outcome)
                }

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
                } version "1.1.0"
            }
        """.trimIndent())


        gradle.runner.withArguments("romeListMissing", "--skipLocalCache", "-i")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":romeListMissing")?.outcome)
                }
    }
}