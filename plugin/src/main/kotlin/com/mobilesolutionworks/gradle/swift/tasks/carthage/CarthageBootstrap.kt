package com.mobilesolutionworks.gradle.swift.tasks.carthage

import com.mobilesolutionworks.gradle.swift.i18n.Strings
import com.mobilesolutionworks.gradle.swift.util.withType
import org.gradle.api.DefaultTask

internal open class CarthageBootstrap : DefaultTask() {

    init {
        group = Carthage.group
        description = Strings["CartfileResolve_description"]


        with(project) {
            tasks.withType<CartfileReplace> {
                this@CarthageBootstrap.dependsOn(this)
            }
        }
    }
}
