package com.mobilesolutionworks.gradle.swift.model

import org.gradle.api.Project

abstract class CarthageDependency(val name: String) {

    abstract val semantic: String

    val romeCache = RomeCache()
}

class CarthageGitHub(name: String, private val repo: String) : CarthageDependency(name) {

    private var versioning: String = ""

    infix fun compatible(version: String) {
        versioning = "~> $version"
    }

    infix fun version(version: String) {
        versioning = "== $version"
    }

    infix fun atLeast(version: String) {
        versioning = ">= $version"
    }

    override val semantic: String
        get() {
            return "github \"$repo\" $versioning"
        }
}


open class CarthageSchematic {

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

fun Project.create() {
    tasks.create("romeCreate") {
        val carthage = CarthageSchematic()
        val repositoryMap = carthage.dependencies.mapNotNull {
            val romeCache = it.romeCache
            if (romeCache.frameworks.isNotEmpty()) {
                "${romeCache.key} = ${romeCache.frameworks.joinToString(", ")}"
            } else {
                null
            }
        }.let {
            val repositoryMap = StringBuilder("[RepositoryMap]").append(System.lineSeparator())
            it.forEach {
                repositoryMap.append("  $it").append(System.lineSeparator())
            }
            repositoryMap.toString()
        }
    }

}