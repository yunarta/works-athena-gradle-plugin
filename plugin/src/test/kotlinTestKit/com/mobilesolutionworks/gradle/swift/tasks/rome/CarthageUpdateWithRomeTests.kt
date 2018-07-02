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
import testKit.range
import testKit.root
import java.io.File

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test carthageUpdate with Rome")
class CarthageUpdateWithRomeTests {

    @Test
    @DisplayName("verify carthageUpdate")
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

        runner.withArguments("carthageUpdate")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom  it.task(":romeDownload")?.outcome
                        TaskOutcome.SUCCESS expectedFrom  it.task(":carthageUpdate")?.outcome
                    }
                }
    }

    @Test
    @DisplayName("verify carthageUpdate incremental build")
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

        runner.withArguments("carthageUpdate", "romeUpload")
                .build()

        runner.withArguments("carthageUpdate", "-i")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom  it.task(":romeListMissing")?.outcome
                        TaskOutcome.UP_TO_DATE expectedFrom  it.task(":romeDownload")?.outcome
                        TaskOutcome.UP_TO_DATE expectedFrom  it.task(":carthageUpdate")?.outcome
                    }
                }

        runner.withArguments("carthageUpdate")
                .build().let {
                    assertMany {
                        TaskOutcome.UP_TO_DATE expectedFrom  it.task(":romeListMissing")?.outcome
                        TaskOutcome.UP_TO_DATE expectedFrom  it.task(":romeDownload")?.outcome
                        TaskOutcome.UP_TO_DATE expectedFrom  it.task(":carthageUpdate")?.outcome
                    }
                }
    }

    @Test
    @DisplayName("verify after romeUpload, clean, carthageUpdate runs again")
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

        runner.withArguments("carthageUpdate", "romeUpload")
                .build()

        File(runner.root, "Carthage").deleteRecursively()
        File(runner.root, "build").deleteRecursively()

        runner.withArguments("carthageUpdate")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom  it.task(":carthageUpdate")?.outcome
                    }
                }
    }
}