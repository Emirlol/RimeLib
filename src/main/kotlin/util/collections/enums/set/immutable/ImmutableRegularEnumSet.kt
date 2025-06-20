package me.ancientri.rimelib.util.collections.enums.set.immutable

import me.ancientri.rimelib.util.collections.enums.set.mutable.RegularEnumSet
import kotlin.enums.EnumEntries

/**
 * An immutable set implementation that uses a bit vector to store the elements.
 *
 * This implementation is suitable for enums with up to 64 elements.
 *
 * @param E The enum type
 * @param enumEntries The entries of the enum [E].
 * @property elements The bit vector representing the elements in this set
 * @property size The number of elements in this set
 */
class ImmutableRegularEnumSet<E : Enum<E>>(enumEntries: EnumEntries<E>) : EnumSet<E>(enumEntries) {
	var elements: Long = 0L
		private set

	override var size: Int = 0
		private set

	//region Constructors

	/**
	 * Alternative constructor that wraps an existing bit vector.
	 *
	 * @param enumEntries The entries of the enum [E].
	 * @param elements The bit vector to wrap.
	 */
	constructor(enumEntries: EnumEntries<E>, elements: Long) : this(enumEntries) {
		this.elements = elements and (-1L ushr (64 - enumEntries.size))
		calculateSize()
	}

	/**
	 * Alternative constructor that wraps an existing [RegularEnumSet].
	 *
	 * Changes to [set] will not be reflected in this set.
	 *
	 * @param set The set to wrap.
	 */
	constructor(set: RegularEnumSet<E>) : this(set.enumEntries) {
		this.elements = set.elements
		this.size = set.size
	}

	/**
	 * Alternative constructor that wraps an existing [ImmutableRegularEnumSet].
	 *
	 * @param set The set to wrap.
	 */
	constructor(set: ImmutableRegularEnumSet<E>) : this(set.enumEntries) {
		this.elements = set.elements
		this.size = set.size
	}

	init {
		require(enumEntries.size <= 64) { "The provided enum has too many entries to fit in a `RegularEnumSet`." }
	}

	//endregion Constructors

	override fun contains(element: E): Boolean = elements and (1L shl element.ordinal) != 0L

	override fun iterator(): EnumSetIterator<E> = RegularEnumSetIterator()

	private fun calculateSize() {
		size = elements.countOneBits()
	}

	override fun mutableCopyOf(): RegularEnumSet<E> = RegularEnumSet(this)

	override fun immutableCopyOf(): ImmutableRegularEnumSet<E> = ImmutableRegularEnumSet(this)

	/**
	 * An iterator over the elements of this set.
	 * @property unseen A bit vector representing the elements not yet returned by this iterator.
	 * @property lastReturned The bit representing the last element returned by this iterator but not removed, or 0 if no such element exists.
	 */
	private inner class RegularEnumSetIterator : EnumSetIterator<E> {
		private var unseen = elements

		private var lastReturned = 0L

		override fun hasNext(): Boolean = unseen != 0L

		override fun next(): E {
			if (unseen == 0L) throw NoSuchElementException()
			lastReturned = unseen and -unseen
			unseen -= lastReturned
			return enumEntries[lastReturned.countTrailingZeroBits()]
		}
	}
}