package com.mobilesolutionworks.gradle.swift.carthage

import org.`junit$pioneer`.jupiter.TempDirectory
import org.gradle.api.tasks.StopExecutionException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import testKit.newFile
import java.nio.file.Path

@ExtendWith(TempDirectory::class)
@DisplayName("Test CarthageResolved")
class CarthageResolvedTests {

    @Test
    @DisplayName("test resolving github sources")
    fun test1(@TempDirectory.TempDir temporaryDir: Path) {
        val file = temporaryDir.toFile().newFile("Cartfile.resolved")
        file.writeText("""
            github "Alamofire/Alamofire" "4.1"
            github "ReactiveCocoa/ReactiveSwift" "3.0"
        """.trimIndent())
        val from = CarthageResolved.from(file) {
            null
        }
        assertAll(
                { assertEquals("Alamofire", from.get(0).group) },
                { assertEquals("Alamofire", from.get(0).module) }
        )
        assertAll(
                { assertEquals("ReactiveCocoa", from.get(1).group) },
                { assertEquals("ReactiveSwift", from.get(1).module) }
        )
    }

    @Test
    @DisplayName("test resolving git sources")
    fun test2(@TempDirectory.TempDir temporaryDir: Path) {
        val file = temporaryDir.toFile().newFile("Cartfile.resolved")
        file.writeText("""
           git "https://enterprise.local/desktop/git-error-translations2.git"
        """.trimIndent())
        Assertions.assertThrows(StopExecutionException::class.java) {
            val unresolved = mutableListOf<String>()
            CarthageResolved.from(file) {
                unresolved.add(it)
                null
            }

            assertTrue(unresolved.isNotEmpty())
        }
    }
}