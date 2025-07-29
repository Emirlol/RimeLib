package me.ancientri.rimelib.config

import me.ancientri.rimelib.config.events.OnLoad
import me.ancientri.rimelib.config.events.OnSave
import me.ancientri.rimelib.config.exceptions.DecodeException
import me.ancientri.rimelib.config.exceptions.EncodeException
import me.ancientri.rimelib.util.EventUtil
import me.ancientri.rimelib.util.FabricLoader
import me.ancientri.symbols.config.ConfigClass
import net.fabricmc.fabric.api.event.Event
import org.slf4j.Logger
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

/**
 * A generic abstract class for managing configurations in a mod.
 * This manager is responsible for:
 * - Loading the configuration from a file.
 * - Saving the configuration to a file.
 * - Providing a way to read and modify the configuration.
 *
 * This manager is designed to work with immutable configuration objects, meaning that the config object held by this manager should not be modified directly.
 * Instead, modifications should be made through a [ConfigBuilder][B] instance, which can then be used to create a new config object.
 *
 * It's recommended to use [ConfigClass] annotation on the config class to generate the builder automatically, but you can also implement the [ConfigBuilder][B] interface manually if needed.
 *
 * @param C The type of the config object.
 * @param B The type of the config builder.
 * @param F The type of the intermediary object type used for serialization, such as a JSON object or a map.
 */
abstract class ConfigManager<C : Any, B : ConfigBuilder<C>, F : Any> {
	abstract val logger: Logger

	/**
	 * The path to the configuration file. This should be an absolute path, as some child of [FabricLoader.configDir][net.fabricmc.loader.api.FabricLoader.getConfigDir].
	 */
	abstract val configPath: Path

	/**
	 * The config object created with default values.
	 * This will be used to initialize the config when the config file does not exist or is invalid.
	 */
	abstract val default: C

	/**
	 * The current configuration object.
	 * This should be an immutable object that represents the current state of the configuration.
	 * This doesn't mean the object itself doesn't hold any immutable data, but rather that care should be taken to not modify the data directly.
	 *
	 * This isn't initialized until [init] is called, which will load the config from the file or create a new one with default values if the file does not exist.
	 *
	 * For modifications, use the [modifyConfig] method which will return a new config instance.
	 * Note that this doesn't save the config to the file automatically, so you should call [saveConfig] after modifying the config to persist changes.
	 * @see modifyConfig
	 */
	lateinit var config: C
		protected set

	/**
	 * Convenience property to get the relative path of the config file from the config directory.
	 */
	val relativePath: Path
		get() = FabricLoader.configDir.relativize(configPath)

	/**
	 * Event that is fired when the config is saved.
	 */
	val ON_SAVE: Event<OnSave> = EventUtil.createArrayBacked { listeners ->
		OnSave {
			for (listener in listeners) {
				listener.onSave()
			}
		}
	}

	/**
	 * Event that is fired when the config is loaded with the loaded config object.
	 */
	val ON_LOAD: Event<OnLoad<C>> = EventUtil.createArrayBacked { listeners ->
		OnLoad { config ->
			for (listener in listeners) {
				listener.onLoad(config)
			}
		}
	}

	/**
	 * Initializes the config object by loading it from the file or creating a new one with default values if the file does not exist.
	 * This method should be called once at the start of the application to ensure that the config is loaded and ready to use.
	 */
	fun init() {
		config = if (configPath.notExists()) {
			logger.warn("Config file {} does not exist, creating with default values.", relativePath)
			saveConfig(default)
			default
		} else when (val loadedConfig = loadConfig()) {
			null -> {
				logger.warn("Config file {} could not be loaded, using default values.", relativePath)
				default
			}

			else -> {
				logger.info("Loaded config file {}.", relativePath)
				loadedConfig
			}
		}
		ON_LOAD.invoker().onLoad(config)
	}

	/**
	 * Creates a new [ConfigBuilder][B] instance.
	 */
	abstract fun builder(config: C): B

	/**
	 * Writes the provided data to the output stream.
	 * @param stream The output stream to write to. The caller will close this stream.
	 * @param data The data to write to the stream, which is an intermediary representation of the config.
	 */
	abstract fun writeToStream(stream: OutputStream, data: F)

	/**
	 * Reads the data from the input stream.
	 * @param stream The input stream to read from. The caller will close this stream.
	 * @return The intermediary representation of the config, which can be used to decode into the actual config object.
	 */
	abstract fun readFromStream(stream: InputStream): F

	/**
	 * Encodes the provided config object into an intermediary representation.
	 * @param config The config object to encode.
	 * @return The intermediary representation of the config, which can be written to a file.
	 * @throws [EncodeException] if the config could not be encoded into the intermediary representation.
	 */
	abstract fun encode(config: C): F

	/**
	 * Decodes the provided intermediary data into a config object.
	 * @param data The intermediary representation of the config.
	 * @return The decoded config object.
	 * @throws [DecodeException] if the data could not be decoded into a config object.
	 */
	abstract fun decode(data: F): C

	/**
	 * Sets the current config to the result of building the provided [ConfigBuilder][B].
	 *
	 * This is meant for usage in GUI applications where the config is modified however the user wants and then saved all at once.
	 *
	 *
	 * @return The current config object after applying the builder.
	 *
	 */
	fun applyBuilder(builder: B): C {
		config = builder.build()
		return config
	}

	/**
	 * Modifies the current config using the provided builder function and returns the updated config.
	 *
	 * Note that this function **does not** save the changes to the config file.
	 * @return The updated config object after applying the builder.
	 * @see updateConfig
	 */
	fun modifyConfig(builder: B.() -> Unit): C = applyBuilder(builder(config).apply(builder))

	/**
	 * Modifies the current config using the provided builder function and saves the changes to the config file.
	 * @param builder A lambda function that takes a [B] (config builder) and modifies it.
	 * @return The updated config object after applying the builder and saving it to the file.
	 */
	fun updateConfig(builder: B.() -> Unit): C {
		saveConfig(modifyConfig(builder))
		return config
	}

	/**
	 * Saves the current configuration to the file.
	 * This method should be called after modifying the config to persist changes.
	 */
	fun saveConfig(config: C = this.config) {
		logger.info("Saving config to file: {}", relativePath)
		logger.debug("Encoding config: {}", config)
		val encoded = try {
			encode(config)
		} catch (e: Exception) {
			logger.error("Failed to encode config for saving: {}", e.message, e)
			return
		}
		logger.debug("Encoded config: {}", encoded)
		configPath.createParentDirectories()
		configPath.outputStream().use { writer ->
			try {
				writeToStream(writer, encoded)
			} catch (e: Exception) {
				logger.error("Failed to write config to file {}: {}", relativePath, e.message, e)
			}
		}
	}

	/**
	 * Loads the configuration from the file.
	 * @return The loaded configuration object, or `null` if the file could not be read for some reason.
	 */
	fun loadConfig(): C? {
		if (configPath.notExists()) return null

		val readResult = configPath.inputStream().use { reader ->
			try {
				readFromStream(reader)
			} catch (e: Exception) {
				logger.error("Failed to read file {}: {}", relativePath, e.message, e)
				return null
			}
		}

		logger.debug("Read config file: {}", readResult)

		val decoded = try {
			decode(readResult)
		} catch (e: Exception) {
			logger.error("Failed to decode config from file {}: {}", relativePath, e.message, e)
			return null
		}

		logger.debug("Decoded config: {}", decoded)
		return decoded
	}
}