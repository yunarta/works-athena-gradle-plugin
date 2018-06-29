package com.mobilesolutionworks.gradle.swift.carthage

import com.mobilesolutionworks.gradle.swift.athena.Component
import java.io.File

object CarthageResolved {

    private val regex = "(github) \\\"([^\\/]*)\\/([^\\\"]*)\\\".*".toRegex()

    fun from(file: File, unresolved: (String) -> Component? = { null }) =
            file.readLines().mapNotNull {
                val find = regex.find(it)
                if (find != null) {
                    Component(find.groupValues[2], find.groupValues[3])
                } else {
                    unresolved(it)
                }
            }
}