package me.ancientri.rimelib.config.kotlinx

import kotlinx.serialization.BinaryFormat
import me.ancientri.rimelib.config.ConfigBuilder
import java.io.InputStream
import java.io.OutputStream

/**
 * A binary config manager that uses `kotlinx.serialization` for serialization and deserialization.
 * The format is `ByteArray`, which is suitable for binary data.
 *
 * This class implements some common functionality for binary serialization,
 * such as encoding and decoding to/from `ByteArray`, and reading/writing to streams.
 */
abstract class BinarySerializerConfigManager<C : Any, B : ConfigBuilder<C>> : SerializerConfigManager<C, B, ByteArray>() {
	// Coerce the type to BinaryFormat by overriding
	abstract override val serialFormat: BinaryFormat

	override fun encode(config: C): ByteArray? = try {
		serialFormat.encodeToByteArray(serializer, config)
	} catch (e: Exception) {
		logger.error("Failed to encode config to string: ${e.message}", e)
		null
	}

	override fun decode(data: ByteArray): C? = try {
		serialFormat.decodeFromByteArray(serializer, data)
	} catch (e: Exception) {
		logger.error("Failed to decode config from string: ${e.message}", e)
		null
	}

	override fun writeToStream(stream: OutputStream, data: ByteArray) = stream.write(data)

	override fun readFromStream(stream: InputStream): ByteArray = stream.readAllBytes()
}