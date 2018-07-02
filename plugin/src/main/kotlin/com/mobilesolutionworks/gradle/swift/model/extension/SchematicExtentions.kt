package com.mobilesolutionworks.gradle.swift.model.extension

import org.gradle.api.Project

val Project.athena: AthenaSchematic
    get() {
        return extensions.getByType(AthenaSchematic::class.java)
    }


val Project.xcode: XcodeSchematic
    get() {
        return extensions.getByType(XcodeSchematic::class.java)
    }


val Project.rome: RomeSchematic
    get() {
        return extensions.getByType(RomeSchematic::class.java)
    }


val Project.carthage: CarthageSchematic
    get() {
        return extensions.getByType(CarthageSchematic::class.java)
    }