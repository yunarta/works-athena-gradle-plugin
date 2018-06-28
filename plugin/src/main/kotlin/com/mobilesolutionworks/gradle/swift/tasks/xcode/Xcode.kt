package com.mobilesolutionworks.gradle.swift.tasks.xcode

object Xcode {

    val group = "xcode"

    enum class Tasks(val value: String) {
        XcodeBuildInfo("xcodeBuildInfo"),
    }
}