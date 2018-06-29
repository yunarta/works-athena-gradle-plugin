package com.mobilesolutionworks.gradle.swift.model

import com.mobilesolutionworks.gradle.swift.cocoa.Platform
import org.junit.Assert.assertEquals
import org.junit.Test

class XcodeSchematicCoverage {

    @Test
    fun fulfillCoverage() {
        val schematic = XcodeSchematic()
        schematic.platforms = setOf("iOS", "macOS", "tvOS", "watchOS")
        assertEquals(schematic.platforms, setOf("iOS", "macOS", "tvOS", "watchOS"))
        assertEquals(schematic.declaredPlatforms, Platform.values().toSet())
    }
}