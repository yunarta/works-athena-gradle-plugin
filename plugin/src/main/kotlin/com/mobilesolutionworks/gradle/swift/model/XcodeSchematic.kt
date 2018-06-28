package com.mobilesolutionworks.gradle.swift.model

import com.mobilesolutionworks.gradle.swift.cocoa.Platform
import com.mobilesolutionworks.gradle.swift.cocoa.PlatformParser
import org.gradle.api.Project

open class XcodeSchematic {

    internal var declaredPlatforms: Collection<Platform> = Platform.values().toList()

    var platforms: List<String> = listOf("iOS", "macOS", "tvOS", "watchOS")
        set(value) {
            declaredPlatforms = PlatformParser.matches(value)
            if (declaredPlatforms.isEmpty()) {
                throw IllegalStateException("Failure in xcode.platforms, at least one of available platforms must be provided")
            }

            println("declaredPlatforms $declaredPlatforms")
            field = declaredPlatforms.map {
                it.name
            }
        }

    internal val platformsAsText: String
        get() = declaredPlatforms.joinToString(",") { it.name }
}

val Project.xcode: XcodeSchematic
    get() {
        return extensions.getByType(XcodeSchematic::class.java)
    }
