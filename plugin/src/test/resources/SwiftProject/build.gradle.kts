import com.mobilesolutionworks.gradle.swift.model.CarthageSchematic

apply {
    plugin("com.mobilesolutionworks.gradle.swift")
}

fun org.gradle.api.Project.`carthage`(configure: CarthageSchematic.() -> Unit): Unit =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("carthage", configure)

carthage {
    platforms = listOf("iOS", "macOS")

//    github("RxSwift", "ReactiveX/RxSwift") { rome ->
//        rome.map("RxSwift", listOf("RxSwift", "RxBlocking", "RxTest"))
//    }
//    github("Moya", "Moya/Moya") { rome ->
//        rome.map("Moya", listOf("Moya", "RxMoya"))
//    }
    github("yunarta/NullFramework")
}

//afterEvaluate {
//    //    val carthageUpdate = tasks.create("carthageUpdate") {
////        group = "carthage"
////        description = "Run carthage update"
////    }
//
//    val createBackup = tasks.create("createBackup", Copy::class.java) {
//        from(file("$projectDir/Works-Swift"))
//        into(file("$projectDir/Works-Swift-BackUp"))
//    }
//
//    val restoreBackup = tasks.create("restoreBackup", Copy::class.java) {
//        from(file("$projectDir/Works-Swift-BackUp"))
//        into(file("$projectDir/Works-Swift"))
//    }
//
//
//    val romeCreateRepositoryMap = tasks.create("romeCreateRepositoryMap") {
//        group = "rome"
//
//        val target = file("$buildDir/works-swift/rome/localRepositoryMap.txt")
//
//        val repositoryMap = carthage.dependencies.mapNotNull {
//            val options = it.options
//            if (options.frameworks.isNotEmpty()) {
//                "${options.key} = ${options.frameworks.joinToString(", ")}"
//            } else {
//                null
//            }
//        }.joinToString(separator = System.lineSeparator())
//
//        outputs.file(target)
//        outputs.upToDateWhen {
//            if (target.exists()) {
//                target.readText() == repositoryMap
//            } else {
//                false
//            }
//        }
//
//        doFirst {
//            file(target).apply {
//                writeText(repositoryMap)
//            }
//        }
//    }
//
//    val romeCreate = tasks.create("romeCreateRomefile") {
//        group = "rome"
//
//        val target = file("$projectDir/Works-Swift/romefile")
//
//        dependsOn(romeCreateRepositoryMap.outputs.files)
//
//        outputs.file(target)
//
//        doFirst {
//            target.apply {
//                val repositoryMap = romeCreateRepositoryMap.outputs.files.singleFile.readText()
//                val romeText = """
//[Cache]
//S3-Bucket = ios-dev-bucket
//local = $buildDir/options/
//
//[RepositoryMap]
//$repositoryMap
//            """.trimIndent()
//                writeText(romeText)
//            }
//        }
//    }
//
//    val carthageCartfileCreate = tasks.create("carthageCartfileCreate") {
//        group = "carthage"
//
//        val path = "$projectDir/Works-Swift"
//
//        val target = file("$path/Cartfile")
//
//        outputs.file(target)
//        doFirst {
//            target.apply {
//                val cartfileText = carthage.dependencies.map {
//                    it.semantic
//                }.joinToString(System.lineSeparator())
//                writeText(cartfileText)
//            }
//        }
//    }
//
//    val carthageCartfileResolveUpdates = tasks.create("carthageCartfileResolveUpdates", Exec::class.java) {
//        group = "carthage"
//
//        val path = "$buildDir/works-swift/carthage/latest"
//
//        val cartfile = file("$path/Cartfile")
//        val cartfileResolved = file("$path/Cartfile.resolved")
//
//        executable = "carthage"
//        workingDir = file(path)
//
//        args(mutableListOf<Any?>().apply {
//            add("update")
//            if (carthage.platforms.isNotEmpty()) {
//                add("--platform")
//                add(carthage.platforms.joinToString(","))
//            }
//            add("--no-build")
//            add("--no-checkout")
//        })
//
//        dependsOn(carthageCartfileCreate.outputs.files)
//
//        outputs.file(cartfileResolved)
//
//        onlyIf {
//            val cartfileResolved = file("$projectDir/Works-Swift/Cartfile.resolved")
//            carthage.updates || !cartfileResolved.exists()
//        }
//
//        doFirst {
//            carthageCartfileCreate.outputs.files.singleFile.copyTo(cartfile, true)
//        }
//    }
//
//    tasks.create("carthageCartfileResolve") {
//        group = "carthage"
//
//        val path = "$projectDir/Works-Swift"
//
//        val cartfileResolved = file("$path/Cartfile.resolved")
//        val latestCartfileResolved = carthageCartfileResolveUpdates.outputs.files
//
//        dependsOn(latestCartfileResolved)
//
//        outputs.file(cartfileResolved)
//
//        onlyIf {
//            if (cartfileResolved.exists()) {
//                carthage.updates && !cartfileResolved.readText().equals(latestCartfileResolved.singleFile.readText())
//            } else {
//                true
//            }
//        }
//
//        doFirst {
//            latestCartfileResolved.singleFile.copyTo(cartfileResolved, true)
//        }
//    }
//
//    tasks.create("romeDownload", Exec::class.java) {
//        group = "rome"
//
//        val path = "$projectDir/Works-Swift"
//
//        dependsOn("romeCreateRomefile", "carthageCartfileResolve")
//
//        executable = "rome"
//        workingDir = file(path)
//
//        args(mutableListOf<Any?>().apply {
//            add("download")
//            add("--cache-prefix")
//            add("Swift_4_1")
//            if (carthage.platforms.isNotEmpty()) {
//                add("--platform")
//                add(carthage.platforms.joinToString(","))
//            }
//        })
//
//        inputs.files(romeCreate.outputs.files)
//        outputs.files(fileTree(
//                mapOf("dir" to file("$projectDir/Works-Swift/CarthageTaskDef/Build"),
//                        "include" to "*.version")
//        ))
//
//        doFirst {
//            file("$projectDir/Works-Swift/CarthageTaskDef/Build").mkdirs()
//        }
//    }
//
//    val romeListMissing = tasks.create("romeListMissing", Exec::class.java) {
//        group = "rome"
//
//        val workingDirPath = "$projectDir/Works-Swift"
//        val target = file("$buildDir/works-swift/rome/missing.txt")
//
//        dependsOn("romeDownload")
//
//        executable = "rome"
//        workingDir = file(workingDirPath)
//
//        args(mutableListOf<Any?>().apply {
//            add("list")
//            add("--missing")
//            add("--cache-prefix")
//            add("Swift_4_1")
//            if (carthage.platforms.isNotEmpty()) {
//                add("--platform")
//                add(carthage.platforms.joinToString(","))
//            }
//        })
//
//        doFirst {
//            standardOutput = file(target).outputStream()
//        }
//        outputs.file(target)
//    }
//
//    tasks.create("carthageBootstrap", Exec::class.java) {
//        group = "carthage"
//
//        val workingDirPath = "$projectDir/Works-Swift"
//        val missingFiles = file("$buildDir/works-swift/rome/missing.txt")
//
//        dependsOn("romeListMissing")
//
//        executable = "carthage"
//        workingDir = file(workingDirPath)
//
//        args(mutableListOf<Any?>().apply {
//            add("bootstrap")
//            addAll(listOf<Any?>("--project-directory", file(workingDirPath)))
//            if (carthage.platforms.isNotEmpty()) {
//                add("--platform")
//                add(carthage.platforms.map { it.toLowerCase() }.joinToString(","))
//            }
//            add("--cache-builds")
//            add("--no-use-binaries")
//        })
//
//        outputs.files(fileTree(
//                mapOf("dir" to file("$projectDir/Works-Swift/CarthageTaskDef/Build"),
//                        "include" to "*.version")
//        ))
//
//        onlyIf {
//            missingFiles.readText().isNotBlank()
//        }
//    }
//
//    tasks.create("romeUpload", Exec::class.java) {
//        group = "rome"
//
//        val path = "$projectDir/Works-Swift"
//        val missingFiles = file("$buildDir/works-swift/rome/missing.txt")
//
//        dependsOn("carthageBootstrap")
//
//        executable = "rome"
//        workingDir = file(path)
//
//        args(mutableListOf<Any?>().apply {
//            add("upload")
//            add("--cache-prefix")
//            add("Swift_4_1")
//            if (carthage.platforms.isNotEmpty()) {
//                add("--platform")
//                add(carthage.platforms.joinToString(","))
//            }
//        })
//
//        onlyIf {
//            missingFiles.readText().isNotBlank()
//        }
//
//        doLast {
//            missingFiles.deleteOnExit()
//        }
//    }
//}
//
//
//
tasks.create("clean", Delete::class.java) {
    delete("$buildDir")
    delete("$projectDir/Cartfile")
}