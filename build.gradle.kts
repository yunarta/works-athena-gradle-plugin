plugins {
    id("com.mobilesolutionworks.gradle.athena") version "1.0.5"
}

repositories {
    mavenLocal()
}

xcode {
    platforms = setOf("iOS")
}

athena {
    enabled = true
}

carthage {
    github("yunarta/NullFramework") version "1.1.0"
}