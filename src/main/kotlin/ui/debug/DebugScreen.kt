package me.rime.rimelib.ui.debug

import com.danrusu.pods4k.immutableArrays.ImmutableArray
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import me.rime.rimelib.ui.AbstractScreen
import me.rime.rimelib.ui.Screen
import me.rime.rimelib.ui.base.*
import me.rime.rimelib.util.ColorPalette
import me.rime.rimelib.util.ColorUtil.withAlpha
import me.rime.rimelib.util.LogUtil
import me.rime.rimelib.util.text
import net.minecraft.client.gui.DrawContext
import kotlin.math.sin

class DebugScreen : Screen("example text".text, padding = Padding(0), childGap = 16) {
	private val layer3Rects = ImmutableArray(100) { RectangleWidget(growing(100, 20..200), fixed(2), ColorPalette.TEXT, LayoutDirection.LEFT_TO_RIGHT, Padding(16)) }
	private val layer2Rects = ImmutableArray(2) { RectangleWidget(fixed(100), fixed(50), ColorPalette.OVERLAY1, LayoutDirection.LEFT_TO_RIGHT, Padding(16)) }
	private val parentRect = RectangleWidget(fit(200, 20..2000), fit(100, 0..2000), ColorPalette.OVERLAY1, LayoutDirection.TOP_TO_BOTTOM, Padding(16), 1) {
		layer3Rects.forEach { +it }
	}
	private val mainRect = RectangleWidget(growing(0, 0..2000), growing(0, 0..2000), ColorPalette.BASE.withAlpha(20), LayoutDirection.LEFT_TO_RIGHT, Padding(16), 16) {
		layer2Rects.forEach { +it }
		+parentRect
	}

	init {
		initChildren = { +mainRect }
	}

	private var ticks = 0
	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(context, mouseX, mouseY, delta)
		ticks++
		layer3Rects.forEach { it.ewidth.setValue(100 + (sin(ticks / 100.0) * 50.0).toInt()) }
		this.renderBackground(context, mouseX, mouseY, delta)

		for (child in children) {
			if (child is Drawable) child.render(context, mouseX, mouseY, delta)
		}
	}

	override val children: MutableList<Widget> = ObjectArrayList()

	override var ewidth: ElementSize = fixed((this as AbstractScreen).width)

	override var eheight: ElementSize = fixed((this as AbstractScreen).height)
	override var positioning: Positioning = Positioning.ABSOLUTE

	override var remainingWidth: Int = 0

	override var remainingHeight: Int = 0

	override var x: Int = 0

	override var y: Int = 0

	companion object {
		val LOGGER = LogUtil.createLogger(DebugScreen::class)
	}
}
