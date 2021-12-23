package com.example.localization

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal fun JsonObject.forEach(
    prefix: String?,
    separator: String,
    action: (key: String, value: String) -> Unit
) {
    for ((key, value) in this) {
        val nextKey = prefix?.plus("$separator$key") ?: key
        when (value) {
            is JsonPrimitive -> {
                action(nextKey, value.content)
            }
            is JsonObject -> value.forEach(nextKey, separator, action)
            else -> {}
        }
    }
}