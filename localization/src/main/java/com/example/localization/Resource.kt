package com.example.localization

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

interface IResource {
    val lang: String
    fun translate(key: String, separator: String): String?
}

class JsonResource(
    override val lang: String,
    private val localization: JsonObject,
) : IResource {
    override fun translate(key: String, separator: String): String? {
        val keys = key.split(separator)
        val result = keys.take(keys.size - 1)
            .fold<String, JsonObject?>(localization) { prev, element -> prev?.get(element)?.jsonObject }
            ?: return null

        return result[keys.last()]?.jsonPrimitive?.content
    }
}

class MapResource(
    override val lang: String,
    private val translations: Map<String, String>,
): IResource {
    override fun translate(key: String, separator: String): String? {
        return translations[key]
    }
}
