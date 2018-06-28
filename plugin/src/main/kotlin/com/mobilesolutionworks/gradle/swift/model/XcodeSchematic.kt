package com.mobilesolutionworks.gradle.swift.model

import com.mobilesolutionworks.gradle.util.StringUtil
import org.gradle.api.Project

enum class Platform(val value: String) {
    ios("iOS"),
    macos("macOS"),
    tvos("tvOS"),
    watchos("watchOS")
}

open class XcodeSchematic {

    var platforms: List<String> = listOf("ios", "macOS", "tvOS", "watchOS")
        set(value) {
            val lowerCaseValues = value.map { StringUtil.lowerCase(it) }
            val platforms = Platform.values().filter {
                lowerCaseValues.contains(it.name)
            }.map {
                it.value
            }

            if (platforms.isEmpty()) {
                throw IllegalStateException("Failure in xcode.platforms, at least one of available platforms must be provided")
            }

            field = platforms
        }

    internal val declaredPlatforms: String
        get() = platforms.joinToString(",")
}

val Project.xcode: XcodeSchematic
    get() {
        return extensions.getByType(XcodeSchematic::class.java)
    }
