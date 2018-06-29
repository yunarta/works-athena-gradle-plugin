package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import testKit.DefaultGradleRunner
import testKit.TestWithCoverage

class AthenaInspectCarthageTests {

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

            xcode {
                platforms = setOf("iOS")
            }

            athena {
                enabled = true
//                resolutions {
//                    "https://bitbucket.org/yunarta/nullframework.git" {
//                        group = "group"
//                        module = "module"
//                    }
//                }
            }

            carthage {
                git("https://bitbucket.org/yunarta/nullframework.git") {
                }
            }
        """.trimIndent())

        gradle.runner.withArguments("athenaInspectCarthage", "--parallel", "--stacktrace", "--continue")
                .buildAndFail()
    }

    @Test
    fun `test athena resolutions`() {
        temporaryFolder.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = temporaryFolder.newFile("build.gradle.kts")
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
                        group = "yunarta"
                        module = "NullFramework"
                    }
                }
            }

            carthage {
                git("https://bitbucket.org/yunarta/nullframework.git") {
                }
            }
        """.trimIndent())

        gradle.runner.withArguments("athenaInspectCarthage", "--parallel", "--stacktrace", "--continue")
                .build()
    }
}