package testKit

import com.mobilesolutionworks.gradle.jacoco.TestKitConfiguration
import org.`junit$pioneer`.jupiter.TempDirectory
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver
import java.io.File
import java.io.IOException
import java.util.*

class GradleRunnerProvider : ParameterResolver {

    companion object {
        val Namespace: ExtensionContext.Namespace = ExtensionContext.Namespace.create(TempDirectory::class.java)
    }

    @Throws(ParameterResolutionException::class)
    override fun supportsParameter(parameterContext: ParameterContext,
                                   extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type == GradleRunner::class.java
    }

    @Throws(ParameterResolutionException::class)
    override fun resolveParameter(parameterContext: ParameterContext,
                                  extensionContext: ExtensionContext): Any {
        return extensionContext.getStore(Namespace) //
                .getOrComputeIfAbsent<String, GradleConstructor>("gradleRunner",
                        { _ -> GradleConstructor() },
                        GradleConstructor::class.java) //
                .get()
    }

    class GradleConstructor : ExtensionContext.Store.CloseableResource {
        val temporaryFolder = createTemporaryFolder()

        override fun close() {
            temporaryFolder.deleteRecursively()
        }

        @Throws(IOException::class)
        private fun createTemporaryFolder(): File {
            val createdFolder = File.createTempFile("junit", "")
            createdFolder.delete()
            createdFolder.mkdir()
            return createdFolder
        }

        fun get(): GradleRunner {
            val agentString = TestKitConfiguration("jacoco").agentString
            if (agentString != null) {
                temporaryFolder.newFile("gradle.properties").apply {
                    val properties = Properties()
                    properties.setProperty("org.gradle.jvmargs", agentString)
                    properties.store(outputStream(), "Gradle")
                }
            }
            return GradleRunner.create()
                    .withProjectDir(temporaryFolder)
                    .withPluginClasspath()
                    .forwardOutput()
        }
    }
}