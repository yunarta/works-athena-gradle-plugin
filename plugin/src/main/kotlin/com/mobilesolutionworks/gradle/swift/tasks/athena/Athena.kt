package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.model.AthenaPackageVersion

internal object Athena {

    val group = "athena"
}

internal fun AthenaPackageVersion.artifactory(swift: String): String {
    return "$group/$module/$version-Swift$swift"
}

internal fun AthenaPackageVersion.artifactoryFile(swift: String): String {
    return "$group/$module/$version-Swift$swift/$module-$version-Swift$swift.zip"
}