package com.example.localization_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.example.localization.ILocalization
import com.example.localization.ITranslator

val LocalLocalization = compositionLocalOf<ITranslator> { error("Not Implemented") }

@Composable
fun t(key: String) = LocalLocalization.current.t(key)

@Composable
fun t(key: String, args: Map<String, Any>, plural: String? = null) = LocalLocalization.current.t(key, args, plural = plural)

@Composable
fun LocalizationProvider(localization: ILocalization, lang: String, content: @Composable () -> Unit) {
    val translator = remember(lang) { localization.get(lang) }
    CompositionLocalProvider(
        LocalLocalization provides translator
    ) {
        content()
    }
}