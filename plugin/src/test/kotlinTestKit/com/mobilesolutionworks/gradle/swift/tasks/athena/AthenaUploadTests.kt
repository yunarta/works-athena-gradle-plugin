package com.mobilesolutionworks.gradle.swift.tasks.athena

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test AthenaUpload")
@EnabledIfEnvironmentVariable(named = "NODE_NAME", matches = "works")
class AthenaUploadTests {

    @Test
    @DisplayName("verify athenaUpload to artifactory")
    fun test1(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI
            import com.mobilesolutionworks.gradle.swift.model.extension.AthenaUploadTarget

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
                upload = AthenaUploadTarget.Artifactory
                repository = "athena"

                enabled = true
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap",
                "athenaUpload", "--upload-dry-run", "--stacktrace")
                .build().let {
                    // Assert.assertEquals(TaskOutcome.SUCCESS, it.task(":romeUpload")?.outcome)
                }
    }

    @Test
    @DisplayName("verify athenaUpload to bintray")
    fun test2(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI
            import com.mobilesolutionworks.gradle.swift.model.extension.AthenaUploadTarget

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
                upload = AthenaUploadTarget.Bintray

                organization = "mobilesolutionworks"
                repository = "athena"

                enabled = true
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap",
                "athenaUpload", "--upload-dry-run", "--stacktrace")
                .build().let {
                    // Assert.assertEquals(TaskOutcome.SUCCESS, it.task(":romeUpload")?.outcome)
                }
    }

}