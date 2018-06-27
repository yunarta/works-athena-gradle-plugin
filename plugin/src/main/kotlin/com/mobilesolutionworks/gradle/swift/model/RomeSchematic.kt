package com.mobilesolutionworks.gradle.swift.model

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File

open class RomeSchematic {

    var enabled = true

    var s3Bucket: String? = null

    var cachePath = File(FileUtils.getUserDirectory(), "Library/Cache/Rome")
}

val Project.rome: RomeSchematic
    get() {
        return extensions.findByName("rome") as RomeSchematic
    }
