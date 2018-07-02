package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test carthageBootstrap with Athena")
class CarthageBootstrapWithAthenaTests {

    @Test
    @DisplayName("verify athenaDownload")
    fun test1(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            repositories {
                maven {
                    url = URI("http://repo.dogeza.club:18090/artifactory/list/athena")
                }
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

        runner.withArguments("carthageBootstrap", "-x", "athenaUpload")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageBootstrap")?.outcome)
                }
    }

    @Test
    @DisplayName("verify athenaDownload with missing artifact")
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
                maven {
                    url = URI("http://repo.dogeza.club:18090/artifactory/list/athena")
                }
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

        runner.withArguments("carthageBootstrap", "-x", "athenaUpload")
                .build().let {
                    assertEquals(TaskOutcome.SUCCESS, it.task(":carthageBootstrap")?.outcome)
                }
    }
}