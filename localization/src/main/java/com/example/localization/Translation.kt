package com.example.localization

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.EXPRESSION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Translation(
    val lang: String,
    val path: String,
    val main: Boolean = false,
    val separator: String = ".",
)