package com.mobilesolutionworks.gradle.swift.carthage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder


class CarthageResolvedTests {

    @JvmField
    @Rule
    val temporaryDir = TemporaryFolder()

    @Test
    fun `test parsing github source`() {

        val file = temporaryDir.newFile()
        file.writeText("""
            github "Alamofire/Alamofire" ~> 4.1
            github "ReactiveCocoa/ReactiveSwift" ~> 3.0
        """.trimIndent())
        val from = CarthageResolved.from(file)
        assertEquals("Alamofire", from.get(0).group)
        assertEquals("Alamofire", from.get(0).module)
        assertEquals("ReactiveCocoa", from.get(1).group)
        assertEquals("ReactiveSwift", from.get(1).module)
    }

    @Test
    fun `test parsing git source`() {
        val file = temporaryDir.newFile()
        file.writeText("""
           git "https://enterprise.local/desktop/git-error-translations2.git"

        """.trimIndent())
        val unresolved= mutableListOf<String>()
        CarthageResolved.from(file) {
            unresolved.add(it)
            null
        }

        assertTrue(unresolved.isNotEmpty())
    }
}