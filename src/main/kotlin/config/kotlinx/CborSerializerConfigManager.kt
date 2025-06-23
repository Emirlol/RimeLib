@file:OptIn(ExperimentalSerializationApi::class)

package me.ancientri.rimelib.config.kotlinx

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import me.ancientri.rimelib.config.ConfigBuilder

/**
 * Convenience implementation that defaults to using [Cbor.Default] as the serialization format.
 */
abstract class CborSerializerConfigManager<C : Any, B : ConfigBuilder<C>>(override val serialFormat: Cbor = Cbor.Default) : BinarySerializerConfigManager<C, B>()