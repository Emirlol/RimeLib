package me.rime.rimelib.ui.debug

import me.rime.rimelib.ui.base.*
import net.minecraft.client.gui.DrawContext
import java.awt.Color

data class RectangleWidget(
	override var ewidth: ElementSize,
	override var eheight: ElementSize,
	var color: Color,
	override var layoutDirection: LayoutDirection = LayoutDirection.LEFT_TO_RIGHT,
	override var padding: Padding = Padding(0),
	override var childGap: Int = 0,
	override var initChildren: WidgetBuilder.() -> Unit = { }
) : AbstractWidget(ewidth, eheight, layoutDirection, padding, childGap, initChildren), Drawable {
	override var positioning: Positioning = Positioning.RELATIVE

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		context.fill(x, y, x + ewidth.getValue(), y + eheight.getValue(), color.rgb)
		if (children.isNotEmpty()) {
			for (child in children) {
				if (child is Drawable) child.render(context, mouseX, mouseY, delta)
			}
		}
	}
}