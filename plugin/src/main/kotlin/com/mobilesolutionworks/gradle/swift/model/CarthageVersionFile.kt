package com.mobilesolutionworks.gradle.swift.model

import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName
import javax.inject.Inject

internal class CarthageBuildInfo @Inject constructor(val name: String, val hash: String)

internal class CarthageBuildFile constructor(
        @SerializedName(value = "Mac")
        private val macOs: List<CarthageBuildInfo> = emptyList(),

        @SerializedName("watchOS")
        private val watchOS: List<CarthageBuildInfo> = emptyList(),

        @SerializedName("iOS")
        private val iOS: List<CarthageBuildInfo> = emptyList(),

        @SerializedName("tvOS")
        private val tvOS: List<CarthageBuildInfo> = emptyList()/*,

        @SerializedName("commitish")
        private val commitish: String = ""*/
) {
    val platforms: Map<Platform, List<CarthageBuildInfo>>
        get() = mapOf(
                Platform.iOS to iOS,
                Platform.macOS to macOs,
                Platform.tvOS to tvOS,
                Platform.watchOS to watchOS
        )
}