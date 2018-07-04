package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.JsonParser
import junit5.assertAll
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.root

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test AthenaInspectCarthage")
class AthenaInspectCarthageTests {

    @Test
    @DisplayName("verify athenaInspectCarthage")
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
                mavenLocal()
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
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("athenaInspectCarthage")
                .build()
                .let {
                    val project = ProjectBuilder().withProjectDir(runner.root).build()
                    val file = project.file("${project.buildDir}/works-swift/athena/packages.json")
                    val element = JsonParser().parse(file.reader())
                    val packages = element.asJsonObject.getAsJsonObject("NullFramework")

                    assertAll {
                        "yunarta" expectedFrom packages["group"].asString
                        "NullFramework" expectedFrom packages["module"].asString
                        "1.0.0" expectedFrom packages["version"].asString
                    }
                }
    }

    @Test
    @DisplayName("verify athenaInspectCarthage with alternate directory")
    fun test6(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

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

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                destination = project.file("Olympus")

                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("athenaInspectCarthage")
                .build()
                .let {
                    val project = ProjectBuilder().withProjectDir(runner.root).build()
                    val file = project.file("${project.buildDir}/works-swift/athena/packages.json")
                    val element = JsonParser().parse(file.reader())
                    val packages = element.asJsonObject.getAsJsonObject("NullFramework")

                    assertAll {
                        "yunarta" expectedFrom packages["group"].asString
                        "NullFramework" expectedFrom packages["module"].asString
                        "1.0.0" expectedFrom packages["version"].asString
                    }
                }
    }

    @Test
    @DisplayName("verify athenaInspectCarthage with github as git source")
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
                mavenLocal()
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
                } version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("athenaInspectCarthage")
                .build()
                .let {
                    val project = ProjectBuilder().withProjectDir(runner.root).build()
                    val file = project.file("${project.buildDir}/works-swift/athena/packages.json")
                    val element = JsonParser().parse(file.reader())
                    val packages = element.asJsonObject.getAsJsonObject("NullFramework")

                    assertAll {
                        "yunarta" expectedFrom packages["group"].asString
                        "NullFramework" expectedFrom packages["module"].asString
                        "1.0.0" expectedFrom packages["version"].asString
                    }
                }
    }

    @Test
    @DisplayName("verify athenaInspectCarthage with git source with component info")
    fun test3(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

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

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                git("https://bitbucket.org/yunarta/nullframework.git") {
                    id("yunarta", "NullFramework")
                } version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("athenaInspectCarthage")
                .build()
                .let {
                    val project = ProjectBuilder().withProjectDir(runner.root).build()
                    val file = project.file("${project.buildDir}/works-swift/athena/packages.json")
                    val element = JsonParser().parse(file.reader())
                    val packages = element.asJsonObject.getAsJsonObject("NullFramework")

                    assertAll {
                        "yunarta" expectedFrom packages["group"].asString
                        "NullFramework" expectedFrom packages["module"].asString
                        "1.0.0" expectedFrom packages["version"].asString
                    }
                }
    }

    @Test
    @DisplayName("verify athenaInspectCarthage with git source without component info")
    fun test4(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

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

            rome {
                cachePath = file("${"$"}{project.rootDir}/romeCache")
            }

            carthage {
                git("https://bitbucket.org/yunarta/nullframework.git") {
                } version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("athenaInspectCarthage")
                .buildAndFail()
    }

    @Test
    @DisplayName("verify athenaInspectCarthage with git source with athena resolutions")
    fun test5(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

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
                } version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("athenaInspectCarthage")
                .build()
                .let {
                    val project = ProjectBuilder().withProjectDir(runner.root).build()
                    val file = project.file("${project.buildDir}/works-swift/athena/packages.json")
                    val element = JsonParser().parse(file.reader())
                    val packages = element.asJsonObject.getAsJsonObject("NullFramework")

                    assertAll {
                        "yunarta" expectedFrom packages["group"].asString
                        "NullFramework" expectedFrom packages["module"].asString
                        "1.0.0" expectedFrom packages["version"].asString
                    }
                }
    }
}