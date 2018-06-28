package com.mobilesolutionworks.gradle.swift.cocoa

import com.mobilesolutionworks.gradle.util.StringUtils

@Suppress("EnumEntryName")
enum class Platform {
    iOS,
    macOS,
    tvOS,
    watchOS,
}

object PlatformParser {

    private val mapping = Platform.values().associateBy { StringUtils.lowerCase(it.name) }

    fun matches(input: List<String>): Collection<Platform> {
        return input.map {
            StringUtils.lowerCase(it)
        }.let { list ->
            mapping.filterKeys {
                list.contains(it)
            }
        }.map {
            it.value
        }
    }
}