package com.mobilesolutionworks.gradle.swift.carthage

import com.mobilesolutionworks.gradle.swift.athena.Component
import org.gradle.api.tasks.StopExecutionException
import java.io.File

object CarthageResolved {

    private val regex = "(git|github) \\\"(([^\\/]*)\\/([^\\\"]*))\\\".*".toRegex()

    fun from(file: File, resolver: (String) -> Component? = { null }) =
            file.readLines().mapNotNull {
                val find = regex.find(it)
                if (find != null) {
                    when (find.groupValues[1]) {
                        "github" -> Component(find.groupValues[3], find.groupValues[4])
                        "git" -> resolver(find.groupValues[2])
                        else -> throw StopExecutionException("""
                            Found source other than git or github in Cartfile.resolved
                            $it
                        """.trimIndent())
                    }
                } else {
                    throw StopExecutionException("""
                            Found source other than git or github in Cartfile.resolved
                            $it
                        """.trimIndent())
                }
            }
}