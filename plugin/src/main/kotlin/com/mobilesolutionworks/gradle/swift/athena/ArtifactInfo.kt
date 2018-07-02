package com.mobilesolutionworks.gradle.swift.athena

import com.mobilesolutionworks.gradle.swift.cocoa.Platform
import java.io.Serializable

internal open class AthenaPackage(val group: String, val module: String) : Serializable

internal open class AthenaPackageVersion(`package`: AthenaPackage, val version: String) :
        AthenaPackage(`package`.group, `package`.module)

internal object NullAthenaPackageVersion : AthenaPackageVersion(AthenaPackage("", ""), "")

internal open class AthenaFramework(val name: String/*, val hash: String*/) : Serializable

internal open class AthenaUploadInfo(val version: AthenaPackageVersion, val swiftVersion: String, val frameworks: Map<Platform, List<AthenaFramework>>) : Serializable
