package me.ancientri.rimelib.util.collections.enums.set.mutable

import com.danrusu.pods4k.immutableArrays.toMutableArray
import it.unimi.dsi.fastutil.longs.LongArrays
import me.ancientri.rimelib.util.collections.enums.set.immutable.ImmutableJumboEnumSet
import java.util.*
import kotlin.enums.EnumEntries

/**
 * A set implementation that uses a bit vector to store the elements.
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
class JumboEnumSet<E : Enum<E>>(enumEntries: EnumEntries<E>) : MutableEnumSet<E>(enumEntries), MutableSet<E> {
	private val arraySize = (this.enumEntries.size + 63) ushr 6 // (entries.size / 64) + 1

	override var size: Int = 0
		private set

	// This has a public getter to allow concise de/serialization
	var elements: LongArray = LongArray(arraySize)
		private set

	/**
	 * Alternative constructor that wraps an existing bit vector.
	 *
	 * The [elements] array will be trimmed to the correct size if it is too large, and left-padded with zeroes if it is too small.
	 *
	 * @param enumEntries The entries of the enum [E].
	 * @param elements The bit vector to wrap.
	 */
	constructor(enumEntries: EnumEntries<E>, elements: LongArray) : this(enumEntries) {
		// Ensure the array is the correct size so that Array.indices doesn't cause any issues
		this.elements = LongArrays.ensureCapacity(LongArrays.trim(elements, arraySize), arraySize)
		calculateSize()
	}

	/**
	 * Alternative constructor that wraps an existing [JumboEnumSet].
	 *
	 * Changes to [set] will not affect this set.
	 *
	 * @param set The set to wrap.
	 */
	constructor(set: JumboEnumSet<E>) : this(set.enumEntries) {
		this.elements = set.elements.copyOf()
		this.size = set.size
	}

	/**
	 * Alternative constructor that wraps an existing [ImmutableJumboEnumSet].
	 *
	 * @param set The set to wrap.
	 */
	constructor(set: ImmutableJumboEnumSet<E>) : this(set.enumEntries) {
		this.elements = set.elements.toMutableArray()
		this.size = set.size
	}

	//region Modification Operations

	//region Single Operations

	override fun add(element: E): Boolean {
		val ordinal = element.ordinal
		val index = ordinal.arrayIndex()
		val oldElements = elements[index]
		elements[index] = elements[index] or (1L shl ordinal)
		val changed = oldElements != elements[index]
		if (changed) size++
		return changed
	}

	override fun remove(element: E): Boolean {
		val ordinal = element.ordinal
		val index = ordinal.arrayIndex()
		val oldElements = elements[index]
		elements[index] = elements[index] and (1L shl ordinal).inv()
		val changed = oldElements != elements[index]
		if (changed) size--
		return changed
	}

	//endregion Single Operations

	//region Bulk Operations

	override fun complement() {
		for (i in elements.indices) {
			elements[i] = elements[i].inv()
		}
		// Mask out the unused bits in the last element
		elements[elements.lastIndex] = elements[elements.lastIndex] and (-1L ushr (64 - (enumEntries.size and 63)))
		calculateSize()
	}

	override fun addAll(elements: Collection<E>): Boolean {
		if (elements.isEmpty()) return false

		val oldSize = size
		if (elements is JumboEnumSet) {
			for (i in elements.indices) {
				this.elements[i] = this.elements[i] or elements.elements[i]
			}
			val changed = size != oldSize
			if (changed) calculateSize()
			return changed
		} else {
			for (element in elements) add(element)
			return size != oldSize
		}
	}

	override fun clear() {
		Arrays.fill(elements, 0L)
		size = 0
	}

	override fun removeAll(elements: Collection<E>): Boolean {
		if (elements.isEmpty()) return false

		val oldSize = size
		if (elements is JumboEnumSet) {
			for (i in elements.indices) {
				this.elements[i] = this.elements[i] and elements.elements[i].inv()
			}
			return calculateSize(oldSize)
		} else {
			for (element in elements) remove(element)
			return size != oldSize
		}
	}

	override fun retainAll(elements: Collection<E>): Boolean {
		if (elements.isEmpty()) {
			val changed = isNotEmpty()
			clear()
			return changed
		}

		val oldElements = LongArrays.copy(this.elements)
		if (elements is JumboEnumSet) {
			for (i in elements.indices) {
				this.elements[i] = oldElements[i] and elements.elements[i]
			}
		} else {
			clear()
			for (element in elements) {
				val index = element.ordinal.arrayIndex()
				this.elements[index] = oldElements[index] or (1L shl element.ordinal)
			}
		}
		val changed = !oldElements.contentEquals(this.elements)
		if (changed) calculateSize()
		return changed
	}

	/**
	 * Calculates and sets the size of this set.
	 */
	private fun calculateSize() {
		size = elements.sumOf(Long::countOneBits)
	}

	/**
	 * Calculates the size of this set and returns whether it has changed.
	 * @param oldSize The previous size of this set.
	 * @return Whether the size has changed.
	 */
	private fun calculateSize(oldSize: Int): Boolean {
		calculateSize()
		return size != oldSize
	}

	//endregion Bulk Operations

	//endregion Modification Operations

	override fun contains(element: E): Boolean {
		val index = element.ordinal.arrayIndex()
		return elements[index] and (1L shl element.ordinal) != 0L
	}

	override fun iterator(): MutableEnumSetIterator<E> = JumboEnumSetIterator()

	/**
	 * An iterator over the elements of this set.
	 * @property unseenIndex The index corresponding to [unseen] in the [elements] array.
	 * @property unseen A bit vector representing the elements not yet returned by this iterator.
	 * @property lastReturned The bit representing the last element returned by this iterator but not removed, or 0 if no such element exists.
	 * @property lastReturnedIndex The index corresponding to [lastReturned] in the [elements] array.
	 */
	private inner class JumboEnumSetIterator : MutableEnumSetIterator<E> {
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

		override fun remove() {
			check(lastReturned != 0L)
			val oldElements = elements[lastReturnedIndex]
			elements[lastReturnedIndex] = oldElements and lastReturned.inv()
			if (oldElements != elements[lastReturnedIndex]) size--
			lastReturned = 0L
		}
	}

	override fun mutableCopyOf(): JumboEnumSet<E> = JumboEnumSet(this)

	override fun immutableCopyOf(): ImmutableJumboEnumSet<E> = ImmutableJumboEnumSet(this)

	private inline fun Int.arrayIndex() = this ushr 6

	// Necessary override because of the array content comparison
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is JumboEnumSet<*>) return false
		if (!super.equals(other)) return false

		if (arraySize != other.arraySize) return false
		if (size != other.size) return false
		if (!elements.contentEquals(other.elements)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + arraySize
		result = 31 * result + size
		result = 31 * result + elements.contentHashCode()
		return result
	}
}