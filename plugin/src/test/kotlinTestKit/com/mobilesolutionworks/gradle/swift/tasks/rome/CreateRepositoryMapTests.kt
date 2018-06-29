package com.mobilesolutionworks.gradle.swift.tasks.rome

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import testKit.DefaultGradleRunner
import testKit.TestWithCoverage

class CreateRepositoryMapTests {

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
            }

            carthage {
                github("yunarta/NullFramework") { options ->
                    options.map("NullFramework", setOf("NullFramework"))
                } version "1.0.0"
            }
        """.trimIndent())

        gradle.runner.withArguments("romeCreateRepositoryMap")
                .build().let {
                    Assert.assertEquals(TaskOutcome.SUCCESS, it.task(":romeCreateRepositoryMap")?.outcome)

                    val mapFile = project.file("${project.buildDir}/works-swift/rome/map/NullFramework.txt")
                    Assert.assertEquals("""
                        NullFramework = NullFramework
                    """.trimIndent(), mapFile.readText())
                }
    }
}