package me.rime.rimelib.util

import java.awt.Color

object ColorUtil {
	fun Color.multiplyOpacity(opacity: Double) = ((alpha * opacity).toInt() shl 24) or ((rgb shl 8) ushr 8)
	fun Color.multiplyOpacity(opacity: Float) = multiplyOpacity(opacity.toDouble())
	fun Color.withAlpha(alpha: Int): Color = Color(this.rgb and 0xFFFFFF or (alpha shl 24), true)
}