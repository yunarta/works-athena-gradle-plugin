package com.mobilesolutionworks.gradle.swift.carthage

import com.mobilesolutionworks.gradle.swift.athena.Component
import java.io.File

object CarthageResolved {

    private val regex = "(github) \\\"([^\\/]*)\\/([^\\\"]*)\\\".*".toRegex()

    fun from(file: File) =
            file.readLines().mapNotNull {
                regex.find(it)?.let {
                    Component(it.groupValues[2], it.groupValues[3])
                }
            }
}