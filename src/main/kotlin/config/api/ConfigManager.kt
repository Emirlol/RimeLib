package me.ancientri.rimelib.config.api

import me.ancientri.rimelib.util.FabricLoader
import me.ancientri.symbols.config.ConfigClass
import java.nio.file.Path

/**
 * A generic interface for managing configurations in a mod.
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
 */
interface ConfigManager<C : Any, B : ConfigBuilder<C>> {
	/**
	 * The path to the configuration file. This should be an absolute path, as some child of [FabricLoader.configDir][net.fabricmc.loader.api.FabricLoader.getConfigDir].
	 */
	val configPath: Path

	/**
	 * Convenience property to get the relative path of the config file from the config directory.
	 */
	val relativePath: Path
		get() = FabricLoader.configDir.relativize(configPath)

	/**
	 * The current configuration object.
	 * This should be an immutable object that represents the current state of the configuration.
	 * This doesn't mean the object itself doesn't hold any immutable data, but rather that care should be taken to not modify the data directly.
	 *
	 * For modifications, use the [modifyConfig] method which will return a new config instance.
	 * The new config instance will be set as the current one via [setFromBuilder].
	 * Note that this doesn't save the config to the file automatically, so you should call [saveConfig] after modifying the config to persist changes.
	 * @see modifyConfig
	 */
	val config: C

	/**
	 * Loads the configuration from the file.
	 * @return The loaded configuration object, or null if the file could not be loaded or is invalid.
	 */
	fun loadConfig(): C?

	/**
	 * Saves the current configuration to the file.
	 * This method should be called after modifying the config to persist changes.
	 */
	fun saveConfig()

	/**
	 * Creates a new [ConfigBuilder][B] instance.
	 */
	fun builder(config: C): B

	/**
	 * Sets the config instance from a provided builder.
	 * @see ConfigBuilder.build
	 */
	fun setFromBuilder(builder: B): C

	/**
	 * Modifies the current config using the provided builder function and returns the updated config.
	 */
	fun modifyConfig(builder: B.() -> Unit): C = setFromBuilder(this.builder(config).apply(builder))
}