package me.ancientri.rimelib.config.events

import me.ancientri.rimelib.config.ConfigManager

/**
 * An interface for handling save events.
 *
 * @see ConfigManager.ON_SAVE
 */
fun interface OnSave {
	fun onSave()
}