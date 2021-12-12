package com.example.localization

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import java.io.File
import kotlin.io.path.Path

internal class LocalizationProcessor(
    val codeGenerator: CodeGenerator,
    val options: Map<String, String>
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Translation::class.qualifiedName!!)
        val ret = symbols.filter { !it.validate() }.toList()
        symbols.filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(Visitor(), Unit) }
        return ret
    }

    inner class Visitor() : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val arguments = classDeclaration.annotations.first {
                it.shortName.asString() == Translation::class.simpleName
            }.arguments
            val lang = arguments.first { args -> args.name?.asString() == "lang" }.value
            val path: String =
                arguments.first { args -> args.name?.asString() == "path" }.value as String
            val file = File(Path(options["translationsDir"]!!, path).toString())
            val json = Json.parseToJsonElement(file.readText()).jsonObject
            val packageName = classDeclaration.containingFile!!.packageName.asString()
            val className = "Generated${classDeclaration.simpleName.asString()}"
            val content = """
                |package $packageName
                |
                |import com.example.localization.MapResource
                |
                |private val translations = mutableMapOf<String, String>(
                |       ${fillTranslations(json)}
                |   )
                |
                |val $className = MapResource("$lang", translations)
            """.trimMargin()

            codeGenerator.createNewFile(
                Dependencies(true, classDeclaration.containingFile!!),
                packageName,
                className
            ).writer().append(content).close()
        }
    }
}

@Suppress("NewApi")
private fun fillTranslations(
    json: JsonObject,
    prefix: String? = null,
    builder: StringBuilder = StringBuilder("")
): String {
    val space = "        "
    json.entries.forEach { (_key, value) ->
        val key = prefix?.plus(".${_key}") ?: _key
        when (value) {
            is JsonObject -> fillTranslations(value, key, builder)
            is JsonPrimitive -> builder.append("$space\"${key}\" to ${value},\n")
            else -> {}
        }
    }
    return builder.toString()
}

internal class LocalizationProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return LocalizationProcessor(
            environment.codeGenerator,
            environment.options
        )
    }
}