package com.example.localization

import kotlinx.serialization.json.JsonObject

private const val PLURAL_SEPARATOR = "_"

interface IResource {
    val lang: String
    val separator: String

    fun translate(key: String, separator: String): String?

    fun plural(key: String, separator: String, pluralSuffix: String): String? {
        return translate("$key$PLURAL_SEPARATOR$pluralSuffix", separator) ?: translate(
            key,
            separator
        )
    }

    fun merge(other: IResource): IResource

    fun forEach(action: (key: String, value: String) -> Unit)
}

private fun jsonToMap(json: JsonObject, separator: String) = mutableMapOf<String, String>().also {
    json.forEach(null, separator) { key, value ->
        it[key] = value
    }
}

class JsonResource(
    override val lang: String,
    localization: JsonObject,
) : IResource {
    override val separator = MapResource.SEPARATOR

    private val resource = MapResource(lang, jsonToMap(localization, separator))

    override fun translate(key: String, separator: String) = resource.translate(key, separator)

    override fun merge(other: IResource) = resource.merge(other)

    override fun forEach(action: (key: String, value: String) -> Unit) = resource.forEach(action)
}

class MapResource(
    override val lang: String,
    private val translations: Map<String, String>,
) : IResource {

    companion object {
        const val SEPARATOR = "."
    }

    override val separator = SEPARATOR

    override fun translate(key: String, separator: String): String? {
        return translations[key]
    }

    override fun merge(other: IResource): IResource {
        val copiedTranslations = translations.toMutableMap()
        other.forEach { key, value ->
            val correctKey = if (other.separator !== separator) {
                key.split(other.separator).joinToString(separator)
            } else {
                separator
            }
            copiedTranslations[correctKey] = value
        }
        return MapResource(other.lang, copiedTranslations)
    }

    override fun forEach(action: (key: String, value: String) -> Unit) {
        for ((key, value) in translations) {
            action(key, value)
        }
    }
}
