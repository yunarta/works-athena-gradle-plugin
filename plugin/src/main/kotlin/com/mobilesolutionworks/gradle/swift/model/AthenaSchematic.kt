package com.mobilesolutionworks.gradle.swift.model

import org.gradle.api.Project

open class AthenaSchematic {

    var enabled = false
}

val Project.athena: AthenaSchematic
    get() {
        return extensions.getByType(AthenaSchematic::class.java)
    }