package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.localization_compose.LocalizationProvider
import com.example.localization.Localization
import com.example.app.localization.GeneratedEnResource
import com.example.app.localization.GeneratedRuResource
import com.example.localization_compose.t
import java.util.*

private val RuLocale = Locale("ru")

@Composable
fun Content() {
    val (locale, setLocale) = remember { mutableStateOf(RuLocale) }
    val localization = remember { Localization(GeneratedRuResource).add(GeneratedEnResource) }
    LocalizationProvider(localization, lang = locale.language) {
        Column() {
            Text("current = ${locale.language}")
            Row {
                Button(onClick = { setLocale(RuLocale) }) {
                    Text(RuLocale.language)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { setLocale(Locale.ENGLISH) }) {
                    Text(Locale.ENGLISH.language)
                }
            }
            Text(text = t("level1.level2.title"))
            Text(text = t("level1.level2.nutritionValues", 100))
            Text(text = t("level1.level2.key2", mapOf("1" to 1000.99, "2" to 100999.999)))
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 0, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 1, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 2, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 3, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 4, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 5, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 6, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 7, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 8, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 9, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 10, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t(
                    "level1.level2.reviewsCount",
                    mapOf("count" to 21, "retailerName" to "Магазин"),
                    plural = "count"
                )
            )
            Text(
                text = t("level1.items_count", 1)
            )
            Text(
                text = t("level1.items_count", 2)
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }
}
