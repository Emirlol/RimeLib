package me.ancientri.rimelib.config.events

/**
 * An interface for handling save events.
 *
 * @see ConfigManager#ON_SAVE
 */
fun interface OnSave {
	fun onSave()
}