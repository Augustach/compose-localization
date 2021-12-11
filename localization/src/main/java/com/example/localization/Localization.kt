package com.example.localization

class Localization(
    defaultResource: IResource,
    private val keySeparator: String = ".",
    private val pluralSeparator: String = "_",
) : ILocalization {
    private val plurals = Plurals()
    private val resources = mutableMapOf(
        defaultResource.lang to defaultResource
    )
    private val defaultLanguage = defaultResource.lang

    inner class Translator(private val lang: String) : ITranslator {
        override fun t(key: String): String {
            val resource = resources[lang]
            return resource?.translate(key, keySeparator)
                ?: resources[defaultLanguage]?.translate(key, keySeparator) ?: key
        }

        override fun t(key: String, vararg args: Any?): String {
            return t(key).format(*args)
        }

        override fun t(key: String, vararg args: Any?, pluralIndex: Int): String {
            return when (val quantity = args[pluralIndex]) {
                null -> t(key, *args)
                is Int -> {
                    val pluralSuffix = plurals.getPlural(lang, quantity).category
                    t("$key$pluralSeparator$pluralSuffix", *args)
                }
                is Double -> {
                    val pluralSuffix = plurals.getPlural(lang, quantity).category
                    t("$key$pluralSeparator$pluralSuffix", *args)
                }
                else -> t(key, *args)
            }
        }
    }

    fun addRule(lang: String, rule: Rule) = plurals.addRule(lang, rule)

    override fun add(vararg resource: IResource): ILocalization {
        resource.forEach { resources[it.lang] = it }
        return this
    }

    override fun get(): ITranslator {
        return Translator(defaultLanguage)
    }

    override fun get(lang: String): ITranslator {
        return Translator(lang)
    }
}