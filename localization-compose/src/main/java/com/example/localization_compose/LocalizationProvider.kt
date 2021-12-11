package com.example.localization_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.example.localization.ILocalization
import com.example.localization.ITranslator
import java.util.*

val LocalLocalization = compositionLocalOf<ITranslator> { error("Not Implemented") }

@Composable
fun t(key: String) = LocalLocalization.current.t(key)

@Composable
fun t(key: String, vararg args: Any?) = LocalLocalization.current.t(key, *args)

@Composable
fun t(key: String, vararg args: Any?, pluralIndex: Int = 0) = LocalLocalization.current.t(key, *args, pluralIndex = pluralIndex)

@Composable
fun LocalizationProvider(localization: ILocalization, locale: Locale, content: @Composable () -> Unit) {
    val translator = remember(locale) { localization.get(locale) }
    CompositionLocalProvider(
        LocalLocalization provides translator
    ) {
        content()
    }
}