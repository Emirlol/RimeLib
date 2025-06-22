package me.ancientri.rimelib.config.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult.Error
import com.mojang.serialization.DataResult.Success
import com.mojang.serialization.JsonOps
import me.ancientri.rimelib.RimeLib
import me.ancientri.rimelib.config.api.CodecConfigManager
import me.ancientri.rimelib.config.api.ConfigBuilder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.notExists

abstract class JsonCodecConfigManager<C, B : ConfigBuilder<C>>(
	override val configPath: Path,
	override val codec: Codec<C>,
	val default: C
) : CodecConfigManager<C, B> {
	private val logger = RimeLib.loggerFactory.createLogger(JsonCodecConfigManager::class)
	val gson: Gson = GsonBuilder().setPrettyPrinting().create()
	val ops: JsonOps get() = JsonOps.INSTANCE

	init {
		setConfig(
			if (configPath.notExists()) {
				logger.info("Config file {} does not exist, creating with default values.", relativePath)
				default
			} else when (val loadedConfig = loadConfig()) {
				null -> {
					logger.warn("Config file {} is invalid or could not be loaded, using default values.", relativePath)
					default
				}

				else -> {
					logger.info("Loaded config file {}.", relativePath)
					loadedConfig
				}
			}
		)
	}

	fun updateConfig(builder: B.() -> Unit): C {
		setConfig(modifyConfig(builder))
		saveConfig()
		return config
	}

	override fun loadConfig(): C? = configPath.bufferedReader().use { reader ->
		val json = gson.fromJson(reader, JsonObject::class.java)
		when (val result = codec.decode(ops, json)) {
			is Success -> result.value.first
			is Error -> {
				logger.error("Failed to load config: {}", result.message())
				null
			}
		}
	}

	override fun saveConfig() {
		val json = when (val result = codec.encodeStart(ops, config)) {
			is Success -> result.value
			is Error -> {
				logger.error("Failed to encode config: {}", result.message())
				return
			}
		}

		Files.createDirectories(configPath.parent)
		Files.newBufferedWriter(configPath).use { writer ->
			gson.toJson(json, writer)
		}
	}
}