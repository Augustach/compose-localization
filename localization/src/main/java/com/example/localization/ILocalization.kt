package com.example.localization

interface ITranslator {
    fun t(key: String): String
    fun t(key: String, arg: Any): String
    fun t(key: String, args: Map<String, Any>): String
    fun t(key: String, args: Map<String, Any>, plural: String? = null): String
}

interface ILocalization {
    fun add(vararg resource: IResource): ILocalization
    fun get(): ITranslator
    fun get(lang: String): ITranslator
}