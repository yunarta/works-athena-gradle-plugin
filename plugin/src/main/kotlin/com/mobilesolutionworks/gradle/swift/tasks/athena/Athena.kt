package com.mobilesolutionworks.gradle.swift.tasks.athena

import com.mobilesolutionworks.gradle.swift.athena.AthenaPackageVersion

object Athena {

    val group = "athena"
}

fun AthenaPackageVersion.artifactory(swift: String): String {
    return "$group/$module/$version-Swift$swift"
}

fun AthenaPackageVersion.artifactoryFile(swift: String): String {
    return "$group/$module/$version-Swift$swift/$module-$version-Swift$swift.zip"
}