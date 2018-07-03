package testKit

import com.mobilesolutionworks.gradle.jacoco.TestKitConfiguration
import org.junit.rules.ExternalResource
import org.junit.rules.TemporaryFolder
import java.util.*

open class TestWithCoverage(private val temporaryFolder: TemporaryFolder) : ExternalResource() {

    override fun before() {
        super.before()

        val agentString = TestKitConfiguration(javaClass.simpleName).agentString
        if (agentString != null) {
            temporaryFolder.newFile("gradle.properties").apply {
                val properties = Properties()
                properties.setProperty("org.gradle.jvmargs", agentString)
                properties.store(outputStream(), "Gradle")
            }
        }
    }
}