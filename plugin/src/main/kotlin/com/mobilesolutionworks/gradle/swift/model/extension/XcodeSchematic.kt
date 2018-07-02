package com.mobilesolutionworks.gradle.swift.model.extension

import com.mobilesolutionworks.gradle.swift.model.Platform
import com.mobilesolutionworks.gradle.swift.model.PlatformParser

open class XcodeSchematic {

    private var declaredPlatforms: Set<Platform> = Platform.values().toSet()

    var platforms: Set<String> = setOf("iOS", "macOS", "tvOS", "watchOS")
        set(value) {
            declaredPlatforms = PlatformParser.matches(value)
            if (declaredPlatforms.isEmpty()) {
                throw IllegalStateException("Failure in xcode.platforms, at least one of available platforms must be provided")
            }

            field = declaredPlatforms.map {
                it.name
            }.toSet()
        }

    internal val platformsAsText: String
        get() = declaredPlatforms.joinToString(",") { it.name }
}

