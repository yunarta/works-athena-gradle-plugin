package com.mobilesolutionworks.gradle.swift.athena

import com.mobilesolutionworks.gradle.swift.cocoa.Platform
import java.io.Serializable

class Component(
        val group: String,
        val module: String

) : Serializable {
    override fun toString(): String {
        return "Component(group='$group', module='$module')"
    }
}

class ArtifactInfo(
        val id: Component,
        val framework: String,
        val version: String,
        val platform: Platform


) : Serializable {
    override fun toString(): String {
        return "ArtifactInfo(id=$id, framework='$framework', version='$version', platform=$platform)"
    }
}