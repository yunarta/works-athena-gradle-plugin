package com.mobilesolutionworks.gradle.swift.tasks.rome

import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.DefaultTask

internal open class RomeUpdate : DefaultTask() {

    init {
        group = Rome.group

        with(project) {
            // dependecies
            tasks.withType<CreateRomefile> {
                this@RomeUpdate.dependsOn(this)
                this.shouldRunAfter()
            }

            tasks.withType<Download> {
                this@RomeUpdate.dependsOn(this)
            }
        }
    }

}