package me.rime.rimelib.util.collections.enums.set.mutable

import me.rime.rimelib.util.collections.enums.set.immutable.EnumSet
import kotlin.enums.EnumEntries

abstract class MutableEnumSet<E : Enum<E>>(
	enumEntries: EnumEntries<E>
) : EnumSet<E>(enumEntries), MutableSet<E>, Cloneable {
	/**
	 * Complements the contents of this set in-place.
	 */
	abstract fun complement()

	@Suppress("UNCHECKED_CAST")
	override fun mutableCopyOf(): MutableEnumSet<E> = clone() as MutableEnumSet<E>

	/**
	 * Returns a new set containing the complement of this set without mutating this set.
	 */
	fun complemented(): MutableEnumSet<E> = mutableCopyOf().apply(MutableEnumSet<E>::complement)

	// I don't see how creating a linked hash map is better than just using the list that simply wraps the array.
	@Suppress("ConvertArgumentToSet")
	fun removeAll(vararg elements: E): Boolean = removeAll(elements.asList())

	fun addAll(vararg elements: E): Boolean = addAll(elements.asList())

	interface MutableEnumSetIterator<E : Enum<E>> : EnumSetIterator<E>, MutableIterator<E>
}