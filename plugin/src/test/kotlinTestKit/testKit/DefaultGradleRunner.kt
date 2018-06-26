package testKit

import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DefaultGradleRunner(private val temporaryFolder: TemporaryFolder) : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return statement(base)
    }

    private fun statement(base: Statement): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                before()
                try {
                    base.evaluate()
                } finally {
                }
            }
        }
    }

    lateinit var runner: GradleRunner

    private fun before() {
        runner = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withPluginClasspath()
                .forwardOutput()
    }
}