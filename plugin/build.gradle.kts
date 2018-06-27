import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.50"
    `java-gradle-plugin`
    jacoco

    id("com.gradle.plugin-publish") version "0.9.10"
    id("com.mobilesolutionworks.gradle.jacoco") version "1.1.3"
}

group = "com.mobilesolutionworks.gradle"
version = "1.0.0"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val sourceSets: SourceSetContainer = java.sourceSets
sourceSets {
    "test" {
        java.srcDir("src/test/kotlinTestKit")
    }
}
dependencies {
    compileOnly(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("commons-io:commons-io:2.6")

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
}

gradlePlugin {
    (plugins) {
        "works-swift" {
            id = "com.mobilesolutionworks.gradle.swift"
            implementationClass = "com.mobilesolutionworks.gradle.swift.SwiftPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/yunarta/works-swift-gradle-plugin"
    vcsUrl = "https://github.com/yunarta/works-swift-gradle-plugin"
    description = ""

    (plugins) {
        "works-swift" {
            id = "com.mobilesolutionworks.gradle.swift"
            displayName = """""".trimIndent()
        }
    }
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
    maxParallelForks = Runtime.getRuntime().availableProcessors().div(2)
    ignoreFailures = shouldIgnoreFailures

    doFirst {
        logger.quiet("Test with max $maxParallelForks parallel forks")
    }
}