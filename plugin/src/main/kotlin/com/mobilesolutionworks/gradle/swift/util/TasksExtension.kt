package com.mobilesolutionworks.gradle.swift.util

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

inline fun <reified T : Task> TaskContainer.withType(closure: T.() -> Unit) {
    withType(T::class.java).forEach {
        it.closure()
    }
}