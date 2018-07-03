# Works presents Athena

[![Build Status](http://jenkins.mobilesolutionworks.com:8080/job/github/job/yunarta/job/works-athena-gradle-plugin/job/master/badge/icon)](http://jenkins.mobilesolutionworks.com:8080/job/github/job/yunarta/job/works-athena-gradle-plugin/job/master/)
[![codecov](https://codecov.io/gh/yunarta/works-athena-gradle-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/yunarta/works-athena-gradle-plugin)
[![gradle plugin](https://img.shields.io/badge/gradle_plugin-1.0.5-blue.svg?style=flat-square)](https://plugins.gradle.org/plugin/com.mobilesolutionworks.gradle.athena)

## Preface

In Cocoa language development which now is dominated with development for iOS platform, there are two famous dependency tools that you probably used in your development or at least heard.

### Cocoapods

https://cocoapods.org/

Cocoapods is still my favorite dependency tools for iOS development, however as my projects is getting bigger, compiling and archiving for release now take more than 20 minutes to finish.

### Carthage

https://github.com/Carthage/Carthage

Carthage offers different way include libraries by linking to their Framework directly rather than by source like Cocoapods. As the result compilation will be significantly faster.

**Rome**

https://github.com/blender/Rome

Rome is a caching tools for Carthage, basically this tools will upload outputs of Carthage, either to your local path or in to Amazon S3 compatible repository.  

### Intention

For now I will exclude Cocoapods usage in this plugin for a while, and focus on how we can do with Carthage to make it even better.

Below are point of view of Carthage and Rome with their current state
- There are nothing much wrong with current version of Carthage. It can download precompiled frameworks if the library writer provides then in Github. The dependency discovery works as expected as well.
- However when the Swift version that you are using is not the same with the precompiled frameworks, or the library writer does not upload the precompile framework in Github, then what Carthage do is basically the same like what you will do by downloading the source and compiles them with your Swift toolchains.
- Then comes Rome to help you with this. What Rome offers is that it allows you to download the precompiled frameworks from different source; they even have a cache prefix for you to use in case you wanted Rome to download frameworks prefixed with Swift 4.1.2 for example.
- While Rome works great, but Rome S3 repository is most likely to be private for each developer. And Rome stated that you need to write the framework mapping your self if one repository produces multiple frameworks.
- Lastly, for both Carthage and Rome, I do find the learning curve on how to use them in CI/CD flow can be trivial as you need to know, should i do `carthage update` or `carthage bootstrap` instead.
- Rome give you a CI workflow example which basically downloads the frameworks, and if everything is there, then skip the `carthage update` operation. This most likely will fail if the Swift toolchains in the CI machine is updated as it will skip `carthage update`
- In nutshell, Carthage and Rome works well in developer machine with a bit of learning, but when you actually automating them on CI machine, you need to do more heavy works to make sure it works as expected

## Athena

First of all, Athena is a Gradle plugin. And yes! even you are iOS developer, Gradle can help you solve things.

Athena helps you to solve at least two things
1. Writes everything you need to configure how Carthage works in a structured platform
2. Download and upload the frameworks produced by Carthage into Maven repository

Typical ```build.gradle.kts``` structure will be like this
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
