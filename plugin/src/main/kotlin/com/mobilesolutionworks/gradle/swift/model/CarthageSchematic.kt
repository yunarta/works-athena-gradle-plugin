package com.mobilesolutionworks.gradle.swift.model

import org.gradle.api.Project

abstract class CarthageDependency {

    abstract val org: String
    abstract val module: String

    abstract val semantic: String

    val options = FrameworkOptions()
}

class CarthageGitHub(val repo: String) : CarthageDependency() {

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

    override val org: String
        get() {
            return repo.substringBefore("/")
        }

    override val module: String
        get() {
            return repo.substringAfter("/")
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

    fun github(repo: String, configure: (FrameworkOptions) -> Unit): CarthageGitHub = CarthageGitHub(repo).also {
        configure(it.options)
        declaredDependencies.add(it)
    }
}

val Project.carthage: CarthageSchematic
    get() {
        return extensions.getByType(CarthageSchematic::class.java)
    }

class FrameworkOptions {

    internal var key: String = ""

    internal var frameworks = setOf<String>()

    fun map(key: String, frameworks: Set<String>) {
        this.key = key
        this.frameworks = frameworks
    }
}