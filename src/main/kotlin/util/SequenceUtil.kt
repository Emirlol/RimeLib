package me.ancientri.rimelib.util

/**
 * Interleaves the elements of this sequence with the given element.
 *
 * @param separator The element to interleave with.
 * @param T The type of the elements in this sequence.
 */
fun <T> Sequence<T>.interleaveWith(separator: T): Sequence<T> = sequence {
	val iterator = iterator()
	if (iterator.hasNext()) {
		yield(iterator.next())
	}
	while (iterator.hasNext()) {
		yield(separator)
		yield(iterator.next())
	}
}
