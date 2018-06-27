package com.mobilesolutionworks.gradle.swift.tasks.carthage

object Carthage {

    val group = "carthage"

    enum class Tasks(val value: String) {
        CarthageCartfileCreate("carthageCartfileCreate"),
        CarthageCartfileResolve("carthageCartfileResolve"),
        CarthageCartfileReplace("carthageCartfileReplace"),
        CarthageActivateUpdate("carthageActivateUpdate"),
        CarthageBootstrap("carthageBootstrap"),
        CarthageUpdate("carthageUpdate"),
    }
}