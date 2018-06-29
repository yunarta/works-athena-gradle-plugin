package com.mobilesolutionworks.gradle.swift.tasks.xcode

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testKit.GradleRunnerProvider
import testKit.newFile

@ExtendWith(GradleRunnerProvider::class)
@DisplayName("Test XcodeSchematic")
class XcodeSchematicTests {

    @Test
    fun `gradle should fail if xcode platforms is empty`(runner: GradleRunner) {
        runner.newFile("settings.gradle.kts").writeText("""
        """.trimIndent())

        val build = runner.newFile("build.gradle.kts")
        build.writeText("""
            plugins {
                id("com.mobilesolutionworks.gradle.swift")
            }

            xcode {
                platforms = emptySet()
            }
        """.trimIndent())

        runner.withArguments("xcodeBuildInfo").buildAndFail()
    }
}