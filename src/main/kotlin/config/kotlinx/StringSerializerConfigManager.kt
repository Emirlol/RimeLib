package me.ancientri.rimelib.config.kotlinx

import kotlinx.serialization.StringFormat
import me.ancientri.rimelib.config.ConfigBuilder
import me.ancientri.rimelib.config.exceptions.DecodeException
import me.ancientri.rimelib.config.exceptions.EncodeException
import java.io.InputStream
import java.io.OutputStream

/**
 * A config manager that uses `kotlinx.serialization` with a [StringFormat] for serialization and deserialization.
 *
 * This class provides methods to encode and decode configurations to and from strings.
 *
 * @see JsonSerializerConfigManager
 */
abstract class StringSerializerConfigManager<C : Any, B : ConfigBuilder<C>> : SerializerConfigManager<C, B, String>() {
	// Coerce the type to StringFormat by overriding
	abstract override val serialFormat: StringFormat

	override fun encode(config: C): String = try {
		serialFormat.encodeToString(serializer, config)
	} catch (e: Exception) {
		throw EncodeException(e)
	}

	override fun decode(data: String): C = try {
		serialFormat.decodeFromString(serializer, data)
	} catch (e: Exception) {
		throw DecodeException(e)
	}

	override fun writeToStream(stream: OutputStream, data: String) = stream.bufferedWriter().use { writer ->
		writer.write(data)
	}

	override fun readFromStream(stream: InputStream): String = stream.bufferedReader().use { reader ->
		reader.readText()
	}
}