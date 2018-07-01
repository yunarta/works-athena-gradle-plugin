package com.mobilesolutionworks.gradle.swift.model

import com.mobilesolutionworks.gradle.swift.athena.AthenaPackage
import com.mobilesolutionworks.gradle.swift.athena.AthenaPackageVersion
import com.mobilesolutionworks.gradle.swift.athena.AthenaUploadInfo
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

internal class AthenaPackageInfo(

        val name: String,
        val platform: String,
        val version: String,
        val hash: String
)

open class AthenaSchematic(val resolutions: NamedDomainObjectContainer<PackageExtension>) {

    var enabled = false

    var swiftVersion = "4.1.2"

    internal fun resolve(it: String): AthenaPackage? {
        return resolutions.findByName(it)?.let {
            AthenaPackage(it.group, it.module)
        }
    }

    internal var artifacts: List<AthenaUploadInfo> = emptyList()

    internal var packages: Map<String, AthenaPackageVersion> = emptyMap()
}

val Project.athena: AthenaSchematic
    get() {
        return extensions.getByType(AthenaSchematic::class.java)
    }