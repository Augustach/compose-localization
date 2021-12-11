package com.example.localization

import java.util.*

class Localization(
    defaultResource: IResource,
    private val keySeparator: String = ".",
    private val pluralSeparator: String = "_",
) : ILocalization {
    private val plurals = Plurals()
    private val resources = mutableMapOf(
        defaultResource.locale to defaultResource
    )
    private val defaultLocale = defaultResource.locale

    inner class Translator(private val locale: Locale) : ITranslator {
        override fun t(key: String): String {
            val resource = resources[locale]
            return resource?.translate(key, keySeparator)
                ?: resources[defaultLocale]?.translate(key, keySeparator) ?: key
        }

        override fun t(key: String, vararg args: Any?): String {
            return t(key).format(*args)
        }

        override fun t(key: String, vararg args: Any?, pluralIndex: Int): String {
            return when (val quantity = args[pluralIndex]) {
                null -> t(key, *args)
                is Int -> {
                    val pluralSuffix = plurals.getPlural(locale, quantity).category
                    t("$key$pluralSeparator$pluralSuffix", *args)
                }
                is Double -> {
                    val pluralSuffix = plurals.getPlural(locale, quantity).category
                    t("$key$pluralSeparator$pluralSuffix", *args)
                }
                else -> t(key, *args)
            }
        }
    }

    fun addRule(locale: Locale, rule: Rule) = plurals.addRule(locale, rule)

    override fun add(vararg resource: IResource): ILocalization {
        resource.forEach { resources[it.locale] = it }
        return this
    }

    override fun get(): ITranslator {
        return Translator(defaultLocale)
    }

    override fun get(locale: Locale): ITranslator {
        return Translator(locale)
    }
}