package com.mobilesolutionworks.gradle.swift.model

import org.junit.Test

class FrameworkOptionsCoverage {

    @Test
    fun fulfillCoverage() {
        val options = FrameworkOptions()
        options.key = "key"
        options.frameworks = setOf()
    }
}