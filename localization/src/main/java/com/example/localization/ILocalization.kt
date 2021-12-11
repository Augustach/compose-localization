package com.example.localization

interface ITranslator {
    fun t(key: String): String
    fun t(key: String, vararg args: Any?): String
    fun t(key: String, vararg args: Any?, pluralIndex: Int = 0): String
}

interface ILocalization {
    fun add(vararg resource: IResource): ILocalization
    fun get(): ITranslator
    fun get(lang: String): ITranslator
}