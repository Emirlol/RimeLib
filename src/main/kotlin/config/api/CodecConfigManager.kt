package me.ancientri.rimelib.config.api

import com.mojang.serialization.Codec

/**
 * Codec-based config manager.
 */
interface CodecConfigManager<C: Any, B: ConfigBuilder<C>> : ConfigManager<C, B> {
	val codec: Codec<C>
	// TODO: Consider refactoring some of the methods in JsonCodecConfigManager to this interface with a more generic approach.
}