package me.rime.rimelib.util.collections.enums.set.mutable

import me.rime.rimelib.util.collections.enums.set.immutable.ImmutableRegularEnumSet
import kotlin.enums.EnumEntries

/**
 * A set implementation that uses a bit vector to store the elements.
 *
 * This implementation is suitable for enums with up to 64 elements.
 *
 * @param E The enum type
 * @param enumEntries The entries of the enum [E].
 * @property elements The bit vector representing the elements in this set
 * @property size The number of elements in this set
 */
class RegularEnumSet<E : Enum<E>>(enumEntries: EnumEntries<E>) : MutableEnumSet<E>(enumEntries), MutableSet<E> {
	var elements: Long = 0L
		private set

	override var size: Int = 0
		private set

	//region Constructors
	/**
	 * Alternative constructor that wraps an existing bit vector.
	 * @param enumEntries The entries of the enum [E].
	 * @param elements The bit vector to wrap.
	 */
	constructor(enumEntries: EnumEntries<E>, elements: Long) : this(enumEntries) {
		this.elements = elements
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

	//region Modification Operations

	//region Single Operations

	override fun add(element: E): Boolean {
		val oldElements = elements
		elements = oldElements or (1L shl element.ordinal)
		return oldElements != elements
	}

	override fun remove(element: E): Boolean {
		val oldElements = elements
		elements = oldElements and (1L shl element.ordinal).inv()
		return oldElements != elements
	}

	//endregion Single Operations

	//region Bulk Operations

	override fun addAll(elements: Collection<E>): Boolean {
		if (elements.isEmpty()) return false
		if (elements is RegularEnumSet) {
			val oldElements = this.elements
			this.elements = oldElements or elements.elements
			return oldElements != this.elements
		} else {
			var changed = false
			for (element in elements) changed = add(element) || changed
			calculateSize() // We can't just add the size of the argument because it might contain duplicates
			return changed
		}
	}

	override fun removeAll(elements: Collection<E>): Boolean {
		if (elements.isEmpty()) return false
		if (elements is RegularEnumSet) {
			val oldElements = this.elements
			this.elements = oldElements and elements.elements.inv()
			calculateSize()
			return oldElements != this.elements
		} else {
			var changed = false
			for (element in elements) changed = remove(element) || changed
			calculateSize()
			return changed
		}
	}

	override fun complement() {
		if (enumEntries.isEmpty()) return // No elements to complement
		elements = elements.inv() and (-1L ushr (64 - enumEntries.size)) // Mask unused bits
		calculateSize()
	}

	override fun clear() {
		elements = 0L
		size = 0
	}

	override fun retainAll(elements: Collection<E>): Boolean {
		val oldElements = this.elements
		if (elements.isEmpty()) {
			clear()
			return oldElements != 0L
		}

		if (elements is RegularEnumSet) this.elements = oldElements and elements.elements
		else {
			clear()
			for (element in elements) this.elements = this.elements or (1L shl element.ordinal)
		}

		calculateSize()
		return oldElements != this.elements
	}

	//endregion Bulk Operations

	//endregion Modification Operations

	override fun contains(element: E): Boolean = elements and (1L shl element.ordinal) != 0L

	override fun iterator(): MutableIterator<E> = RegularEnumSetIterator()

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
	private inner class RegularEnumSetIterator : MutableEnumSetIterator<E> {
		private var unseen = elements

		private var lastReturned = 0L

		override fun hasNext(): Boolean = unseen != 0L

		override fun next(): E {
			if (unseen == 0L) throw NoSuchElementException()
			lastReturned = unseen and -unseen
			unseen -= lastReturned
			return enumEntries[lastReturned.countTrailingZeroBits()]
		}

		override fun remove() {
			check(lastReturned != 0L)
			val oldElements = elements
			elements = oldElements and lastReturned.inv()
			if (oldElements != elements) size--
			lastReturned = 0L
		}
	}
}