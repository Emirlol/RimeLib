package me.rime.rimelib.util

import java.awt.Color

object ColorUtil {
	/**
	 * Multiply the opacity of a color by a factor. The factor should be between 0 and 1, inclusive.
	 *
	 * @param opacity The factor to multiply the opacity by.
	 */
	fun Color.multiplyOpacity(opacity: Double) = ((alpha * opacity).toInt() shl 24) or ((rgb shl 8) ushr 8)
	/**
	 * Multiply the opacity of a color by a factor. The factor should be between 0 and 1, inclusive.
	 *
	 * @param opacity The factor to multiply the opacity by.
	 */
	fun Color.multiplyOpacity(opacity: Float) = multiplyOpacity(opacity.toDouble())

	/**
	 * Creates a new [Color] instance with the rgb color of [this] and the specified alpha value.
	 */
	fun Color.withAlpha(alpha: Int): Color = Color(this.rgb and 0xFFFFFF or (alpha shl 24), true)
}