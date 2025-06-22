package me.ancientri.rimelib.config.api

import com.mojang.serialization.Codec

interface CodecConfigManager<C, B: ConfigBuilder<C>> : ConfigManager<C, B> {
	val codec: Codec<C>
}