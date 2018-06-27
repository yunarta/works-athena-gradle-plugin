package com.mobilesolutionworks.gradle.swift.model

import org.gradle.api.Project

enum class Platform(val value: String) {
    ios("iOS"),
    macos("macOS"),
    tvos("tvOS"),
    watchos("watchOS")
}

open class XcodeSchematic {

    var platforms: List<String> = listOf("ios")
        set(value) {
            val lowerCaseValues = value.map { it.toLowerCase() }
            field = Platform.values().filter {
                lowerCaseValues.contains(it.name)
            }.map {
                it.value
            }
        }

    internal val hasDeclaredPlatforms: Boolean
        get() = platforms.isNotEmpty()

    internal val declaredPlatforms: String
        get() = platforms.joinToString(",")
}

val Project.xcode: XcodeSchematic
    get() {
        return extensions.findByName("xcode") as XcodeSchematic
    }
