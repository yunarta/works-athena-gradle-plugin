package com.mobilesolutionworks.gradle.swift.tasks.athena

import junit5.assertMany
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test Athena schematic")
class AthenaSchematicTests {

    @Test
    @DisplayName("verify toolchain org.swift.4020170919a")
    @EnabledIf(value = arrayOf(
            "def out = new StringBuilder(), err = new StringBuilder()",
            "def proc = 'swift -version'.execute(['TOOLCHAINS=org.swift.4020170919a'], new File('.'))",
            "proc.consumeProcessOutput(out, err)",
            "proc.waitForOrKill(1000)",
            "def token = out.toString() =~ /Apple Swift version (.*) \\((.*)\\)/",
            "token[0][1] == '4.1.2'"
    ), engine = "groovy")
    fun test1(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            xcode {
                platforms = setOf("iOS")
                swiftToolchain = "org.swift.4020170919a"
            }

            athena {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework")
            }
        """.trimIndent())

        runner.withArguments("athenaCheck")
                .build().let {
                    assertMany {
                        isTrue {
                            it.output.contains("4.1.2")
                        }
                    }
                }
    }

    @Test
    @DisplayName("verify toolchain org.swift.4020170919")
    @EnabledIf(value = arrayOf(
            "def out = new StringBuilder(), err = new StringBuilder()",
            "def proc = 'swift -version'.execute(['TOOLCHAINS=org.swift.4020170919'], new File('.'))",
            "proc.consumeProcessOutput(out, err)",
            "proc.waitForOrKill(1000)",
            "def token = out.toString() =~ /Apple Swift version (.*) \\((.*)\\)/",
            "token[0][1] == '4.0'"
    ), engine = "groovy")
    fun test2(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
            }

            xcode {
                platforms = setOf("iOS")
                swiftToolchain = "org.swift.4020170919"
            }

            athena {
                enabled = true
            }

            carthage {
                github("yunarta/NullFramework")
            }
        """.trimIndent())

        runner.withArguments("athenaCheck")
                .build().let {
                    assertMany {
                        isTrue {
                            it.output.contains("4.0")
                        }
                    }
                }
    }

    @Test
    @DisplayName("verify toolchain")
    fun test3(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            import java.net.URI

            plugins {
                id("com.mobilesolutionworks.gradle.athena")
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

        runner.withArguments("athenaCheck")
                .build().let { result ->
                    result.output.contains(swiftVersion())
                }
    }
}