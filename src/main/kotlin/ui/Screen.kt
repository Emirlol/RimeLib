package me.ancientri.rimelib.ui

import me.ancientri.rimelib.RimeLib
import me.ancientri.rimelib.ui.base.LayoutDirection
import me.ancientri.rimelib.ui.base.Padding
import me.ancientri.rimelib.ui.base.ParentWidget
import me.ancientri.rimelib.ui.base.WidgetBuilder
import me.ancientri.rimelib.ui.base.WidgetBuilder.Companion.growChildren
import me.ancientri.rimelib.ui.base.WidgetBuilder.Companion.positionChildren
import me.ancientri.rimelib.ui.base.WidgetBuilder.Companion.shrinkChildren
import me.ancientri.rimelib.util.profiled
import me.ancientri.rimelib.util.text.TextBuilder
import me.ancientri.rimelib.util.text.text
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

typealias AbstractScreen = net.minecraft.client.gui.screen.Screen

/**
 * As a design philosophy, the new fields added by this class are mutable, but their values are immutable.
 *
 * This is to make it easier to update the screen when the fields are changed, without having each field's value fields setters change the screen.
 */
abstract class Screen(
	title: Text?,
	override val layoutDirection: LayoutDirection = LayoutDirection.LEFT_TO_RIGHT,
	override val padding: Padding = Padding(0),
	override val childGap: Int = 0,
	override var initChildren: WidgetBuilder.() -> Unit = { }
) : AbstractScreen(title), ParentWidget {
	/**
	 * Whether this screen needs to be reinitialized.
	 */
	private var dirty: Boolean = false

	/**
	 * Marks this screen as dirty, so it will be reinitialized on the next render.
	 */
	fun markDirty() {
		dirty = true
	}

	override fun clearChildren() {
		super<ParentWidget>.clearChildren()
	}

	/**
	 * When overriding this, either call super.render or handle the dirty flag yourself.
	 */
	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		if (dirty) {
			clearAndInit()
			dirty = false
		}
	}

	constructor(titleBuilder: TextBuilder.() -> Unit) : this(text(titleBuilder))

	/**
	 * This is only public because it needs to be access by the game. You don't need to override this method.
	 */
	override fun init() {
		profiled("Screen init") {
			remainingWidth = (this as AbstractScreen).width // We want the vanilla width and height, because these are effectively the size of the window.
			remainingHeight = (this as AbstractScreen).height
			x = 0
			y = 0
			with(WidgetBuilder()) {
				profiled("Element initialization") {
					addWidget(this@Screen)
				}
				profiled("Element shrinking") {
					shrinkChildren()
				}
				profiled("Element growing") {
					growChildren()
				}
				profiled("Element positioning") {
					positionChildren()
				}
			}
		}
	}

	companion object {
		val LOGGER = RimeLib.loggerFactory.createLogger(Screen::class)
	}
}
