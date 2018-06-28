package com.mobilesolutionworks.gradle.swift.tasks.xcode

import org.gradle.api.DefaultTask

internal open class XcodeBuildInfo: DefaultTask() {

    init {
        group = Xcode.group

        with(project) {

        }
    }
}