package com.mobilesolutionworks.gradle.swift.model

import com.mobilesolutionworks.gradle.swift.athena.ArtifactInfo
import org.gradle.api.Project

open class AthenaSchematic {

    var enabled = false

    internal var packages: Set<ArtifactInfo> = emptySet()
}

val Project.athena: AthenaSchematic
    get() {
        return extensions.getByType(AthenaSchematic::class.java)
    }