package com.mobilesolutionworks.gradle.swift.athena

import com.mobilesolutionworks.gradle.swift.cocoa.Platform
import java.io.Serializable

open class AthenaPackage(
        val group: String,
        val module: String

) : Serializable {
    override fun toString(): String {
        return "Package(group='$group', module='$module')"
    }
}

class AthenaPackageVersion(`package`: AthenaPackage, val version: String) :
        AthenaPackage(`package`.group, `package`.module)

class AthenaFramework(val name: String, val hash: String) : Serializable

class AthenaUploadInfo(val version: AthenaPackageVersion, val swiftVersion: String, val frameworks: Map<Platform, List<AthenaFramework>>) : Serializable
