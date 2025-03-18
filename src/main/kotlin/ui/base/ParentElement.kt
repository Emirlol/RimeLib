package me.rime.rimelib.ui.base

import me.rime.rimelib.ui.base.Constants.LEFT_MOUSE_BUTTON

/**
 * An element that can contain other elements.
 */
interface ParentElement : Element {
	val children: MutableList<out Element>

	/**
	 * The child element that is currently being dragged.
	 *
	 * If the child element is also a parent element, it could have its own dragged element.
	 * Recursively following the focus chain will lead to the leaf element that is being dragged.
	 *
	 * Note that this is `null` if the parent element itself is being dragged.
	 */
	var draggedElement: Element?

	/**
	 * The child element that is currently focused.
	 *
	 * If the child element is also a parent element, it could have its own focused element.
	 * Recursively following the focus chain will lead to the leaf element that is focused.
	 *
	 * Note that this is `null` if the parent element itself is focused.
	 */
	var focusedElement: Element?

	/**
	 * Find the leaf element in the hierarchy that is hovered.
	 *
	 * This includes the parent element itself, if the mouse is over none of its children but over the parent itself.
	 */
	fun hoveredElement(mouseX: Int, mouseY: Int): Element? {
		for (child in children) {
			if (child.isMouseOver(mouseX, mouseY)) {
				// Recursive check to find the leaf element that is hovered
				return if (child is ParentElement) child.hoveredElement(mouseX, mouseY) else child
			}
		}
		return if (this.isMouseOver(mouseX, mouseY)) this else null
	}

	override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean = when (val hovered = hoveredElement(mouseX, mouseY)) {
		null -> super.mouseClicked(mouseX, mouseY, button)
		this -> {
			if (focusedElement != null) focusedElement!!.focused = false
			focusedElement = null
			focused = true
			if (button == LEFT_MOUSE_BUTTON) {
				dragging = true
				draggedElement = null
			}
			true
		}

		else -> {
			if (hovered.mouseClicked(mouseX, mouseY, button)) {
				if (focusedElement != null) focusedElement!!.focused = false
				focusedElement = hovered
				// If it's a parent element, it will handle its own focus and dragging state
				if (hovered !is ParentElement) hovered.focused = true
				if (button == LEFT_MOUSE_BUTTON) {
					if (hovered !is ParentElement) hovered.dragging = true
					draggedElement = hovered
				}
				true
			} else false
		}
	}

	override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int): Boolean {
		// Only act on the dragged element instead of the focused element, because after release the focused element will remain the same.
		// If an element is being dragged, it's also the focused element anyway.
		// Focus is only changed upon click, not upon release.
		if (button == LEFT_MOUSE_BUTTON) {
			if (dragging) {
				dragging = false
				return true
			} else if (draggedElement != null) {
				draggedElement!!.mouseReleased(mouseX, mouseY, button)
				draggedElement = null
				return true
			}
		}

		return super.mouseReleased(mouseX, mouseY, button)
	}

	/**
	 * This override doesn't handle the case of the parent element being dragged.
	 *
	 * Override and call the super method if you want to act on the parent element being dragged.
	 */
	override fun mouseDragged(mouseX: Int, mouseY: Int, button: Int, deltaX: Double, deltaY: Double): Boolean =
		if (button == 0 && draggedElement != null && draggedElement!!.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) true
		else super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)

	override fun mouseScrolled(mouseX: Int, mouseY: Int, amount: Double): Boolean =
		hoveredElement(mouseX, mouseY)?.mouseScrolled(mouseX, mouseY, amount) ?: super.mouseScrolled(mouseX, mouseY, amount)

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
		focusedElement?.keyPressed(keyCode, scanCode, modifiers) ?: super.keyPressed(keyCode, scanCode, modifiers)

	override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
		focusedElement?.keyReleased(keyCode, scanCode, modifiers) ?: super.keyReleased(keyCode, scanCode, modifiers)

	override fun charTyped(character: Char, modifiers: Int): Boolean =
		focusedElement?.charTyped(character, modifiers) ?: super.charTyped(character, modifiers)
}