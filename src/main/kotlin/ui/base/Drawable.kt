package me.ancientri.rimelib.ui.base

import net.minecraft.client.gui.DrawContext

/**
 * Represents a drawable UI element.
 */
fun interface Drawable {
	fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float)
}