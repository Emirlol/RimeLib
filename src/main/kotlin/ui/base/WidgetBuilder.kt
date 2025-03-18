package me.rime.rimelib.ui.base

import com.danrusu.pods4k.immutableArrays.toImmutableArray
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import me.rime.rimelib.util.LogUtil

class WidgetBuilder {
	private val stack = ObjectArrayList<Widget>()

	private fun openWidget(widget: Widget) {
		require(stack.isEmpty || stack.top() is ParentWidget) { "Cannot add a child to a non-parent widget" }
		if (stack.isNotEmpty()) {
			val top = stack.top() as ParentWidget
			top.children.add(widget)
		}
		stack.push(widget)
	}

	private fun closeWidget() {
		require(stack.isNotEmpty()) { "Cannot close a widget when there are no widgets open" }
		val last = stack.pop()
		if (stack.isNotEmpty()) last.onClose(stack.top() as ParentWidget) // This is safe because only ParentWidgets can be parents
		if (last is ParentWidget) last.onCloseParent()
	}

	operator fun Widget.unaryPlus() = addWidget(this)

	fun addWidget(widget: Widget) {
		openWidget(widget)
		if (widget is ParentWidget) widget.initChildren(this)
		closeWidget()
	}

	companion object {
		private val LOGGER = LogUtil.createLogger(WidgetBuilder::class)

		// Shrink children first, then shrink self so everything is at its smallest
		// Note that the root widget is the screen, and it can't shrink so it'll effectively only shrink its children
		fun ParentWidget.shrinkChildren() {
			// Only ParentWidgets can be shrunk to fit content because child Widgets *are* the content, and they can't be shrunk any further.
			for (child in children) if (child is ParentWidget) child.shrinkChildren()

			if (ewidth is FittingElementSize) {
				if (ewidth.canShrink()) {
					val remainingShrink = (ewidth - calculateMinWidth()).coerceAtMost(ewidth.getRemainingShrink())
					ewidth -= remainingShrink
					remainingWidth -= remainingShrink
				} else if (ewidth.canGrow()) {
					val remainingGrow = (ewidth - calculateMinWidth()).coerceAtMost(ewidth.getRemainingGrow())
					ewidth += remainingGrow
					remainingHeight += remainingGrow
				}
			}

			if (eheight is FittingElementSize) {
				if (eheight.canShrink()) {
					val remainingShrink = (eheight - calculateMinHeight()).coerceAtMost(eheight.getRemainingShrink())
					eheight -= remainingShrink
					remainingHeight -= remainingShrink
				} else if (eheight.canGrow()) {
					val remainingGrow = (eheight - calculateMinHeight()).coerceAtMost(eheight.getRemainingGrow())
					eheight += remainingGrow
					remainingHeight += remainingGrow
				}
			}
		}

		// Caller is responsible for bounds checking
		private fun ParentWidget.growChildHorizontally(child: Widget, amount: Int) {
			child.ewidth += amount
			remainingWidth += amount
			if (child is ParentWidget) child.remainingWidth += amount
		}

		private fun ParentWidget.growChildVertically(child: Widget, amount: Int) {
			child.eheight += amount
			remainingHeight -= amount
			if (child is ParentWidget) child.remainingHeight += amount
		}

		private fun ParentWidget.growChildrenWidthPass() {
			if (children.isEmpty()) return
			val seq = children.asSequence()
			val filteredChildren = seq.filter { it.ewidth is GrowingElementSize }

			if (filteredChildren.count() == 1) {
				val child = filteredChildren.first()
				if (child.ewidth.canGrow() && remainingWidth > 0) {
					growChildHorizontally(child, child.ewidth.getRemainingGrow().coerceAtMost(remainingWidth))
				}
			} else when (layoutDirection) {
				LayoutDirection.LEFT_TO_RIGHT -> while (remainingWidth > 0) {
					val smallest = filteredChildren.minByOrNull { it.ewidth.getValue() } ?: break // If this is null, that means there are no children that can grow, so we're done
					val smallestWidth = smallest.ewidth.getValue()
					val equalSmallest = filteredChildren.filter { it.ewidth.getValue() == smallestWidth }.toImmutableArray()

					val nextSmallest = filteredChildren.filter { it.ewidth.getValue() != smallestWidth }.minByOrNull { it.ewidth.getValue() } // If this is null, that means there's only one child, in which case it can expand to fill the remaining space

					val widthDiff = if (nextSmallest != null) nextSmallest.ewidth - smallestWidth else remainingWidth - smallestWidth
					if (widthDiff < 0) break // There's no more space.
					val growAmount = if (nextSmallest != null) widthDiff.coerceAtMost(remainingWidth) else remainingWidth
					equalSmallest.forEach {
						val growPerChild = growAmount / equalSmallest.size
						if (it.ewidth.canGrow() && growPerChild > 0) {
							growChildHorizontally(it, it.ewidth.getRemainingGrow().coerceAtMost(growPerChild))
						}
					}
				}

				LayoutDirection.TOP_TO_BOTTOM -> {
					val largest = seq.maxOf { it.ewidth.getValue() }
					for (child in filteredChildren) {
						if (child.ewidth.canNotGrow()) continue
						val remainingSpace = largest - child.ewidth.getValue()
						if (remainingSpace <= 0) continue // This also checks if this child is the largest, in which case it would be 0
						val growAmount = remainingSpace.coerceAtMost(child.ewidth.getRemainingGrow())
						growChildHorizontally(child, growAmount)
					}
				}
			}
		}

		private fun ParentWidget.growChildrenHeightPass() {
			if (children.isEmpty()) return
			val seq = children.asSequence()
			val filteredChildren = children.asSequence().filter { it.eheight is GrowingElementSize }

			if (filteredChildren.count() == 1) {
				val child = filteredChildren.first()
				if (child.eheight.canGrow() && remainingHeight > 0) {
					growChildVertically(child, child.eheight.getRemainingGrow().coerceAtMost(remainingHeight))
				}
			} else when (layoutDirection) {
				LayoutDirection.TOP_TO_BOTTOM -> while (remainingHeight > 0) {
					val smallest = filteredChildren.minByOrNull { it.eheight.getValue() } ?: break // If this is null, that means there are no children that can grow, so we're done
					val smallestHeight = smallest.eheight.getValue()
					val equalSmallest = filteredChildren.filter { it.eheight.getValue() == smallestHeight }.toImmutableArray()

					val nextSmallest = filteredChildren.filter { it.eheight.getValue() != smallestHeight }.minByOrNull { it.eheight.getValue() } // If this is null, that means all children have the same width, in which case they can all expand equally

					val heightDiff = if (nextSmallest != null) nextSmallest.eheight - smallestHeight else remainingHeight - smallestHeight
					val growAmount = if (nextSmallest != null) heightDiff.coerceAtMost(remainingHeight) else remainingHeight
					equalSmallest.forEach {
						val growPerChild = growAmount / equalSmallest.size
						if (it.eheight.canGrow() && growPerChild > 0) {
							growChildVertically(it, it.eheight.getRemainingGrow().coerceAtMost(growPerChild))
						}
					}
				}

				LayoutDirection.LEFT_TO_RIGHT -> {
					val largest = seq.maxOf { it.eheight.getValue() }
					for (child in filteredChildren) {
						if (child.eheight.canNotGrow()) continue
						val remainingSpace = largest - child.eheight.getValue()
						if (remainingSpace <= 0) continue // This also checks if this child is the largest, in which case it would be 0
						val growAmount = remainingSpace.coerceAtMost(child.eheight.getRemainingGrow())
						growChildVertically(child, growAmount)
					}
				}
			}
		}

		// No need to handle growing self, each parent also handles the sizing of its children which may be parents themselves.
		// As for the root widget, that's the screen, and screens don't grow.
		fun ParentWidget.growChildren() {
			growChildrenWidthPass()
			growChildrenHeightPass()

			for (child in children) if (child is ParentWidget) child.growChildren()
		}

		fun ParentWidget.positionChildren() {
			var xOffset = x + padding.left
			var yOffset = y + padding.top

			for (child in children) {
				child.position(xOffset, yOffset)

				if (child is ParentWidget) child.positionChildren()

				when (layoutDirection) {
					LayoutDirection.LEFT_TO_RIGHT -> {
						xOffset += child.ewidth + childGap
					}

					LayoutDirection.TOP_TO_BOTTOM -> {
						yOffset += child.eheight + childGap
					}
				}
			}
		}

		private fun ParentWidget.onCloseParentWidthPass() {
			remainingWidth -= when (layoutDirection) {
				LayoutDirection.LEFT_TO_RIGHT -> padding.left + padding.right + (children.lastIndex * childGap)
				LayoutDirection.TOP_TO_BOTTOM -> padding.left + padding.right + (children.maxOfOrNull { it.ewidth.getValue() } ?: 0)
			}
		}

		private fun ParentWidget.onCloseParentHeightPass() {
			remainingHeight -= when (layoutDirection) {
				LayoutDirection.LEFT_TO_RIGHT -> padding.top + padding.bottom + (children.maxOfOrNull { it.eheight.getValue() } ?: 0)
				LayoutDirection.TOP_TO_BOTTOM -> padding.top + padding.bottom + (children.lastIndex * childGap)
			}
		}

		private fun ParentWidget.calculateMinWidth() = when (layoutDirection) {
			LayoutDirection.LEFT_TO_RIGHT -> padding.left + padding.right + children.sumOf { it.ewidth.getValue() } + (children.lastIndex * childGap)
			LayoutDirection.TOP_TO_BOTTOM -> padding.left + padding.right + (children.maxOfOrNull { it.ewidth.getValue() } ?: 0)
		}

		private fun ParentWidget.calculateMinHeight() = when (layoutDirection) {
			LayoutDirection.LEFT_TO_RIGHT -> padding.top + padding.bottom + (children.maxOfOrNull { it.eheight.getValue() } ?: 0)
			LayoutDirection.TOP_TO_BOTTOM -> padding.top + padding.bottom + children.sumOf { it.eheight.getValue() } + (children.lastIndex * childGap)
		}

		private fun ParentWidget.onCloseParent() {
			if (children.isEmpty()) return
			onCloseParentWidthPass()
			onCloseParentHeightPass()
		}

		private fun Widget.onClose(parent: ParentWidget) {
			parent.remainingWidth -= ewidth.getValue()
			parent.remainingHeight -= eheight.getValue()
		}
	}
}