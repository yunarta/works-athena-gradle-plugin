# Works presents Athena

[![Join the chat at https://mobilesolutionworks.slack.com/messages/CBLV86PNJ](https://img.shields.io/badge/chat-on_slack-red.svg?style=flat-square)](https://mobilesolutionworks.slack.com/messages/CBLV86PNJ)
[![Build Status](http://jenkins.mobilesolutionworks.com:8080/job/github/job/yunarta/job/works-athena-gradle-plugin/job/master/badge/icon)](http://jenkins.mobilesolutionworks.com:8080/job/github/job/yunarta/job/works-athena-gradle-plugin/job/master/)
[![codecov](https://codecov.io/gh/yunarta/works-athena-gradle-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/yunarta/works-athena-gradle-plugin)
[![gradle plugin](https://img.shields.io/badge/gradle_plugin-1.0.5-blue.svg?style=flat-square)](https://plugins.gradle.org/plugin/com.mobilesolutionworks.gradle.athena)

## Preface

As a mobile application engineer that develops iOS and Android applications, I found that in Android, dependencies can be downloaded and gets compiled to your application easier and faster with Gradle, compared to iOS dependencies when i'm using Carthage or Cocoapods.

And along with my recent experiments in working on Gradle plugins, I realized that with Gradle plugins, we can reduce the complexity of using Carthage and even make Carthage works better.

### Dependency Manager

Let me explain briefly about dependency manager that you probably had used from my point of view related to development process. You may skip to this section and go to on [how to use Athena](#Athena)


#### Cocoapods

https://cocoapods.org/

Cocoapods is still my favorite dependency tools for iOS development, however as my projects is getting bigger, compiling and archiving for release now take more than 20 minutes to finish.

#### Carthage

https://github.com/Carthage/Carthage

Carthage offers different way include libraries by linking to their Framework directly rather than by source like Cocoapods. As the result compilation will be significantly faster.

**Rome**

https://github.com/blender/Rome

Rome is a caching tools for Carthage, basically this tools will upload outputs of Carthage, either to your local path or in to Amazon S3 compatible repository.  

### Intention

For now I will exclude Cocoapods usage in this plugin for a while, and focus on how we can do with Carthage to make it even better.

Below are issues with Carthage and Rome with their current state

**Carthage - Missing precompiled frameworks or different Swift version**

When the dependency project does not have precompiled frameworks binaries or they were compiled with different Swift version, Carthage will downloads the source and compiles them again. With certain libraries this will takes very long.

In CI/CD where your project gets compiled in clean environment, each iteration will redo the compilation.

Of course then the recommended solution is to use Rome

**Rome - Repository sharing**

While Rome will solve the framework recompilation issues, there is no global Rome repository where you can get the binaries. It would be great is there a Maven like repository for Carthage precompiled frameworks.

**Rome - CI/CD flow**

Rome give you an example code that you can put in your CI/CD

```
rome download --platform iOS # download missing frameworks (or copy from local cache)
rome list --missing --platform ios | awk '{print $1}' | xargs carthage update --platform ios --cache-builds # list what is missing and update/build if needed
rome list --missing --platform ios | awk '{print $1}' | xargs rome upload --platform ios # upload what is missing
```

However I found that if the Swift toolchains is updated, this code will download an incompatible Swift version of dependency.

**Conclusion**

In nutshell, Carthage and Rome works well in developer machine with a bit of learning, but when you actually automating them on CI machine, you need to do more works to make sure it works as expected

## <a name="Athena"></a>Athena

First of all, Athena is a Gradle plugin. And yes! even you are iOS developer, Gradle can help you solve things.

Athena helps you to solve at least two things
1. Writes everything you need to configure how Carthage works in a structured platform (with auto completes if you are using IntelliJ IDEA)
2. Download and upload the frameworks produced by Carthage into Maven repository

If you are new in Gradle, please take your time to read about it at [Gradle.org](https://gradle.org/)

**Gradle Installation**

In order to use Gradle, you need Java 1.8.
You can use homebrew to get the latest Gradle
```
brew install gradle
```
Or you may download the [bootstrap kit](https://github.com/yunarta/works-athena-gradle-plugin/archive/bootstrap-kit.zip) here.

If you are using the boostrap kit, instead of running the command with `gradle`, use `./gradlew` instead


Your typical ```build.gradle.kts``` structure will be like this
```groovy
// Use athena in the build script
plugins {
    id("com.mobilesolutionworks.gradle.athena") version "1.0.5"
}

xcode {
    // Declare which platform that Carthage should use
    platforms = setOf("iOS", "macOS", "tvOS", "watchOS")
}

athena {
    enabled = true

    // Where the artifact should be uploaded to
    upload = AthenaUploadTarget.MavenLocal
}

carthage {
    // Define your dependency as like what you would do in Cartfile
    github("yunarta/NullFramework") version "1.1.0"
}
```

Then execute ```gradle carthageBootstrap``` or ```gradle carthageBootstrap athenaUpload```

This execution will
1. Download Carthage precompiled frameworks from your ```mavenLocal```,
2. Download from Github release files
3. Run ```carthage bootstrap```
4. And if ```athenaUpload``` is provided it will upload the result into your ```mavenLocal```

With Gradle incremental build, when you executes the command again, provided the ```build.gradle.kts``` file is not modified, the execution will returns immediately.

### Configuration

#### xcode

```groovy
xcode {
    // Declare which platform that Carthage should use
    platforms = setOf("iOS", "macOS", "tvOS", "watchOS")

    // Declare which swift toolchain [optional]
    swiftToolchains = "org.swift.4020170919a"
}
```
- **platforms**, defines which platform that you want Carthage to produces.
- **swiftToolchains**, defines which Swift Toolchains that you want Carthage to use. Athena will also determine the Swift version from the toolchains.

#### athena

```groovy
// import must be in the first lines of build.gradle.kts
import com.mobilesolutionworks.gradle.swift.model.extension.AthenaUploadTarget

athena {
    enabled = true

    // Where the artifact should be uploaded to
    upload = AthenaUploadTarget.MavenLocal

    // Defines organization if you choose to upload to Jfrog Bintray
    organization = "mobilesolutionworks"

    // Defines repository name for Jfrog Bintray or Artifactory
    repository = "athena"

    // Defines where the place Athena should be stored in your project [default to $rootDir/Athena]
    workDir = File("$rootDir/Athena")
}
```
- **enabled**, when set to false, Carthage will run without Athena download and upload. You can still use this to run Carthage operation as you normally would
- **upload**, when ommited, this will be defaulted to MavenLocal
  - AthenaUploadTarget.Bintray to upload to your bintray account by using Jfrog CLI
  - AthenaUploadTarget.Artifactory to upload to your Artifactory by using Jfrog CLI
  - AthenaUploadTarget.MavenLocal to upload to Maven repository in your machine (~/.m2)
- **organization**, this is required if you choose Bintray
- **repository**, this is required if you choose Bintray or Artifactory
- **workDir**, normally you would not change this

#### carthage

**github sources**

```groovy
carthage {
    github("yunarta/NullFramework") compatible "1.1.0"
    github("yunarta/NullFramework") version "1.1.0"
    github("yunarta/NullFramework") atLeast "1.1.0"
    git("https://bitbucket.org/yunarta/nullframework.git") {
        id("yunarta", "NullFramework")
    } atLeast "1.1.0"
}
```

- **compatible** is the same with "~>", **version** is the same with "==" and **atLeast** is the same with ">=" in Cartfile definitions

**git sources**

```groovy
carthage {
    git("https://bitbucket.org/yunarta/nullframework.git") {
        id("yunarta", "NullFramework")
    } atLeast "1.1.0"
}
```
- **id(group, name)** as Git URL can be in any kind for formats, you need to tell Athena what is the *group* and *module name*

#### carthage dependency

In situation where the Cartfile.resolved contains extra **git sources** brought along by your carthage sources, you need to tell Athena how to resolves them as well. Athena will tell you what are the items that you need to resolve during execution such as below.
```
Found non github source in Cartfile.
Athena need to have this resolved in order to have it uploaded
Please add this items in Athena resolutions

resolutions {
    "https://bitbucket.org/yunarta/nullframework.git" {
        group = "…"
        module = "…"
    }
}
```

Then what you need to do for this example is to tell Athena how to resolve them
```groovy
athena {
    resolutions {
        "https://bitbucket.org/yunarta/nullframework.git" {
            group = "yunarta"
            module = "NullFramework"
        }
    }
}
```
### Commands

While there are a lot of Gradle tasks created by the plugin, the most important tasks that you want to focus are

**carthageBootstrap**

Executes `carthage bootstrap` and gets all the dependencies. Successive execution of this operation will be in swift if there are no changes in the `build.gradle.kts`

**carthageUpdate**

Executes `carthage update` and to update all the dependencies. This will first check if there actually any updates based on the changes of Cartfile.resolved, and then executes

**athenaUpload**

Upload dependency to specified upload target

### Uploading to Maven
Athena will upload the Carthage outputs into Maven and tagged them with the Swift version that toolchain defines. This would allows you to have multiple version of same framework with different Swift version that you can still use in the legacy Swift 3 project for example.

#### Artifactory and MavenLocal
Format:
`[group]/[module]/[version]-Swift[version]/[module]-[version]-Swift[version].zip`

For example:
`yunarta/NullFramework/NullFramework-1.0.0-Swift4.1.2.zip`

For Artifactory upload, all the Carthage output in your project will be uploaded to the specified repository with format. Artifactory is the best choice to put all your Carthage output used in your projects.

#### Bintray
Format:
`[group]:[module]:[version]-Swift[version]/[module]-[version]-Swift[version].zip`

For example:
`yunarta:NullFramework:NullFramework-1.0.0-Swift4.1.2.zip`

For Bintray upload, you are expected to create the packages first. Bintray is *not your storage for all Carthage output* that you used in your projects.

For now this feature is experimental and still under development.

### Question and Answer

**What is the need of creating Athena just to upload to Artifactory, whereas Rome done this by uploading to S3**

When you are using Rome, you will likely will be using **Minio** for your S3 compatible bucket. And compared with Artifactory, the repository management and permission is much more sophisticated.

Secondly, your Android developer may had been using Artifactory for storing their modules.

Lastly, Gradle comes with Maven repository handler, and different plugin can be written to take advantages of this Cocoa framework repository.

### Roadmap

Now that we have a tools to simplify Carthage operation and have a Maven based repository, what comes in my mind is to write another plugin where we can recreate a Cocoapods dependency that points to this precompiled frameworks. This would give your the power of Cocoapods dependency substitution and fast compilation of Carthage
