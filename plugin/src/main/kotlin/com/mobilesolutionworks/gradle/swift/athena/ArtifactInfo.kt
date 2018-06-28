package com.mobilesolutionworks.gradle.swift.athena

import com.mobilesolutionworks.gradle.swift.cocoa.Platform

class ArtifactInfo(
        val organization: String,
        val module: String,
        val framework: String,
        val version: String,
        val platform: Platform
)