//import com.adarshr.gradle.testlogger.theme.ThemeType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.2.50"
    `java-gradle-plugin`
    jacoco

    id("com.gradle.plugin-publish") version "0.9.10"
    id("com.mobilesolutionworks.gradle.jacoco") version "1.1.3"
}

group = "com.mobilesolutionworks.gradle"
version = "1.0.4"

repositories {
    mavenCentral()
    maven {
        url = URI("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

worksJacoco {
    onlyRunCoverageWhenReporting = true
    hasTestKit = true
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val sourceSets: SourceSetContainer = java.sourceSets
sourceSets {
    "test" {
        java.srcDir("src/test/kotlinTestKit")
        java.srcDir("src/test/kotlinCoverage")

        val enableParallelTest: String? by rootProject.extra
        if (enableParallelTest == "true") {
            resources.srcDir("src/test/parallelTest")
        }
    }
}

dependencies {
    compileOnly(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("commons-io:commons-io:2.6")
    implementation("commons-io:commons-io:2.6")
    implementation("com.google.code.gson:gson:2.8.5")

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.0-M1")
    testImplementation("org.junit.jupiter:junit-jupiter-migrationsupport:5.3.0-M1")
    testImplementation("org.junit-pioneer:junit-pioneer:0.1-SNAPSHOT")

    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.0-M1")
    testRuntime("org.junit.vintage:junit-vintage-engine:5.3.0-M1")
}

gradlePlugin {
    (plugins) {
        "swift-athena" {
            id = "com.mobilesolutionworks.gradle.athena"
            implementationClass = "com.mobilesolutionworks.gradle.swift.AthenaPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/yunarta/works-athena-gradle-plugin"
    vcsUrl = "https://github.com/yunarta/works-athena-gradle-plugin"
    description = """
        Athena is a Gradle plugin for managing Carthage using Gradle.

        This plugin would reduce the learning curve of using Carthage and its caching.
        This plugin allow you to use Rome as the caching manager, or use Athena instead.

        With Athena, the cache will be uploaded into Artifactory and Bintray
    """.trimIndent()

    (plugins) {
        "swift-athena" {
            id = "com.mobilesolutionworks.gradle.athena"
            displayName = "Gradle plugin for managing Carthage and upload binary cache into Artifactory"
            tags = listOf("swift", "carthage", "rome", "xcode", "ios")
            version = project.version
        }
    }
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }

    // generated classes
    classDirectories = fileTree(mapOf(
            "dir" to "$buildDir/classes/java/main")
    ) + fileTree(mapOf(
            "dir" to "$buildDir/classes/kotlin/main")
    )

    // sources
    sourceDirectories = files(listOf("src/main/kotlin", "src/main/java"))
    executionData = fileTree(mapOf("dir" to project.rootDir.absolutePath, "include" to "**/build/jacoco/*.exec"))
}

tasks.create("createClasspathManifest") {
    group = "plugin development"

    val target = file("$buildDir/classpathManifest/manifest.gradle")

    inputs.files(sourceSets.getAt("main").runtimeClasspath)
    outputs.files(target)

    doFirst {
        val text = sourceSets["main"].runtimeClasspath
                .map { "        classpath(files(\"$it\"))" }
                .joinToString(System.lineSeparator())
        target.writeText("""
buildscript {
    dependencies {
$text
    }
}
        """.trimIndent())
    }
}

val ignoreFailures: String? by rootProject.extra
val shouldIgnoreFailures = ignoreFailures?.toBoolean() == true

tasks.withType<Test> {
    ignoreFailures = shouldIgnoreFailures
    useJUnitPlatform()
}