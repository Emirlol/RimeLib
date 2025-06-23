package me.ancientri.rimelib.config.kotlinx

import kotlinx.serialization.json.Json
import me.ancientri.rimelib.config.ConfigBuilder

/**
 * Convenience implementation that defaults to using [Json.Default] as the serialization format.
 */
abstract class JsonSerializerConfigManager<C : Any, B : ConfigBuilder<C>>(override val serialFormat: Json = Json.Default) : StringSerializerConfigManager<C, B>()