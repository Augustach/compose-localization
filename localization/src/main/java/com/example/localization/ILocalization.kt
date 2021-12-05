package com.example.localization

import java.util.*

interface ITranslator {
    fun t(key: String): String
    fun t(key: String, vararg args: Any?): String
    fun t(key: String, vararg args: Any?, pluralIndex: Int = 0): String
}

interface ILocalization {
    fun add(vararg resource: IResource): ILocalization
    fun get(): ITranslator
    fun get(locale: Locale): ITranslator
}