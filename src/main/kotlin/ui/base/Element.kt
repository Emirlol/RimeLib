package me.ancientri.rimelib.ui.base

/**
 * Represents a UI element that can receive input events.
 */
interface Element {
	/**
	 * Whether this element is being dragged.
	 *
	 * This is only `true` for the leaf element in a UI hierarchy that is being dragged.
	 */
	var dragging: Boolean

	/**
	 * Whether this element is focused.
	 *
	 * This is only `true` for the leaf element in a UI hierarchy that is focused.
	 */
	var focused: Boolean

	/**
	 * Called when the mouse is moved.
	 */
	fun mouseMoved(mouseX: Int, mouseY: Int): Unit = Unit

	/**
	 * Called when a mouse button is pressed.
	 * @return true if the event was handled, false otherwise
	 */
	fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean = false

	/**
	 * Called when a mouse button is released.
	 * @return true if the event was handled, false otherwise
	 */
	fun mouseReleased(mouseX: Int, mouseY: Int, button: Int): Boolean = false

	/**
	 * Called when the mouse is dragged with a button being held.
	 * @return true if the event was handled, false otherwise
	 */
	fun mouseDragged(mouseX: Int, mouseY: Int, button: Int, deltaX: Double, deltaY: Double): Boolean = false

	/**
	 * Called when the mouse is scrolled.
	 * @return true if the event was handled, false otherwise
	 */
	fun mouseScrolled(mouseX: Int, mouseY: Int, amount: Double): Boolean = false

	/**
	 * Called when a key is pressed.
	 * @return true if the event was handled, false otherwise
	 */
	fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean = false

	/**
	 * Called when a key is released.
	 * @return true if the event was handled, false otherwise
	 */
	fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean = false

	/**
	 * Called when a character is typed.
	 * @return true if the event was handled, false otherwise
	 */
	fun charTyped(character: Char, modifiers: Int): Boolean = false

	/**
	 * Whether the mouse is over this element.
	 * @return true if the mouse is over this element, false otherwise
	 */
	fun isMouseOver(mouseX: Int, mouseY: Int): Boolean = false
}