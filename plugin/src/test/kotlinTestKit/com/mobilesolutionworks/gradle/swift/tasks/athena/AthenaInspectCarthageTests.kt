package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import testKit.GradleRunnerProvider
import testKit.newFile


@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test AthenaInspectCarthage")
@ResourceLock(value = "xcode", mode = ResourceAccessMode.READ_WRITE)
class AthenaInspectCarthageTests {

    @Test
    @DisplayName("verify athenaInspectCarthage")
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
            }

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                github("ReactiveX/RxSwift")
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap", "athenaInspectCarthage", "-x", "athenaUpload", "--parallel", "--stacktrace", "--continue")
                .build()
    }

    @Test
    @DisplayName("test unresolved github")
    fun test2(runner: GradleRunner) {
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
            }

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                git("https://github.com/yunarta/NullFramework.git") {
                }
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap", "athenaInspectCarthage", "-x", "athenaUpload", "--parallel", "--stacktrace", "--continue")
                .build()
    }

    @Test
    @DisplayName("test resolved github")
    fun test3(runner: GradleRunner) {
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

        runner.withArguments("carthageBootstrap", "athenaInspectCarthage", "-x", "athenaUpload", "--parallel", "--stacktrace", "--continue")
                .build()
    }

    @Test
    @DisplayName("test unresolved source")
    fun test4(runner: GradleRunner) {
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
            }

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                git("https://bitbucket.org/yunarta/nullframework.git") {
                }
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap", "athenaInspectCarthage", "-x", "athenaUpload", "--parallel", "--stacktrace", "--continue")
                .buildAndFail()
    }

    @Test
    @DisplayName("test athena resolutions")
    fun test5(runner: GradleRunner) {
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

        runner.withArguments("carthageBootstrap", "athenaInspectCarthage", "-x", "athenaUpload", "--parallel", "--stacktrace", "--continue")
                .build()
    }
}