package com.mobilesolutionworks.gradle.swift.tasks.athena

import junit5.assertMany
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.cleanMavenLocalForTest
import testKit.newFile
import testKit.root
import java.io.File

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test AthenaDownload")
class AthenaDownloadTests {

    @Test
    @DisplayName("verify athenaDownload")
    fun test1(runner: GradleRunner) {
        cleanMavenLocalForTest()

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            repositories {
                mavenLocal()
            }

            xcode {
                platforms = setOf("iOS")
            }

            athena {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework")
            }
        """.trimIndent())

        runner.withArguments("athenaDownload")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":athenaDownload")?.outcome)
                }

        runner.withArguments("athenaDownload")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":athenaDownload")?.outcome)
                }
    }

    @Test
    @DisplayName("verify athenaDownload with alternate directory")
    fun test3(runner: GradleRunner) {
        cleanMavenLocalForTest()

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            repositories {
                mavenLocal()
            }

            xcode {
                platforms = setOf("iOS")
            }

            athena {
                enabled = true
            }

            carthage {
                destination = project.file("Olympus")

                github("yunarta/NullFramework")
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap", "athenaUpload")
                .build()
        runner.withArguments("athenaDownload")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaDownload")?.outcome

                        isTrue {
                            File(runner.root, "Olympus/Carthage/Build/iOS/NullFramework.framework").exists()
                        }
                    }
                }
    }

    @Test
    @DisplayName("verify athenaDownload from cache")
    fun test2(runner: GradleRunner) {
        cleanMavenLocalForTest()

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            repositories {
                mavenLocal()
            }

            xcode {
                platforms = setOf("iOS")
            }

            athena {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework")
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap", "athenaUpload")
                .build()
        runner.withArguments("athenaDownload")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaDownload")?.outcome
                    }
                }
    }
}