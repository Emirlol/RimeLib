package me.ancientri.rimelib.config.events

/**
 * An interface for handling load events.
 *
 * @param C The type of the configuration that is being loaded.
 * @see ConfigManager#ON_LOAD
 */
fun interface OnLoad<C> {
	fun onLoad(config: C)
}