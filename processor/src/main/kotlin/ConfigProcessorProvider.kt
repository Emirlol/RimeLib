package me.ancientri.symbols.config

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class ConfigProcessorProvider : SymbolProcessorProvider {
	override fun create(environment: SymbolProcessorEnvironment) = ConfigProcessor(environment)
}