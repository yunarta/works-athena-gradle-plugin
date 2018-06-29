package com.mobilesolutionworks.gradle.swift.athena

import com.mobilesolutionworks.gradle.swift.cocoa.Platform

class Component (
    val group: String,
    val module: String
)

class ArtifactInfo(
        val framework: String,
        val version: String,
        val platform: Platform
)