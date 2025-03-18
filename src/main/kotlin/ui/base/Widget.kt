package me.rime.rimelib.ui.base

/**
 * Represents a UI element that has a position and size.
 */
interface Widget {
	var x: Int
	var y: Int
	var ewidth: ElementSize
	var eheight: ElementSize
	var positioning: Positioning

	fun position(x: Int, y: Int) {
		this.x = x
		this.y = y
	}
}