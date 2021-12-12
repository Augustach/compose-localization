package com.example.localization

private val REGEX = "\\{\\{?.+?\\}\\}".toRegex()

internal fun String.format(arg: Any) = this.replace(REGEX, arg.toString())

internal fun String.format(args: Map<String, Any>) = this.replace(REGEX) { result ->
    args[result.value.substring(
        2,
        result.value.length - 2
    )].toString()
}