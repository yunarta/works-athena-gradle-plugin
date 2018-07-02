package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.google.gson.JsonParser
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
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
                    assertAll(
                            { assertEquals("yunarta", packages["group"].asString) },
                            { assertEquals("NullFramework", packages["module"].asString) },
                            { assertEquals("1.0.0", packages["version"].asString) }
                    )
                }
    }

    @Test
    @DisplayName("test unresolved github")
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
                    assertAll(
                            { assertEquals("yunarta", packages["group"].asString) },
                            { assertEquals("NullFramework", packages["module"].asString) },
                            { assertEquals("1.0.0", packages["version"].asString) }
                    )
                }
    }

    @Test
    @DisplayName("test resolved github")
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
                    assertAll(
                            { assertEquals("yunarta", packages["group"].asString) },
                            { assertEquals("NullFramework", packages["module"].asString) },
                            { assertEquals("1.0.0", packages["version"].asString) }
                    )
                }
    }

    @Test
    @DisplayName("test unresolved source")
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
    @DisplayName("test athena resolutions")
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
                maven {
                    url = URI("http://repo.dogeza.club:18090/artifactory/list/athena")
                }
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
                    assertAll(
                            { assertEquals("yunarta", packages["group"].asString) },
                            { assertEquals("NullFramework", packages["module"].asString) },
                            { assertEquals("1.0.0", packages["version"].asString) }
                    )
                }
    }
}