package com.mobilesolutionworks.gradle.swift.model.extension

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File

open class RomeSchematic {

    var enabled = false

    var s3Bucket: String? = null

    var cachePath = File(FileUtils.getUserDirectory(), "Library/Cache/Rome")
}
