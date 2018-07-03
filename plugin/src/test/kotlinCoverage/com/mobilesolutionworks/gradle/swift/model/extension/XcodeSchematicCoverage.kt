package com.mobilesolutionworks.gradle.swift.model.extension

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Coverage for XcodeSchematic")
class XcodeSchematicCoverage {

    @Test
    @DisplayName("fulfill coverage")
    fun fulfillCoverage() {
        val schematic = XcodeSchematic()
        schematic.platforms = setOf("iOS", "macOS", "tvOS", "watchOS")
        assertEquals(schematic.platforms, setOf("iOS", "macOS", "tvOS", "watchOS"))
    }
}