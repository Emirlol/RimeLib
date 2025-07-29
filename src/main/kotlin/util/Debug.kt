package me.ancientri.rimelib.util

object Debug {
	val isEnabled: Boolean = FabricLoader.isDevelopmentEnvironment || System.getProperty("rimelib.debug").toBoolean()
}