package com.mobilesolutionworks.gradle.swift.tasks.rome

internal object RomeTaskDef {

    const val group = "rome"

    enum class Tasks(val value: String) {
        RomeCreateRepositoryMap("romeCreateRepositoryMap"),
        RomeCreateRomefile("romeCreateRomefile"),
        RomeListMissing("romeListMissing"),
        RomeUpload("romeUpload"),
        RomeDownload("romeDownload"),
        RomeUpdate("romeUpdate"),
    }
}