
plugins {
    id("com.mobilesolutionworks.gradle.reporting") version "1.0.9"
    id("jacoco")
}

allprojects {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
}