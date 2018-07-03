package com.mobilesolutionworks.gradle.swift.tasks.athena

import junit5.assertAll
import junit5.assertMany
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.parallel.ResourceLock
import testKit.GradleRunnerProvider
import testKit.cleanMavenLocalForTest
import testKit.newFile
import testKit.root
import java.io.File

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test AthenaListMissing")
class AthenaListMissingTests {

    @Test
    @DisplayName("verify athenaListMissing")
    @ResourceLock(value = "mavenLocal")
    fun test1(runner: GradleRunner) {
        cleanMavenLocalForTest()

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
                enabled = true
                upload = AthenaUploadTarget.Artifactory
            }

            carthage {
                github("yunarta/NullFramework")
            }
        """.trimIndent())

        runner.withArguments("athenaListMissing")
                .build().let {
                    val project = ProjectBuilder().withProjectDir(runner.root).build()
                    val readText = project.file("${project.buildDir}/works-swift/athena/missing.txt").readText()

                    assertAll {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaListMissing")?.outcome

                        isTrue {
                            readText.contains("yunarta:NullFramework")
                        }
                    }
                }
    }

    @Test
    @DisplayName("verify athenaListMissing incremental build")
    @ResourceLock(value = "mavenLocal")
    fun test2(runner: GradleRunner) {
        cleanMavenLocalForTest()

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
                github("yunarta/NullFramework")
            }
        """.trimIndent())

        runner.withArguments("carthageBootstrap", "athenaUpload")
                .build().let {
                    val project = ProjectBuilder().withProjectDir(runner.root).build()
                    val readText = project.file("${project.buildDir}/works-swift/athena/missing.txt").readText()

                    assertAll {
                        TaskOutcome.SUCCESS expectedFrom it.task(":athenaListMissing")?.outcome

                        isTrue {
                            readText.contains("yunarta:NullFramework")
                        }
                    }
                }

        runner.withArguments("athenaListMissing")
                .build().let {
                    assertMany {
                        val project = ProjectBuilder().withProjectDir(runner.root).build()
                        val readText = project.file("${project.buildDir}/works-swift/athena/missing.txt").readText()

                        assertAll {
                            TaskOutcome.SUCCESS expectedFrom it.task(":athenaListMissing")?.outcome

                            isFalse {
                                readText.contains("yunarta:NullFramework")
                            }
                        }

                    }
                }
    }
}