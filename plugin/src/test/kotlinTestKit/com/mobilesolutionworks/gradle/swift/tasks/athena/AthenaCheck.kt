package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile

@ExtendWith(GradleRunnerProvider::class)
class AthenaCheck {

    @Test
    @DisplayName("verify athena schematic")
    fun test1(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            xcode {
                platforms = setOf("iOS")
            }

            athena {
                enabled = true
                resolutions {
                    "https://bitbucket.org/yunarta/nullframework.git" {
                        group = "group"
                        module = "module"
                    }
                }
            }

            carthage {
                github("yunarta/NullFramework")
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap")
                .build().let {
                    // Assert.assertEquals(TaskOutcome.SUCCESS, it.task(":romeUpload")?.outcome)
                }
    }
}