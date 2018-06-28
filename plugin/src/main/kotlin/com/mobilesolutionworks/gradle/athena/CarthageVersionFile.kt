package com.mobilesolutionworks.gradle.athena

import com.mobilesolutionworks.gradle.swift.model.Platform
import javax.inject.Inject

class CarthageBuildInfo @Inject constructor(val name: String, val hash: String)

class CarthageBuildFile {
    val platforms: Map<String, List<CarthageBuildInfo>>
        get() {
            return mapOf(
                    Platform.ios.value to iOS,
                    Platform.macos.value to Mac,
                    Platform.tvos.value to tvOS,
                    Platform.watchos.value to watchOS
            )
        }

    val Mac = emptyList<CarthageBuildInfo>()
    val watchOS = emptyList<CarthageBuildInfo>()
    val iOS = emptyList<CarthageBuildInfo>()
    val tvOS = emptyList<CarthageBuildInfo>()
    val commitish: String = ""
}