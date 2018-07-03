package com.mobilesolutionworks.gradle.swift.model

import com.mobilesolutionworks.gradle.util.StringUtils

@Suppress("EnumEntryName")
enum class Platform {
    iOS,
    macOS,
    tvOS,
    watchOS,
}

internal object PlatformParser {

    private val mapping = Platform.values().associateBy { StringUtils.lowerCase(it.name) }

    fun matches(input: Set<String>): Set<Platform> {
        return input.map {
            StringUtils.lowerCase(it)
        }.let { list ->
            mapping.keys.intersect(list).map {
                mapping.getValue(it)
            }
        }.toSet()
    }
}