package com.mobilesolutionworks.gradle.swift.model

import junit.framework.Assert.assertEquals
import org.junit.Test

class XcodeSchematicCoverage {

    @Test
    fun fulfillCoverage() {
        val schematic = XcodeSchematic()
        assertEquals(schematic.platforms, listOf("ios", "macOS", "tvOS", "watchOS"))
    }
}