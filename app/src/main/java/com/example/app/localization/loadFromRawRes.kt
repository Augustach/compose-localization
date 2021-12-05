package com.example.app.localization

import android.content.Context
import androidx.annotation.RawRes
import com.example.localization.ILocalization
import com.example.localization.JsonResource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.util.*

fun ILocalization.loadFromRawRes(context: Context, locale: Locale, @RawRes resId: Int) {
    val text = context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
    val json = Json.parseToJsonElement(text).jsonObject
    this.add(JsonResource(locale, json))
}