package me.ancientri.rimelib.ui.base

interface ParentWidget: Widget {
	val layoutDirection: LayoutDirection
	val padding: Padding
	val childGap: Int
	val children: MutableList<Widget>
	var remainingWidth: Int
	var remainingHeight: Int

	var initChildren: WidgetBuilder.() -> Unit

	fun clearChildren() {
		for (child in children) {
			if (child is ParentWidget) child.clearChildren()
		}
		children.clear()
	}
}