package com.example.localization

class Localization(
    defaultResource: IResource,
    private val keySeparator: String = ".",
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

        override fun t(key: String, arg: Any): String {
            return plural(key, arg).format(arg)
        }

        override fun t(key: String, args: Map<String, Any>): String {
            return t(key).format(args)
        }

        override fun t(key: String, args: Map<String, Any>, plural: String?): String {
            return plural(key, args[plural]).format(args)
        }

        private fun plural(key: String, pluralSuffix: String): String {
            val resource = resources[lang]
            return resource?.plural(key, keySeparator, pluralSuffix)
                ?: resources[defaultLanguage]?.plural(key, keySeparator, pluralSuffix) ?: key
        }

        private fun plural(key: String, quantity: Any?): String {
            return when (quantity) {
                null -> t(key)
                is Int -> {
                    val pluralSuffix = plurals.getPlural(lang, quantity).category
                    plural(key, pluralSuffix)
                }
                is Double -> {
                    val pluralSuffix = plurals.getPlural(lang, quantity).category
                    plural(key, pluralSuffix)
                }
                else -> t(key)
            }
        }
    }

    override fun add(vararg resource: IResource): ILocalization {
        resource.forEach {
            resources[it.lang] = resources[it.lang]?.merge(it) ?: it
        }
        return this
    }

    override fun get() = Translator(defaultLanguage)

    override fun get(lang: String) = Translator(lang)
}