package com.mobilesolutionworks.gradle.swift.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Coverage for CarthageBuildInfo")
class CarthageBuildInfoCoverage {

    @Test
    @DisplayName("fulfill coverage")
    fun fulfillCoverage() {
        val info = CarthageBuildInfo("name", "hash")
        assertEquals("name", info.name)
        assertEquals("hash", info.hash)
    }
}