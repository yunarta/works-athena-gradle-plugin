package com.mobilesolutionworks.gradle.swift.model

import com.mobilesolutionworks.gradle.swift.athena.ArtifactInfo
import com.mobilesolutionworks.gradle.swift.athena.Component
import com.mobilesolutionworks.gradle.swift.athena.ComponentWithVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

internal class AthenaPackageInfo(

    val name: String,
    val platform: String,
    val version: String,
    val hash: String
)

open class AthenaSchematic(val resolutions: NamedDomainObjectContainer<ComponentExtension>) {

    var enabled = false

    internal var packages: List<ArtifactInfo> = emptyList()

    internal var components: Map<String, ComponentWithVersion> = emptyMap()

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