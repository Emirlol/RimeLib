package me.ancientri.rimelib.util.collections.enums.set.immutable

import com.danrusu.pods4k.immutableArrays.ImmutableLongArray
import com.danrusu.pods4k.immutableArrays.buildImmutableLongArray
import com.danrusu.pods4k.immutableArrays.toImmutableArray
import me.ancientri.rimelib.util.collections.enums.set.mutable.JumboEnumSet
import kotlin.enums.EnumEntries

/**
 * An immutable set implementation that uses a bit vector to store the elements.
 * This implementation is suitable for enums with more than 64 elements.
 *
 * Since the values are stored in a bit vector, the iteration order is always in the enum's natural order.
 *
 * **Note: This implementation will still work for enums with 64 or fewer elements, but it is less efficient than [RegularEnumSet].** Use `enumSetOf` if you are unsure.
 * @param E The enum type
 * @param enumEntries The entries of the enum [E].
 * @property elements The bit vector representing the elements in this set
 * @property size The number of elements in this set
 * @property arraySize The size of the bit vector array
 */
@Suppress("NOTHING_TO_INLINE")
class ImmutableJumboEnumSet<E : Enum<E>>(enumEntries: EnumEntries<E>) : EnumSet<E>(enumEntries) {
	private val arraySize = (this.enumEntries.size + 63) ushr 6 // (entries.size / 64) + 1
	override var size: Int = 0
		private set

	// This has a public getter to allow concise de/serialization
	var elements: ImmutableLongArray = buildImmutableLongArray(arraySize) {}
		private set

	/**
	 * Alternative constructor that wraps an existing bit vector.
	 *
	 * @param enumEntries The entries of the enum [E].
	 * @param elements The bit vector to wrap.
	 *
	 * @throws IllegalArgumentException If the provided elements array has too many elements for the given enum.
	 * @throws IllegalArgumentException If the provided elements array has bits set that are not a part of the enum.
	 */
	constructor(enumEntries: EnumEntries<E>, elements: ImmutableLongArray) : this(enumEntries) {
		require(elements.size <= arraySize) { "The provided elements array has too many elements for the given enum." }
		require(elements.last() and (-1L ushr (64 - (enumEntries.size and 63))) == 0L) { "The provided elements array has bits set that are not a part of the enum." }
		this.elements = elements
		calculateSize()
	}

	/**
	 * Alternative constructor that wraps an existing [ImmutableJumboEnumSet].
	 *
	 * @param set The set to wrap.
	 */
	constructor(set: ImmutableJumboEnumSet<E>) : this(set.enumEntries) {
		this.elements = set.elements // No need to copy the array since it's already immutable and can't be modified
		this.size = set.size
	}

	/**
	 * Alternative constructor that wraps an existing [JumboEnumSet].
	 *
	 * Changes to [set] will not be reflected in this set.
	 *
	 * @param set The set to wrap.
	 */
	constructor(set: JumboEnumSet<E>) : this(set.enumEntries) {
		this.elements = set.elements.toImmutableArray() // Copy the array to ensure immutability
		this.size = set.size
	}

	/**
	 * Calculates and sets the size of this set.
	 */
	private fun calculateSize() {
		size = elements.sumOf(Long::countOneBits)
	}

	override fun contains(element: E): Boolean {
		val index = element.ordinal.arrayIndex()
		return elements[index] and (1L shl element.ordinal) != 0L
	}

	override fun iterator(): EnumSetIterator<E> = JumboEnumSetIterator()

	/**
	 * An iterator over the elements of this set.
	 * @property unseenIndex The index corresponding to [unseen] in the [elements] array.
	 * @property unseen A bit vector representing the elements not yet returned by this iterator.
	 * @property lastReturned The bit representing the last element returned by this iterator but not removed, or 0 if no such element exists.
	 * @property lastReturnedIndex The index corresponding to [lastReturned] in the [elements] array.
	 */
	private inner class JumboEnumSetIterator : EnumSetIterator<E> {
		private var unseenIndex = 0

		private var unseen = elements[unseenIndex]

		private var lastReturned = 0L

		private var lastReturnedIndex = 0

		override fun hasNext(): Boolean {
			while (unseen == 0L && unseenIndex < elements.size - 1)
				unseen = elements[++unseenIndex]
			return unseen != 0L
		}

		override fun next(): E {
			if (!hasNext()) throw NoSuchElementException()
			lastReturned = unseen and -unseen
			lastReturnedIndex = unseenIndex
			unseen -= lastReturned
			return enumEntries[(lastReturnedIndex shl 6) + lastReturned.countTrailingZeroBits()]
		}
	}

	private inline fun Int.arrayIndex() = this ushr 6

	// Necessary override because of the array content comparison
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ImmutableJumboEnumSet<*>) return false
		if (other.javaClass != this.javaClass) return false
		if (!super.equals(other)) return false

		if (arraySize != other.arraySize) return false
		if (size != other.size) return false
		if (!elements.equals(other.elements)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + arraySize
		result = 31 * result + size
		result = 31 * result + elements.hashCode()
		return result
	}

	override fun mutableCopyOf(): JumboEnumSet<E> = JumboEnumSet(this)

	override fun immutableCopyOf(): ImmutableJumboEnumSet<E> = ImmutableJumboEnumSet(this)

	companion object {
		/**
		 * Factory method to create an immutable set from an array of elements.
		 *
		 * @param enumEntries The entries of the enum [E].
		 * @param elements The bit vector to wrap. A copy of the array will be made to ensure immutability.
		 */
		fun <E : Enum<E>> fromArray(enumEntries: EnumEntries<E>, elements: LongArray): ImmutableJumboEnumSet<E> {
			// This can't be a constructor since [LongArray] and [ImmutableLongArray] both compile to `long[]`, which causes a signature clash.
			val arraySize = (enumEntries.size + 63) ushr 6
			require(elements.size <= arraySize) { "The provided elements array has too many elements for the given enum." }

			val newElements = buildImmutableLongArray(arraySize) {
				for (index in elements.indices) {
					if (index < arraySize - 1) {
						val element = elements[index]
						add(element)
					} else {
						val element = elements[index] and (-1L ushr (64 - (enumEntries.size and 63))) // Mask out the bits that are not part of the enum just in case
						add(element)
						break // Functionally useless since elements can be only as big as arraySize, so this is the last index anyway, but it clarifies the intent
					}
				}
			}
			return ImmutableJumboEnumSet(enumEntries, newElements)
		}
	}
}