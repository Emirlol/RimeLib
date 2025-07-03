package me.ancientri.rimelib.config.dfu

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult.Error
import com.mojang.serialization.DataResult.Success
import com.mojang.serialization.DynamicOps
import me.ancientri.rimelib.config.ConfigBuilder
import me.ancientri.rimelib.config.ConfigManager
import me.ancientri.rimelib.config.exceptions.DecodeException
import me.ancientri.rimelib.config.exceptions.EncodeException

/**
 * Codec-based config manager.
 */
abstract class CodecConfigManager<C : Any, B : ConfigBuilder<C>, F : Any> : ConfigManager<C, B, F>() {
	/**
	 * The codec used for encoding and decoding the config.
	 */
	abstract val codec: Codec<C>

	/**
	 * The dynamic operations used for encoding and decoding.
	 */
	abstract val ops: DynamicOps<F>

	override fun encode(config: C): F = when (val result = codec.encodeStart(ops, config)) {
		is Success -> result.value
		is Error -> throw EncodeException(result.message())
	}

	override fun decode(data: F): C = when (val result = codec.decode(ops, data)) {
		is Success -> result.value.first
		is Error -> throw DecodeException(result.message())
	}
}