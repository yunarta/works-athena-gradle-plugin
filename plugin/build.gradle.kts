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