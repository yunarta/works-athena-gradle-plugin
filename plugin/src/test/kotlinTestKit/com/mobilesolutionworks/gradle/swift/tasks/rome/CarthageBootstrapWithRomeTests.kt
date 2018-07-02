package com.mobilesolutionworks.gradle.swift.tasks.rome

import junit5.assertMany
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.root
import java.io.File

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test carthageBootstrap with Rome")
class CarthageBootstrapWithRomeTests {

    @Test
    @DisplayName("verify carthageBootstrap")
    fun execution(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            xcode {
                platforms = setOf("iOS")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap", "romeUpload")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom  it.task(":romeDownload")?.outcome
                        TaskOutcome.SUCCESS expectedFrom  it.task(":carthageBootstrap")?.outcome
                    }
                }
    }

    @Test
    @DisplayName("verify carthageBootstrap incremental build")
    fun test2(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            xcode {
                platforms = setOf("iOS")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap", "romeUpload")
                .build()

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom  it.task(":romeListMissing")?.outcome
                        TaskOutcome.UP_TO_DATE expectedFrom  it.task(":romeDownload")?.outcome
                        TaskOutcome.UP_TO_DATE expectedFrom  it.task(":carthageBootstrap")?.outcome
                    }
                }

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assertMany {
                        TaskOutcome.UP_TO_DATE expectedFrom  it.task(":romeListMissing")?.outcome
                        TaskOutcome.UP_TO_DATE expectedFrom  it.task(":romeDownload")?.outcome
                        TaskOutcome.UP_TO_DATE expectedFrom  it.task(":carthageBootstrap")?.outcome
                    }
                }
    }

    @Test
    @DisplayName("verify after romeUpload, clean, carthageBootstrap runs again")
    fun test3(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            xcode {
                platforms = setOf("iOS")
            }

            rome {
                enabled = true
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap", "romeUpload")
                .build()

        File(runner.root, "Carthage").deleteRecursively()
        File(runner.root, "build").deleteRecursively()

        runner.withArguments("carthageBootstrap")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom  it.task(":carthageBootstrap")?.outcome
                    }
                }
    }
}