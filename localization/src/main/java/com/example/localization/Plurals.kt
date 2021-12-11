package com.example.localization

import kotlin.math.absoluteValue

/**
 * ref: http://unicode.org/reports/tr35/tr35-numbers.html#Language_Plural_Rules
 * Symbol	Value
 * n	    absolute value of the source number.
 * i	    integer digits of n.
 * v	    number of visible fraction digits in n, with trailing zeros.*
 * w    	number of visible fraction digits in n, without trailing zeros.*
 * f	    visible fraction digits in n, with trailing zeros.*
 * t	    visible fraction digits in n, without trailing zeros.*
 * c	    compact decimal exponent value: exponent of the power of 10 used in compact decimal formatting.
 * e	    currently, synonym for ‘c’. however, may be redefined in the future.
 */
class Rule(
    val zero: (n: Double, i: Long, f: Long, v: Int) -> Boolean = { _, _, _, _ -> false },
    val one: (n: Double, i: Long, f: Long, v: Int) -> Boolean = { _, _, _, _ -> false },
    val two: (n: Double, i: Long, f: Long, v: Int) -> Boolean = { _, _, _, _ -> false },
    val few: (n: Double, i: Long, f: Long, v: Int) -> Boolean = { _, _, _, _ -> false },
    val many: (n: Double, i: Long, f: Long, v: Int) -> Boolean = { _, _, _, _ -> false }
)

internal enum class EPluralCategory(val category: String) {
    ZERO("zero"),
    TWO("two"),
    ONE("one"),
    FEW("few"),
    MANY("many"),
    OTHER("other"),
}

internal class Plurals {
    private val plurals: MutableMap<String, Rule> = mutableMapOf()

    init {
        plurals.putAll(pluralRules)
    }

    fun addRule(lang: String, rule: Rule) {
        plurals[lang] = rule
    }

    fun getPlural(lang: String, quantity: Int): EPluralCategory {
        return getPlural(lang, quantity.toDouble())
    }

    fun getPlural(lang: String, quantity: Double): EPluralCategory {
        val rule = plurals[lang] ?: return EPluralCategory.OTHER
        val absQuantity = quantity.absoluteValue
        val (int, frac) = absQuantity.toString().split('.')
        val integerPart = int.toLong()
        val fractionPart = frac.trimStart('0').ifEmpty { "0" }.toLong()
        val fractionPartDigitCount = frac.trimEnd('0').count()
        return when {
            rule.zero(absQuantity, integerPart, fractionPart, fractionPartDigitCount) -> EPluralCategory.ZERO
            rule.one(absQuantity, integerPart, fractionPart, fractionPartDigitCount) -> EPluralCategory.ONE
            rule.two(absQuantity, integerPart, fractionPart, fractionPartDigitCount) -> EPluralCategory.TWO
            rule.few(absQuantity, integerPart, fractionPart, fractionPartDigitCount) -> EPluralCategory.FEW
            rule.many(absQuantity, integerPart, fractionPart, fractionPartDigitCount) -> EPluralCategory.MANY
            else -> EPluralCategory.OTHER
        }
    }
}

