package me.ancientri.symbols.config

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import org.intellij.lang.annotations.Language

class ConfigProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
	private var ksFile: KSFile? = null
	private var classes = mutableListOf<KSClassDeclaration>()

	override fun process(resolver: Resolver): List<KSAnnotated> = with(environment) {
		val symbols = resolver.getSymbolsWithAnnotation(Config::class.qualifiedName!!)

		for (symbol in symbols) {
			if (symbol is KSClassDeclaration) {
				if (symbol.classKind == ClassKind.CLASS) {
					if (symbol.getVisibility() == Visibility.PUBLIC) {
						if (Modifier.DATA in symbol.modifiers) {
							if (symbol.primaryConstructor != null) {
								if (symbol.primaryConstructor!!.getVisibility() == Visibility.PUBLIC) {
									if (symbol.primaryConstructor!!.parameters.isNotEmpty()) {
										if (symbol.primaryConstructor!!.parameters.all(KSValueParameter::isVal)) {
											classes += symbol
										} else logger.error("Expected all parameters of the primary constructor of class annotated with @Config to be val, found vars: ${symbol.primaryConstructor!!.parameters.filter(KSValueParameter::isVar).joinToString { it.name?.asString().orEmpty() }}", symbol.primaryConstructor!!)
									} else logger.error("Expected primary constructor of class annotated with @Config to have at least one parameter, found none", symbol.primaryConstructor!!)
								} else logger.error("Expected public visibility for primary constructor of class annotated with @Config, found: ${symbol.primaryConstructor!!.getVisibility()}", symbol.primaryConstructor!!)
							} else logger.error("Expected primary constructor for data class annotated with @Config", symbol)
						} else logger.error("Expected symbol annotated with @Config to be a data class, found: ${symbol.modifiers}", symbol)
					} else logger.error("Expected public visibility for class annotated with @Config, found: ${symbol.getVisibility()}", symbol)
				} else logger.error("Expected classKind ${ClassKind.CLASS} on symbol annoted with @Config, found: ${symbol.classKind}", symbol)
			} else logger.error("Expected KSClassDeclaration on symbol annoted with @Config, found: ${symbol::class.simpleName}", symbol)
		}

		logger.info("Found ${classes.size} classes to generate builders for.")

		ksFile = resolver.getNewFiles().firstOrNull() // This file is different for each project using this processor, so we can't just get it by name.

		return emptyList()
	}

	override fun finish(): Unit = with(environment) {
		when {
			ksFile == null -> {
				logger.warn("No new files were found, skipping generating config builders.")
				return
			}

			classes.isEmpty() -> {
				logger.warn("No config builders to generate.") // Could be a normal case, so it's a warning.
				return
			}
		}

		for (configClass in classes) {
			logger.info("Generating builder for class: ${configClass.qualifiedName?.asString()}")
			generateBuilderFile(codeGenerator, configClass)
		}
	}

	private fun generateBuilderFile(codeGenerator: CodeGenerator, configClass: KSClassDeclaration) {
		val generatedClassName = configClass.simpleName.getShortName() + "Builder"
		val classQualifiedName = configClass.qualifiedName?.asString()
			?: error("Config class ${configClass.simpleName.asString()} does not have a qualified name, cannot generate builder file.")
		codeGenerator
			.createNewFile(Dependencies(false, ksFile!!), configClass.packageName.asString(), generatedClassName)
			.bufferedWriter()
			.use { writer ->
				writer.append(
					syntaxHighlighter(
						// region Generated Code
						// @formatter:off

						"""
package ${configClass.packageName.asString()}

inline fun ${classQualifiedName}.builder(builder: $generatedClassName.() -> Unit): $generatedClassName = $generatedClassName(this).apply(builder).build()

class $generatedClassName(config: ${classQualifiedName}) : $BUILDER_QUALIFIED_NAME<$classQualifiedName> {

${configClass.primaryConstructor!!.parameters.joinToString(separator = "\n") { param ->
	val type = param.type.resolve()
	// Expected format: `val <name>: <type><nullability if applicable>`
	val nullability = if (type.isMarkedNullable) "?" else ""
	"\tvar ${param.name?.asString()}: ${type.declaration.qualifiedName?.asString()}$nullability = config.${param.name?.asString()}" 
}}

	override fun build(): $classQualifiedName = ${classQualifiedName}(
		${configClass.primaryConstructor!!.parameters.joinToString(separator = ",\n\t\t") { param ->
			param.name?.asString() ?: error("Parameter name is null in primary constructor of $classQualifiedName")
		}}
	)
}
"""
						// @formatter:on
						// endregion Generated Code
					)
				)
			}
	}

	companion object {
		const val BUILDER_QUALIFIED_NAME = "me.ancientri.rimelib.config.api.ConfigBuilder"

		/**
		 * A simple syntax highlighter for Kotlin code, using the IntelliJ IDEA's language injection feature.
		 *
		 * I don't know why this can't be applied to string literals directly, so this is a workaround.
		 */
		@Suppress("NOTHING_TO_INLINE")
		inline fun syntaxHighlighter(@Language("kotlin") code: String) = code.trimIndent()
	}
}