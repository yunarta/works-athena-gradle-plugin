package com.mobilesolutionworks.gradle.swift.carthage

import com.mobilesolutionworks.gradle.swift.athena.AthenaPackage
import com.mobilesolutionworks.gradle.swift.athena.AthenaPackageVersion
import org.gradle.api.tasks.StopExecutionException
import java.io.File

object CarthageResolved {

    private val regex = "(git|github) \\\"(([^\\/]*)\\/([^\\\"]*))\\\" \"([^\"]*)\"".toRegex()

    internal fun from(file: File, resolver: (String) -> AthenaPackage? = { null }) =
            file.readLines().mapNotNull { it ->
                val find = regex.find(it)
                when {
                    find != null && find.groupValues[1] == "github" -> {
                        AthenaPackageVersion(AthenaPackage(find.groupValues[3], find.groupValues[4]),
                                find.groupValues[5])
                    }

                    find != null && find.groupValues[1] == "git" -> {
                        resolver(find.groupValues[2])?.let {
                            AthenaPackageVersion(it, find.groupValues[5])
                        }
                    }

                    else -> throw StopExecutionException("""
                            Found source other than git or github in Cartfile.resolved
                            $it
                        """.trimIndent())
                }
            }
}