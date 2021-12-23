package com.example.localization

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.io.File
import kotlin.io.path.Path

private val PLURAl_SUFFIX = listOf("_zero", "_one", "_few", "_many", "_other")

private fun fillTranslations(
    json: JsonObject,
    separator: String,
) = StringBuilder("").also {
    json.forEach(null, separator) { key, value ->
        it.append("        \"${key}\" to \"${value}\",\n")
    }
}.toString()

private fun mapKeys(json: JsonObject, separator: String) = mutableMapOf<String, String>().also {
    json.forEach(null, separator) { key, value ->
        val suffix = PLURAl_SUFFIX.find { key.endsWith(it) } ?: ""
        val tKey = key.removeSuffix(suffix)
        it[tKey.split(separator).joinToString("_")] = tKey
    }
}

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
            val main = arguments.first { args -> args.name?.asString() == "main" }.value as Boolean
            val separator =
                arguments.first { args -> args.name?.asString() == "separator" }.value as String
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
                |       ${fillTranslations(json, separator)}
                |   )
                |
                |val $className = MapResource("$lang", translations)
            """.trimMargin()

            codeGenerator.createNewFile(
                Dependencies(true, classDeclaration.containingFile!!),
                packageName,
                className
            ).writer().append(content).close()

            if (main) {
                codeGenerator.createNewFile(
                    Dependencies(true, classDeclaration.containingFile!!),
                    packageName,
                    "T"
                ).writer().append(
                    """
                |package $packageName
                |
                |object T {
                |   ${
                        mapKeys(json, separator).map { (key, value) -> "val $key = \"$value\"" }
                            .joinToString("\n")
                    }
                |}
            """.trimMargin()
                ).close()
            }
        }
    }
}

internal class LocalizationProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return LocalizationProcessor(
            environment.codeGenerator,
            environment.options
        )
    }
}