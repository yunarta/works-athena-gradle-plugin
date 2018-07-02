package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.model.AthenaPackageVersion

internal object AthenaTaskDef {

    const val group = "athena"
}

internal fun AthenaPackageVersion.component(swift: String): String {
    return "$group:$module:$version-Swift$swift"
}

internal fun AthenaPackageVersion.bintrayPackage(): String {
    return "$group:$module"
}

internal fun AthenaPackageVersion.bintrayTarget(swift: String): String {
    return "$group:$module/$version-Swift$swift"
}

internal fun AthenaPackageVersion.source(swift: String): String {
    return "$group/$module/$version-Swift$swift"
}