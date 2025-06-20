@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.ui.base

import me.ancientri.rimelib.ui.Screen
import me.ancientri.rimelib.util.client

/**
 * Wrapper around an integer that represents the size of an element. This can be either height or width.
 */
abstract class ElementSize(@JvmField protected var value: Int) { // Without the @JvmField annotation, the value field generates a getter and setter, which clashes with the getter and setter in the class.
	fun getValue(): Int = value
	fun setValue(value: Int) {
		this.value = value
		(client.currentScreen as? Screen)?.markDirty()
	}

	operator fun minus(amount: Int): Int = value - amount

	operator fun plus(amount: Int): Int = value + amount

	operator fun times(amount: Int): Int = value * amount

	internal operator fun minusAssign(amount: Int) {
		value -= amount
	}

	internal operator fun plusAssign(amount: Int) {
		value += amount
	}

	/**
	 * Meant to be used internally by the layout system to not recursively reinitialize the layout.
	 */
	internal fun setValueInternal(value: Int) {
		this.value = value
	}

	abstract fun canGrow(amount: Int): Boolean
	abstract fun canShrink(amount: Int): Boolean
	abstract fun canGrow(): Boolean
	abstract fun canShrink(): Boolean
	abstract fun getRemainingGrow(): Int
	abstract fun getRemainingShrink(): Int
}

inline fun ElementSize.canNotShrink() = !canShrink()

inline fun ElementSize.canNotGrow() = !canGrow()

class FixedElementSize(value: Int) : ElementSize(value) {
	override fun canGrow(amount: Int): Boolean = false
	override fun canShrink(amount: Int): Boolean = false
	override fun canGrow(): Boolean = false
	override fun canShrink(): Boolean = false
	override fun getRemainingGrow(): Int = 0
	override fun getRemainingShrink(): Int = 0
}

class GrowingElementSize(value: Int = 0, val min: Int, val max: Int) : ElementSize(value) {
	override fun canGrow(amount: Int): Boolean = value + amount <= max
	override fun canShrink(amount: Int): Boolean = false
	override fun canGrow(): Boolean = value < max
	override fun canShrink(): Boolean = false
	override fun getRemainingGrow(): Int = max - value
	override fun getRemainingShrink(): Int = 0
}

class FittingElementSize(value: Int = 0, val min: Int, val max: Int) : ElementSize(value) {
	override fun canGrow(amount: Int): Boolean = false
	override fun canShrink(amount: Int): Boolean = value - amount >= min
	override fun canGrow(): Boolean = value < max
	override fun canShrink(): Boolean = value > min
	override fun getRemainingGrow(): Int = max - value
	override fun getRemainingShrink(): Int = value - min
}

fun fixed(value: Int) = FixedElementSize(value)

fun growing(value: Int = 0, min: Int, max: Int) = GrowingElementSize(value, min, max)

fun growing(value: Int = 0, range: IntRange) = GrowingElementSize(value, range.first, range.last)

fun fit(value: Int = 0, min: Int, max: Int) = FittingElementSize(value, min, max)

fun fit(value: Int = 0, range: IntRange) = FittingElementSize(value, range.first, range.last)