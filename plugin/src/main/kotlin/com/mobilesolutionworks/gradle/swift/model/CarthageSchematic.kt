package com.mobilesolutionworks.gradle.swift.model

import org.gradle.api.Project

abstract class CarthageDependency(val name: String) {

    abstract val semantic: String

    val romeCache = RomeCache()
}

class CarthageGitHub(name: String, private val repo: String) : CarthageDependency(name) {

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

    var platforms = emptyList<String>()

    private val declaredDependencies = mutableListOf<CarthageDependency>()
    private val declaredDependencies2 = mutableListOf<CarthageDependency>()

    val dependencies: List<CarthageDependency>
        get() {
            return declaredDependencies
        }

    fun github(name: String, repo: String): CarthageGitHub = CarthageGitHub(name, repo).also {
        declaredDependencies.add(it)
    }

    fun github(name: String, repo: String, rome: (RomeCache) -> Unit): CarthageGitHub = CarthageGitHub(name, repo).also {
        rome(it.romeCache)
        declaredDependencies.add(it)
    }

    internal val hasDeclaredPlatforms: Boolean
        get() = platforms.isNotEmpty()

    internal val declaredPlatforms: String
        get() = platforms.joinToString(",")
}

val Project.carthage: CarthageSchematic
    get() {
        return extensions.findByName("carthage") as CarthageSchematic
    }

class RomeCache {

    var key: String = ""
    var frameworks = emptyList<String>()

    fun map(frameworks: List<String>) {
        this.frameworks = frameworks
    }


    fun map(key: String, frameworks: List<String>) {
        this.key = key
        this.frameworks = frameworks
    }
}