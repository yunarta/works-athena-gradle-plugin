package com.mobilesolutionworks.gradle.swift.carthage

import org.`junit$pioneer`.jupiter.TempDirectory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.nio.file.Path

class CarthageResolvedTests {

    @Test
    @ExtendWith(TempDirectory::class)
    fun `test parsing github source`(@TempDirectory.TempDir path: Path) {
        val root = path.toFile()
        val file = File.createTempFile("Carthage", "resolved", root)

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
    @ExtendWith(TempDirectory::class)
    fun `test parsing git source`(@TempDirectory.TempDir path: Path) {
        val root = path.toFile()
        val file = File.createTempFile("Carthage", "resolved", root)
        file.writeText("""
           git "https://enterprise.local/desktop/git-error-translations2.git"

        """.trimIndent())
        CarthageResolved.from(file)
    }
}