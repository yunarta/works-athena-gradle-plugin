import java.nio.file.Paths

abstract class CarthageDependency(val name: String) {

    abstract val semantic: String

    val romeCache = RomeCache()
}

class CarthageGitHub(name: String, private val repo: String) : CarthageDependency(name) {

    private var versioning: String = ""

    infix fun compatible(version: String) {
        versioning = "~> $version"
    }

    infix fun version(version: String) {
        versioning = "== $version"
    }

    infix fun atLeast(version: String) {
        versioning = ">= $version"
    }

    override val semantic: String
        get() {
            return "github \"$repo\" $versioning"
        }
}


open class CarthageSchematic {

    var updates = false

    var platforms = emptyList<String>()

    private val declaredDependencies = mutableListOf<CarthageDependency>()
    private val declaredDependencies2 = mutableListOf<CarthageDependency>()

    val dependencies: List<CarthageDependency>
        get() {
            return declaredDependencies
        }

    fun github(name: String, repo: String): CarthageGitHub = CarthageGitHub(name, repo).also {
        declaredDependencies.add(it)
    }

    fun github(name: String, repo: String, rome: (RomeCache) -> Unit): CarthageGitHub = CarthageGitHub(name, repo).also {
        rome(it.romeCache)
        declaredDependencies.add(it)
    }
}

class RomeCache {

    var key: String = ""
    var frameworks = emptyList<String>()

    fun map(frameworks: List<String>) {
        this.frameworks = frameworks
    }


    fun map(key: String, frameworks: List<String>) {
        this.key = key
        this.frameworks = frameworks
    }
}

extensions.create("carthage", CarthageSchematic::class.java)
fun org.gradle.api.Project.`carthage`(configure: CarthageSchematic.() -> Unit): Unit =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("carthage", configure)

val org.gradle.api.Project.`carthage`: CarthageSchematic
    get() =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("carthage") as CarthageSchematic

carthage {
    platforms = listOf("iOS")

    github("RxSwift", "ReactiveX/RxSwift") { rome ->
        rome.map("RxSwift", listOf("RxSwift", "RxBlocking", "RxTest"))
    }
    github("Moya", "Moya/Moya") { rome ->
        rome.map("Moya", listOf("Moya", "RxMoya"))
    }
}

afterEvaluate {
    //    val carthageUpdate = tasks.create("carthageUpdate") {
//        group = "carthage"
//        description = "Run carthage update"
//    }

    val createBackup = tasks.create("createBackup", Copy::class.java) {
        from(file("$projectDir/Works-Swift"))
        into(file("$projectDir/Works-Swift-BackUp"))
    }

    val restoreBackup = tasks.create("restoreBackup", Copy::class.java) {
        from(file("$projectDir/Works-Swift-BackUp"))
        into(file("$projectDir/Works-Swift"))
    }


    val romeCreateRepositoryMap = tasks.create("romeCreateRepositoryMap") {
        group = "rome"

        val target = file("$buildDir/works-swift/rome/localRepositoryMap.txt")

        val repositoryMap = carthage.dependencies.mapNotNull {
            val romeCache = it.romeCache
            if (romeCache.frameworks.isNotEmpty()) {
                "${romeCache.key} = ${romeCache.frameworks.joinToString(", ")}"
            } else {
                null
            }
        }.joinToString(separator = System.lineSeparator())

        outputs.file(target)
        outputs.upToDateWhen {
            if (target.exists()) {
                target.readText() == repositoryMap
            } else {
                false
            }
        }

        doFirst {
            file(target).apply {
                writeText(repositoryMap)
            }
        }
    }

    val romeCreate = tasks.create("romeCreateRomefile") {
        group = "rome"

        val target = file("$projectDir/Works-Swift/romefile")

        dependsOn(romeCreateRepositoryMap.outputs.files)

        outputs.file(target)

        doFirst {
            target.apply {
                val repositoryMap = romeCreateRepositoryMap.outputs.files.singleFile.readText()
                val romeText = """
[Cache]
local = $buildDir/romeCache/

[RepositoryMap]
$repositoryMap
            """.trimIndent()
                writeText(romeText)
            }
        }
    }

    val carthageCartfileCreate = tasks.create("carthageCartfileCreate") {
        group = "carthage"

        val path = "$projectDir/Works-Swift"

        val target = file("$path/Cartfile")

        outputs.file(target)
        doFirst {
            target.apply {
                val cartfileText = carthage.dependencies.map {
                    it.semantic
                }.joinToString(System.lineSeparator())
                writeText(cartfileText)
            }
        }
    }

    val carthageCartfileResolveUpdates = tasks.create("carthageCartfileResolveUpdates", Exec::class.java) {
        group = "carthage"

        val path = "$buildDir/works-swift/carthage/latest"

        val cartfile = file("$path/Cartfile")
        val cartfileResolved = file("$path/Cartfile.resolved")

        executable = "carthage"
        workingDir = file(path)

        args(mutableListOf<Any?>().apply {
            add("update")
            if (carthage.platforms.isNotEmpty()) {
                add("--platform")
                add(carthage.platforms.joinToString(","))
            }
            add("--no-build")
            add("--no-checkout")
        })

        dependsOn(carthageCartfileCreate.outputs.files)

        outputs.file(cartfileResolved)

        onlyIf {
            val cartfileResolved = file("$projectDir/Works-Swift/Cartfile.resolved")
            carthage.updates || !cartfileResolved.exists()
        }

        doFirst {
            carthageCartfileCreate.outputs.files.singleFile.copyTo(cartfile, true)
        }
    }

    tasks.create("carthageCartfileResolve") {
        group = "carthage"

        val path = "$projectDir/Works-Swift"

        val cartfileResolved = file("$path/Cartfile.resolved")
        val latestCartfileResolved = carthageCartfileResolveUpdates.outputs.files

        dependsOn(latestCartfileResolved)

        outputs.file(cartfileResolved)

        onlyIf {
            if (cartfileResolved.exists()) {
                carthage.updates && !cartfileResolved.readText().equals(latestCartfileResolved.singleFile.readText())
            } else {
                true
            }
        }

        doFirst {
            latestCartfileResolved.singleFile.copyTo(cartfileResolved, true)
        }
    }

    tasks.create("romeDownload", Exec::class.java) {
        group = "rome"

        val path = "$projectDir/Works-Swift"

        dependsOn("romeCreateRomefile", "carthageCartfileResolve")

        executable = "rome"
        workingDir = file(path)

        args(mutableListOf<Any?>().apply {
            add("download")
            add("--cache-prefix")
            add("Swift_4_1")
            if (carthage.platforms.isNotEmpty()) {
                add("--platform")
                add(carthage.platforms.joinToString(","))
            }
        })

        inputs.files(romeCreate.outputs.files)
        outputs.files(fileTree(
                mapOf("dir" to file("$projectDir/Works-Swift/Carthage/Build"),
                        "include" to "*.version")
        ))

        doFirst {
            file("$projectDir/Works-Swift/Carthage/Build").mkdirs()
        }
    }

    val romeListMissing = tasks.create("romeListMissing", Exec::class.java) {
        group = "rome"

        val workingDirPath = "$projectDir/Works-Swift"
        val target = file("$buildDir/works-swift/rome/missing.txt")

        dependsOn("romeDownload")

        executable = "rome"
        workingDir = file(workingDirPath)

        args(mutableListOf<Any?>().apply {
            add("list")
            add("--missing")
            add("--cache-prefix")
            add("Swift_4_1")
            if (carthage.platforms.isNotEmpty()) {
                add("--platform")
                add(carthage.platforms.joinToString(","))
            }
        })

        standardOutput = file(target).outputStream()
        outputs.file(target)
    }

    tasks.create("carthageBootstrap", Exec::class.java) {
        group = "carthage"

        val workingDirPath = "$projectDir/Works-Swift"
        val missingFiles = file("$buildDir/works-swift/rome/missing.txt")

        dependsOn("romeListMissing")

        executable = "carthage"
        workingDir = file(workingDirPath)

        args(mutableListOf<Any?>().apply {
            add("bootstrap")
            addAll(listOf<Any?>("--project-directory", file(workingDirPath)))
            if (carthage.platforms.isNotEmpty()) {
                add("--platform")
                add(carthage.platforms.map { it.toLowerCase() }.joinToString(","))
            }
            add("--cache-builds")
            add("--no-use-binaries")
        })

        onlyIf {
            missingFiles.readText().isNotBlank()
        }
    }

    tasks.create("romeUpload", Exec::class.java) {
        group = "rome"

        val path = "$projectDir/Works-Swift"
        val missingFiles = file("$buildDir/works-swift/rome/missing.txt")

        dependsOn("carthageBootstrap")

        executable = "rome"
        workingDir = file(path)

        args(mutableListOf<Any?>().apply {
            add("upload")
            add("--cache-prefix")
            add("Swift_4_1")
            if (carthage.platforms.isNotEmpty()) {
                add("--platform")
                add(carthage.platforms.joinToString(","))
            }
        })

        onlyIf {
            missingFiles.readText().isNotBlank()
        }

        doLast {
            missingFiles.deleteOnExit()
        }
    }

//    tasks.create("carthageResolve", Exec::class.java) {
//
//
//
//        group = "carthage"
//        inputs.file(romeCreate.outputs.files)
//        outputs.file(file("$path/Cartfile.resolved"))
//
//        executable = "carthage"
//        workingDir = file(path)
//
//        args(mutableListOf<Any?>().apply {
//            add("bootstrap")
//            if (carthage.platforms.isNotEmpty()) {
//                add("--platform")
//                add(carthage.platforms.joinToString(","))
//            }
//            add("--no-build")
//            add("--no-checkout")
//        })
//
//        doFirst {
//            file(path).apply {
//                mkdirs()
//                File(this, "Cartfile").writeText(it.semantic)
//            }
//        }
//    }
//
//    carthage.dependencies.forEach {
//
//
//
//        tasks.create("carthageUpdate${it.name}", Exec::class.java) {
//            val path = "$buildDir/works-swift/${it.name}"
//
//            group = "carthage"
//            description = "Run carthage update for ${it.name}"
//
//            dependsOn("carthageResolve${it.name}")
//            carthageUpdate.dependsOn(this)
//
//            outputs.dir(file("$buildDir/${it.name}/Carthage"))
//            inputs.file("$buildDir/${it.name}/Carthage.resolved")
//
//            executable = "carthage"
//            workingDir = file(path)
//
//            args(mutableListOf<Any?>().apply {
//                add("update")
//                addAll(listOf<Any?>("--project-directory", file(path)))
//                if (carthage.platforms.isNotEmpty()) {
//                    add("--platform")
//                    add(carthage.platforms.joinToString(","))
//                }
//            })
//        }
//
//        val romeDownload = tasks.create("romeDownload${it.name}", Exec::class.java) {
//            val path = "$buildDir/works-swift/${it.name}"
//
//            group = "rome"
//            dependsOn("carthageResolve${it.name}")
//
//            inputs.file(romeCreate.outputs.files)
//            outputs.files(fileTree(
//                    mapOf("dir" to file("$buildDir/works-swift/${it.name}/Carthage/Build"),
//                            "include" to "*.version")
//            ))
//
//            executable = "rome"
//            workingDir = file(path)
//            args(mutableListOf<Any?>().apply {
//                add("download")
//                if (carthage.platforms.isNotEmpty()) {
//                    add("--platform")
//                    add(carthage.platforms.joinToString(","))
//                }
//            })
//
//            doFirst {
//                file(path).apply {
//                    mkdirs()
//
//                    val singleFile = romeCreate.outputs.files.singleFile
//                    File(this, "romefile").delete()
//                    singleFile.copyTo(File(this, "romefile"))
//                }
//            }
//        }
//
//        tasks.create("carthageBootstrap${it.name}", Exec::class.java) {
//            val path = "$buildDir/works-swift/${it.name}"
//
//            group = "carthage"
//            description = "Run carthage update for ${it.name}"
//
//            dependsOn("carthageResolve${it.name}", romeDownload.outputs.files)
//            carthageUpdate.dependsOn(this)
////            outputs.files(fileTree(
////                    mapOf("dir" to file("$buildDir/works-swift/${it.name}/Carthage/Build"),
////                            "include" to "*.version")
////            ))
//
//            executable = "carthage"
//            workingDir = file(path)
//
//            args(mutableListOf<Any?>().apply {
//                add("bootstrap")
//                addAll(listOf<Any?>("--project-directory", file(path)))
//                if (carthage.platforms.isNotEmpty()) {
//                    add("--platform")
//                    add(carthage.platforms.joinToString(","))
//                }
//            })
//        }
//
//    }
}



tasks.create("clean", Delete::class.java) {
    delete("$buildDir")
}