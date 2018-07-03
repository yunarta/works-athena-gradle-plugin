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
                "athenaUpload", "--force-upload", "--upload-dry-run")
        runner.withArguments("carthageBootstrap",
                "athenaUpload", "--upload-dry-run")
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
                "athenaUpload", "--force-upload", "--upload-dry-run")
                .build()
        runner.withArguments("carthageBootstrap",
                "athenaUpload", "--upload-dry-run")
                .build()
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
                "athenaUpload", "--force-upload")
                .build()
        runner.withArguments("carthageBootstrap",
                "athenaUpload")
                .build()
    }
}