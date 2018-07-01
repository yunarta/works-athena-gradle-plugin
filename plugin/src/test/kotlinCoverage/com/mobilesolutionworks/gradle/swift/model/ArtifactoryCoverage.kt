package com.mobilesolutionworks.gradle.swift.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Coverage for XcodeSchematic")
class ArtifactoryCoverage {

    @Test
    @DisplayName("fulfill coverage")
    fun fulfillCoverage() {
        val spec = Artifactory.FileSpecs.FileSpec("pattern")
        val files = listOf(spec)
        val fileSpecs = Artifactory.FileSpecs(files)

        assertEquals(files, fileSpecs.files)
        assertEquals("pattern", spec.pattern)

        val result = Artifactory.ResultSpec("path")
        assertEquals("path", result.path)
    }
}