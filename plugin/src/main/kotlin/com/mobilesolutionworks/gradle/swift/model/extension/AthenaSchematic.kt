package com.mobilesolutionworks.gradle.swift.model.extension

import com.mobilesolutionworks.gradle.swift.model.AthenaPackage
import com.mobilesolutionworks.gradle.swift.model.AthenaPackageVersion
import com.mobilesolutionworks.gradle.swift.model.AthenaUploadInfo
import org.gradle.api.NamedDomainObjectContainer

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