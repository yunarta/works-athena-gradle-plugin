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
            }

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                github("ReactiveX/RxSwift")
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap", "athenaInspectCarthage", "--parallel", "--stacktrace", "--continue")
                .build()
    }

    @Test
    fun `test unresolved github`() {
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
            }

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                git("https://github.com/yunarta/NullFramework.git") {
                }
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap", "athenaInspectCarthage", "--parallel", "--stacktrace", "--continue")
                .build()
    }

    @Test
    fun `test resolved git`() {
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
            }

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                git("https://bitbucket.org/yunarta/nullframework.git") {
                    id("yunarta", "NullFramework")
                }
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap", "athenaInspectCarthage", "--parallel", "--stacktrace", "--continue")
                .build()
    }

    @Test
    fun `test unresolved source`() {
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
            }

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                git("https://bitbucket.org/yunarta/nullframework.git") {
                }
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap", "athenaInspectCarthage", "--parallel", "--stacktrace", "--continue")
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

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                git("https://bitbucket.org/yunarta/nullframework.git") {
                }
            }
        """.trimIndent())

        gradle.runner.withArguments("carthageBootstrap", "athenaInspectCarthage", "--parallel", "--stacktrace", "--continue")
                .build()
    }
}