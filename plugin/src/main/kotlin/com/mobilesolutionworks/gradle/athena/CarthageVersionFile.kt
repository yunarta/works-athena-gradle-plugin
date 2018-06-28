package com.mobilesolutionworks.gradle.athena

import com.mobilesolutionworks.gradle.swift.cocoa.Platform
import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName
import javax.inject.Inject

class CarthageBuildInfo @Inject constructor(val name: String, val hash: String)

class CarthageBuildFile {
    val platforms: Map<Platform, List<CarthageBuildInfo>>
        get() = mapOf(
                Platform.iOS to iOS,
                Platform.macOS to macOs,
                Platform.tvOS to tvOS,
                Platform.watchOS to watchOS
        )

    @SerializedName("Mac")
    val macOs = emptyList<CarthageBuildInfo>()

    @SerializedName("watchOS")
    val watchOS = emptyList<CarthageBuildInfo>()

    @SerializedName("iOS")
    val iOS = emptyList<CarthageBuildInfo>()

    @SerializedName("tvOS")
    val tvOS = emptyList<CarthageBuildInfo>()

    @SerializedName("commitish")
    val commitish: String = ""
}