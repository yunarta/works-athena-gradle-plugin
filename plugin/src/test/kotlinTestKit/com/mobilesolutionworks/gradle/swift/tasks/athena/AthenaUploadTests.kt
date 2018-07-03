package com.mobilesolutionworks.gradle.swift.tasks.athena

import junit5.assertMany
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test AthenaUpload")
class AthenaUploadTests {

    @Test
    @DisplayName("verify athenaUpload to artifactory")
    @EnabledIfEnvironmentVariable(named = "NODE_NAME", matches = "works|aux")
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
                mavenLocal()
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
                "athenaUpload")
                .build().let {
                    // Assert.assertEquals(TaskOutcome.SUCCESS, it.task(":romeUpload")?.outcome)
                }
    }

    @Test
    @DisplayName("verify athenaUpload to bintray")
    @EnabledIfEnvironmentVariable(named = "NODE_NAME", matches = "works|aux")
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
                mavenLocal()
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

    @Test
    @DisplayName("verify athenaUpload to mavenLocal")
    fun test3(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI
            import com.mobilesolutionworks.gradle.swift.model.extension.AthenaUploadTarget

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            xcode {
                platforms = setOf("iOS")
            }

            athena {
                upload = AthenaUploadTarget.MavenLocal

                organization = "mobilesolutionworks"
                repository = "athena"

                enabled = true
            }

            carthage {
                github("yunarta/NullFramework") version "1.0.0"
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap",
                "athenaUpload")
                .build()
    }
}