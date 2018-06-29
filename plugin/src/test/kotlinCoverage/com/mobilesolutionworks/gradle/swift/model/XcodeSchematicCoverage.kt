package com.mobilesolutionworks.gradle.swift.model

import org.junit.Assert.assertEquals
import org.junit.Test

class XcodeSchematicCoverage {

    @Test
    fun fulfillCoverage() {
        val schematic = XcodeSchematic()
        assertEquals(schematic.platforms, setOf("iOS", "macOS", "tvOS", "watchOS"))
    }
}