package com.mobilesolutionworks.gradle.swift.tasks.athena

import junit5.assertAll
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.root

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test AthenaListMissing")
class AthenaListMissingTests {

    @Test
    @DisplayName("verify athenaListMissing")
    fun test1(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
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

        runner.withArguments("athenaListMissing")
                .build().let {
                    val project = ProjectBuilder().withProjectDir(runner.root).build()
                    val readText = project.file("${project.buildDir}/works-swift/athena/missing.txt").readText()
                    println(readText)

                    assertAll {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaListMissing")?.outcome

                        isTrue {
                            readText.contains("yunarta:NullFramework")
                        }
                    }
                }
    }

    @Test
    @DisplayName("test incremental build")
    fun test2(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            repositories {
            }

            xcode {
                platforms = setOf("iOS")
            }

            athena {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework")
                github("ReactiveX/RxSwift")
            }
        """.trimIndent())

        runner.withArguments("athenaListMissing")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":athenaListMissing")?.outcome)
                }
        runner.withArguments("athenaListMissing")
                .build().let {
                    assertEquals(TaskOutcome.UP_TO_DATE, it.task(":athenaListMissing")?.outcome)
                }
    }
}