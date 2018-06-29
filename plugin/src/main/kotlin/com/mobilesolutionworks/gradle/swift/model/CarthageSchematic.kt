package com.mobilesolutionworks.gradle.swift.model

import org.gradle.api.Project

abstract class CarthageDependency {

    abstract val group: String
    abstract val module: String

    abstract val semantic: String

    val options = FrameworkOptions()

    internal var versioning: String = ""

    var frameworks = setOf<String>()

    infix fun compatible(version: String) {
        versioning = " ~> $version"
    }

    infix fun version(version: String) {
        versioning = " == $version"
    }

    infix fun atLeast(version: String) {
        versioning = " >= $version"
    }
}

class CarthageGit(val repo: String) : CarthageDependency() {

    override val semantic: String
        get() {
            return "git \"$repo\"$versioning"
        }

    var backingGroup = ""
    var backingModule = ""

    fun id(group: String, module: String) {
        backingGroup = group
        backingModule = module
    }

    override val group: String
        get() {
            return backingGroup
        }

    override val module: String
        get() {
            return backingModule
        }
}

class CarthageGitHub(val repo: String) : CarthageDependency() {

    override val semantic: String
        get() {
            return "github \"$repo\"$versioning"
        }

    override val group: String
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

    fun github(repo: String, configure: CarthageGitHub.() -> Unit): CarthageGitHub = CarthageGitHub(repo).also {
        it.configure()
        declaredDependencies.add(it)
    }

    fun git(repo: String, configure: CarthageGit.() -> Unit): CarthageGit = CarthageGit(repo).also {
        it.configure()
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