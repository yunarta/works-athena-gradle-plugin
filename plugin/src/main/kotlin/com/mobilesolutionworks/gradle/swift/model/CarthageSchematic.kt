package com.mobilesolutionworks.gradle.swift.model

import org.gradle.api.Project

abstract class CarthageDependency() {

    abstract val semantic: String

    val options = FrameworkOptions()
}

class CarthageGitHub(private val repo: String) : CarthageDependency() {

    private var versioning: String = ""

    infix fun compatible(version: String) {
        versioning = " ~> $version"
    }

    infix fun version(version: String) {
        versioning = " == $version"
    }

    infix fun atLeast(version: String) {
        versioning = " >= $version"
    }

    override val semantic: String
        get() {
            return "github \"$repo\"$versioning"
        }
}


open class CarthageSchematic {

    var updates = false

    private val declaredDependencies = mutableListOf<CarthageDependency>()

    val dependencies: List<CarthageDependency>
        get() {
            return declaredDependencies
        }

    fun github(repo: String): CarthageGitHub = CarthageGitHub(repo).also {
        declaredDependencies.add(it)
    }

    fun github(repo: String, rome: (FrameworkOptions) -> Unit): CarthageGitHub = CarthageGitHub(repo).also {
        rome(it.options)
        declaredDependencies.add(it)
    }
}

val Project.carthage: CarthageSchematic
    get() {
        return extensions.getByType(CarthageSchematic::class.java)
    }

class FrameworkOptions {

    var key: String = ""
    var frameworks = listOf<String>()

    fun map(key: String, frameworks: List<String>) {
        this.key = key
        this.frameworks = frameworks
    }
}