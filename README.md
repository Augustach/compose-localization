# compose-localization

Localization for jetpack-compose via CompositionLocalProvider.
Thank to CompositionLocalProvider localization can be changed dynamically in runtime.

For speed up runtime it uses ksp to generate resource-files from localization json during compilation.
But also localization resources can be loaded in runtime from network, cache, file system.

For example extension for loading resources from assets
```kotlin
fun ILocalization.loadFromRawRes(context: Context, locale: Locale, @RawRes resId: Int) {
    val text = context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
    val json = Json.parseToJsonElement(text).jsonObject
    this.add(JsonResource(locale, json))
}
```

Setup:
1. In your app module set `projectDir`
```
ksp {
    arg("translationsDir", "${projectDir}")
}
```
2. add `id 'com.google.devtools.ksp'` 
3. add your localization resources in assets or any other res folder [example](https://github.com/Augustach/compose-localization/blob/master/app/src/main/assets/ru.json)

Example of localization file:
```json
{
  "level1": {
    "level2": {
      "title": "Пора обновить приложение",
      "nutritionValues": "Пищевая ценность на %1d г",
      "key2": "Больше %1f кг, но меньше %2f кг",
      "reviewsCount_one": "%1d оценка — %2s",
      "reviewsCount_few": "%1d оценки — %2s",
      "reviewsCount_many": "%1d оценок —  %2s",
      "reviewsCount_other": "%1d оценок —  %2s"
    }
  }
}
```

4. annotate your localization classes Translation annotation.
This annotation will generate localization classes with Locale and map of strings according passed lang and json
```kotlin
@Translation(lang = "ru", path = "src/main/assets/ru.json")
class RuResource

@Translation(lang = "en", path = "src/main/assets/en.json")
class EnResource
```

5. create localization instance
```kotlin
val localization = Localization(GeneratedRuResource) // GeneratedRuResource - generated class by @Translation

// resources can be added later
localization.add(GeneratedEnResource)
```

You can get translation via `t` method
```kotlin
val traslatedkey = localization.get().t("level1.level2.title") // -> "Пора обновить приложение"
val traslatedkey = localization.get(Locale.ENGLISH).t("level1.level2.title") // -> "It is time to update the app"
```

It also supports string formatting
```kotlin
localization.get().t("level1.level2.nutritionValues", 100) // -> "Пищевая ценность на 100 г"
localization.get().t("level1.level2.key2", 1000.00, 9999.99) // -> "Больше 1000,00 кг, но меньше 9999,99 кг"
```

Pluralism also works
```
localization.get(Locale.ENGLISH).t("level1.level2.reviewsCount", 0, "Shop1", pluralIndex = 0) // -> "0 reviews - Shop1"
localization.get(Locale.ENGLISH).t("level1.level2.reviewsCount", 1, "Shop1", pluralIndex = 0) // -> "1 review - Shop1"
localization.get(Locale.ENGLISH).t("level1.level2.reviewsCount", 5, "Shop1", pluralIndex = 0) // -> "5 reviews - Shop1"
```

It can be used in jetpack-compose
```kotlin
import com.example.localization_compose.LocalizationProvider
import com.example.localization.Localization
import com.example.localization_compose.t

private val RuLocale = Locale("ru")

@Composable
fun Content() {
    val (locale, setLocale) = remember { mutableStateOf(RuLocale) }
    val localization = remember { Localization(GeneratedRuResource).add(GeneratedEnResource) }
    LocalizationProvider(localization, locale) {
        Column() {
            Text(text = t("level1.level2.title"))
            Text(text = t("level1.level2.nutritionValues", 100))
            Text(text = t("level1.level2.key2", 1000.99, 100999.999))
            Text(text = t("level1.level2.reviewsCount", 0, "Магазин", pluralIndex = 0))
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
```
