package com.mobilesolutionworks.gradle.swift.carthage

import com.mobilesolutionworks.gradle.swift.athena.Component
import com.mobilesolutionworks.gradle.swift.athena.ComponentWithVersion
import org.gradle.api.tasks.StopExecutionException
import java.io.File

object CarthageResolved {

    private val regex = "(git|github) \\\"(([^\\/]*)\\/([^\\\"]*))\\\" \"([^\"]*)\"".toRegex()

    internal fun from(file: File, resolver: (String) -> Component? = { null }) =
            file.readLines().mapNotNull {
                val find = regex.find(it)
                println("it = $it, found = $find")
                if (find != null) {
                    when (find.groupValues[1]) {
                        "github" -> {
                            ComponentWithVersion(Component(find.groupValues[3], find.groupValues[4]),
                                    find.groupValues[5])
                        }
                        "git" -> {
                            resolver(find.groupValues[2])?.let {
                                ComponentWithVersion(it, find.groupValues[5])
                            }
                        }
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