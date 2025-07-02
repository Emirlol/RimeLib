package me.ancientri.rimelib.config.dfu

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import me.ancientri.rimelib.config.ConfigBuilder
import java.io.InputStream
import java.io.OutputStream

/**
 * A json-based config manager that uses a [Codec] to encode and decode the config object.
 */
abstract class JsonCodecConfigManager<C : Any, B : ConfigBuilder<C>> : CodecConfigManager<C, B, JsonElement>() {
	open val gson: Gson = GsonBuilder().setPrettyPrinting().create()

	override val ops: DynamicOps<JsonElement> = JsonOps.INSTANCE

	override fun readFromStream(stream: InputStream): JsonElement = stream.bufferedReader().use { gson.fromJson(it, JsonElement::class.java) }

	override fun writeToStream(stream: OutputStream, data: JsonElement) = stream.bufferedWriter().use {gson.toJson(data, it) }
}