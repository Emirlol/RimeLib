package me.ancientri.rimelib.ui.base

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.client.gui.DrawContext

abstract class AbstractWidget(
	override var ewidth: ElementSize,
	override var eheight: ElementSize,
	override val layoutDirection: LayoutDirection = LayoutDirection.LEFT_TO_RIGHT,
	override val padding: Padding = Padding.NONE,
	override val childGap: Int = 0,
	override var initChildren: WidgetBuilder.() -> Unit = { }
) : ParentWidget, Drawable {
	override val children: MutableList<Widget> = ObjectArrayList()
	override var remainingWidth = 0
	override var remainingHeight = 0
	override var x: Int = 0
	override var y: Int = 0

	fun renderBorder(context: DrawContext, color: Int) {
		context.drawBorder(x, y, ewidth.getValue(), eheight.getValue(), color)
	}
}