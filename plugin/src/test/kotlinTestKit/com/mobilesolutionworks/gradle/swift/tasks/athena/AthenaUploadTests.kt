package com.mobilesolutionworks.gradle.swift.tasks.athena

import junit5.assertMany
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.parallel.ResourceLock
import testKit.GradleRunnerProvider
import testKit.cleanMavenLocalForTest
import testKit.newFile
import testKit.root
import java.io.File

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test AthenaUpload")
class AthenaUploadTests {

    @Test
    @DisplayName("verify athenaUpload to artifactory")
    fun test1(runner: GradleRunner) {
        createMockJfrog(runner)

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI
            import com.mobilesolutionworks.gradle.swift.model.extension.AthenaUploadTarget

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            val enableRepository: String? by project.extra
            enableRepository?.let {
                repositories {
                    maven {
                        url = URI("https://raw.githubusercontent.com/yunarta/works-athena-gradle-plugin/mock-repo")
                    }
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
                "athenaUpload",
                "--server-id=athena")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaUpload")?.outcome
                    }
                }
        runner.withArguments("carthageBootstrap",
                "athenaUpload", "--upload-dry-run", "-PenableRepository")
                .build().let {
                    assertMany {
                        TaskOutcome.SKIPPED expectedFrom it.task(":athenaUpload")?.outcome
                    }
                }
        runner.withArguments("carthageBootstrap",
                "athenaUpload", "--upload-dry-run", "--force-upload", "-PenableRepository")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaUpload")?.outcome
                    }
                }
    }

    @Test
    @DisplayName("verify athenaUpload to bintray")
    fun test2(runner: GradleRunner) {
        createMockJfrog(runner)

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI
            import com.mobilesolutionworks.gradle.swift.model.extension.AthenaUploadTarget

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            val enableRepository: String? by project.extra
            enableRepository?.let {
                repositories {
                    maven {
                        url = URI("https://raw.githubusercontent.com/yunarta/works-athena-gradle-plugin/mock-repo")
                    }
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
                "athenaUpload")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaUpload")?.outcome
                    }
                }
        runner.withArguments("carthageBootstrap",
                "athenaUpload", "--upload-dry-run", "-PenableRepository")
                .build().let {
                    assertMany {
                        TaskOutcome.SKIPPED expectedFrom it.task(":athenaUpload")?.outcome
                    }
                }
        runner.withArguments("carthageBootstrap",
                "athenaUpload", "--upload-dry-run", "--force-upload", "-PenableRepository")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaUpload")?.outcome
                    }
                }
    }

    @Test
    @DisplayName("verify athenaUpload to mavenLocal")
    @ResourceLock(value = "mavenLocal")
    fun test3(runner: GradleRunner) {
        cleanMavenLocalForTest()

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
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaUpload")?.outcome
                    }
                }
        runner.withArguments("carthageBootstrap",
                "athenaUpload")
                .build().let {
                    assertMany {
                        TaskOutcome.SKIPPED expectedFrom it.task(":athenaUpload")?.outcome
                    }
                }
        runner.withArguments("carthageBootstrap",
                "athenaUpload", "--force-upload")
                .build().let {
                    assertMany {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaUpload")?.outcome
                    }
                }
    }

    private fun createMockJfrog(runner: GradleRunner) {
        File(runner.root, "Athena/jfrog").apply {
            parentFile.mkdirs()
            writeText("""
                #!/bin/bash
                echo ${'$'}@
            """.trimIndent())
            setExecutable(true)
        }

        runner.newFile("gradle.properties").writeText("""
                jfrogExecutable=./jfrog
            """.trimIndent())
    }
}