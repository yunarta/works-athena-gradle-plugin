package com.mobilesolutionworks.gradle.swift.model.extension

import com.mobilesolutionworks.gradle.swift.model.AthenaPackage
import com.mobilesolutionworks.gradle.swift.model.AthenaPackageVersion
import com.mobilesolutionworks.gradle.swift.model.AthenaUploadInfo
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

enum class AthenaUploadTarget {
    Bintray, Artifactory, MavenLocal
}

open class AthenaSchematic(project: Project, val resolutions: NamedDomainObjectContainer<PackageExtension>) {

    var upload = AthenaUploadTarget.Artifactory

    var organization = ""
    var repository = "athena"

    var enabled = false

    internal var swiftVersion = "4.1.2"

    var workDir = project.file("Athena")

    internal fun resolve(it: String): AthenaPackage? {
        return resolutions.findByName(it)?.let {
            AthenaPackage(it.group, it.module)
        }
    }

    internal var artifacts: List<AthenaUploadInfo> = emptyList()

    internal var packages: Map<String, AthenaPackageVersion> = emptyMap()
}