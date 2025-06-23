package me.ancientri.rimelib.config.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import me.ancientri.rimelib.config.ConfigBuilder
import me.ancientri.rimelib.config.ConfigManager

/**
 * A config manager that uses `kotlinx.serialization` for serialization and deserialization.
 */
abstract class SerializerConfigManager<C : Any, B : ConfigBuilder<C>, F : Any> : ConfigManager<C, B, F>() {
	/**
	 * The serializer used for the config type.
	 */
	abstract val serializer: KSerializer<C>

	/**
	 * The serialization format used for encoding and decoding.
	 */
	abstract val serialFormat: SerialFormat
}