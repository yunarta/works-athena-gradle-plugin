package com.mobilesolutionworks.gradle.swift.tasks.xcode

object XcodeTaskDef {

    const val group = "xcode"

    enum class Tasks(val value: String) {
        XcodeBuildInfo("xcodeBuildInfo"),
    }
}