package me.ancientri.rimelib.config.api

import me.ancientri.rimelib.util.FabricLoader
import java.nio.file.Path

/**
 * @property configPath The path to the configuration file. This should be an absolute path, as some child of [FabricLoader.configDir][net.fabricmc.loader.api.FabricLoader.getConfigDir].
 * @param C The type of the configuration object.
 * @param B The type of the config builder.
 */
interface ConfigManager<C, B: ConfigBuilder<C>> {
	val configPath: Path

	val relativePath: Path
		get() = FabricLoader.configDir.relativize(configPath)

	val config: C

	fun setConfig(config: C)

	fun loadConfig(): C?

	fun saveConfig()

	fun modifyConfig(builder: B.() -> Unit): C
}