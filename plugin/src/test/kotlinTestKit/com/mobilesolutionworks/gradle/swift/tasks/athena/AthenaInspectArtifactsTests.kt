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
@DisplayName("Test AthenaInspectArtifacts")
class AthenaInspectArtifactsTests {

    @Test
    @DisplayName("verify athenaInspectArtifacts")
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

        runner.withArguments("athenaInspectArtifacts")
                .build()
                .let {
                    val project = ProjectBuilder().withProjectDir(runner.root).build()
                    val file = project.file("${project.buildDir}/works-swift/athena/artifacts.json")

                    val element = JsonParser().parse(file.reader())
                    val uploadInfo = element.asJsonArray[0].asJsonObject

                    assertAll {
                        val version = uploadInfo["version"].asJsonObject

                        "1.0.0" expectedFrom version["version"].asString
                        "yunarta" expectedFrom version["group"].asString
                        "NullFramework" expectedFrom version["module"].asString

                        "4.1.2" expectedFrom uploadInfo["swiftVersion"].asString

                        val frameworks = uploadInfo["frameworks"].asJsonObject
                        "NullFramework" expectedFrom frameworks["iOS"].asJsonArray[0].asJsonObject["name"].asString
                    }
                }
    }

    @Test
    @DisplayName("verify athenaInspectArtifacts with alternate directory")
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
                destination = project.file("Olympus")

                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("athenaInspectArtifacts")
                .build()
                .let {
                    val project = ProjectBuilder().withProjectDir(runner.root).build()
                    val file = project.file("${project.buildDir}/works-swift/athena/artifacts.json")

                    val element = JsonParser().parse(file.reader())
                    val uploadInfo = element.asJsonArray[0].asJsonObject

                    assertAll {
                        val version = uploadInfo["version"].asJsonObject

                        "1.0.0" expectedFrom version["version"].asString
                        "yunarta" expectedFrom version["group"].asString
                        "NullFramework" expectedFrom version["module"].asString

                        "4.1.2" expectedFrom uploadInfo["swiftVersion"].asString

                        val frameworks = uploadInfo["frameworks"].asJsonObject
                        "NullFramework" expectedFrom frameworks["iOS"].asJsonArray[0].asJsonObject["name"].asString
                    }
                }
    }
}