package com.mobilesolutionworks.gradle.swift.model

import com.mobilesolutionworks.gradle.swift.athena.ArtifactInfo
import com.mobilesolutionworks.gradle.swift.athena.Component
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

open class AthenaSchematic(val resolutions: NamedDomainObjectContainer<ComponentExtension>) {

    var enabled = false

    internal var packages: Set<ArtifactInfo> = emptySet()

    internal val resolvedObjects = mutableMapOf<String, Component>()

    init {
        resolutions.whenObjectAdded {
            resolvedObjects[it.name] = Component(it.group, it.module)
        }
    }
}

val Project.athena: AthenaSchematic
    get() {
        return extensions.getByType(AthenaSchematic::class.java)
    }