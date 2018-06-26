package com.mobilesolutionworks.gradle.swift.i18n

import java.util.*

object Strings {

    private val bundle = ResourceBundle.getBundle("Strings")

    operator fun get(key: String): String = bundle.getString(key)
}