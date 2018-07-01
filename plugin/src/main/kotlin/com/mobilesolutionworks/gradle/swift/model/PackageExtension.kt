package com.mobilesolutionworks.gradle.swift.model;

class PackageExtension(val name: String) {

    var group: String = ""
    var module: String = ""

    override fun toString(): String {
        return "PackageExtension(name='$name', group='$group', module='$module')"
    }
}
