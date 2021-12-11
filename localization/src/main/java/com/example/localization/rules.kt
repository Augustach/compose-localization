package com.example.localization

// ref https://unicode-org.github.io/cldr-staging/charts/37/supplemental/language_plural_rules.html
internal val pluralRules = mutableMapOf(
    "ru" to Rule(
        one = { _: Double, i: Long, _: Long, v: Int -> v == 0 && i % 10 == 1L && i % 100 != 11L },
        few = { _: Double, i: Long, _: Long, v: Int ->
            v == 0 && i % 10 in 2L..4L && i % 100 !in 12L..14L
        },
        many = { _: Double, i: Long, _: Long, v: Int ->
            v == 0 && i % 10 == 0L || v == 0 && i % 10 in 5L..9L || v == 0 && i % 100 in 11L..14L
        },
    ),
    "en" to Rule(
        one = { _: Double, i: Long, _: Long, v: Int -> i == 1L && v == 0 },
    )
)