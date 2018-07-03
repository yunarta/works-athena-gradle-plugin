package com.mobilesolutionworks.gradle.swift.tasks.athena

import junit5.assertMany
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile
import testKit.root

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test AthenaCreatePackage")
class AthenaCreatePackageTests {

    @Test
    @DisplayName("verify athenaCreatePackage")
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

            carthage {
                github("yunarta/NullFramework") version "1.1.0"
            }
        """.trimIndent())

        runner.withArguments("athenaCreatePackage")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaCreatePackage")?.outcome

                        val version = swiftVersion()
                        val project = ProjectBuilder().withProjectDir(runner.root).build()
                        val path = "Athena/yunarta/NullFramework/1.1.0-Swift${version}"

                        isTrue {
                            project.file("$path/NullFramework-1.1.0-Swift${version}.zip").exists()
                        }

                        isTrue {
                            project.file("$path/NullFramework-1.1.0-Swift${version}.pom").exists()
                        }
                    }
                }
    }
}