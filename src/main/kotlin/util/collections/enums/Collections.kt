package me.rime.rimelib.util.collections.enums

import me.rime.rimelib.util.collections.enums.set.immutable.EnumSet
import me.rime.rimelib.util.collections.enums.set.immutable.ImmutableJumboEnumSet
import me.rime.rimelib.util.collections.enums.set.immutable.ImmutableRegularEnumSet
import me.rime.rimelib.util.collections.enums.set.mutable.JumboEnumSet
import me.rime.rimelib.util.collections.enums.set.mutable.MutableEnumSet
import me.rime.rimelib.util.collections.enums.set.mutable.RegularEnumSet
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries

// These functions are all inline to allow for reified type parameters and to allow for the use of the enumEntries function to get the enum values.
// Without that, we'd need the enum class/entries to be passed in as a parameter, which would be less convenient.

// region EnumSets

//   region Generic
/**
 * Creates a new [MutableEnumSet] of the appropriate type for the given enum type [E] with no elements.
 */
inline fun <reified E : Enum<E>> mutableEnumSetOf(): MutableEnumSet<E> = enumEntries<E>().let {
	if (it.size <= 64) regularEnumSetOf(it) else jumboEnumSetOf(it)
}

/**
 * Creates a new [(Immutable) EnumSet][EnumSet] of the appropriate type for the given enum type [E] with no elements.
 */
inline fun <reified E : Enum<E>> enumSetOf(): EnumSet<E> = enumEntries<E>().let {
	if (it.size <= 64) immutableRegularEnumSetOf() else immutableJumboEnumSetOf()
}

/**
 * Creates a new [MutableEnumSet] of the appropriate type for the given enum type [E] with the given elements.
 */
inline fun <reified E : Enum<E>> mutableEnumSetOf(vararg elements: E): MutableEnumSet<E> = enumEntries<E>().let {
	(if (it.size <= 64) regularEnumSetOf(it) else jumboEnumSetOf(it)).apply { addAll(elements) }
}

/**
 * Creates a new [(Immutable) EnumSet][EnumSet] of the appropriate type for the given enum type [E] with the given elements.
 */
inline fun <reified E : Enum<E>> enumSetOf(vararg elements: E): EnumSet<E> = enumEntries<E>().let {
	if (it.size <= 64) immutableRegularEnumSetOf(it, elements.asList()) else immutableJumboEnumSetOf(it, elements.asList())
}

/**
 * Creates a new [MutableEnumSet] of the appropriate type for the given enum type [E] with the given elements.
 */
inline fun <reified E : Enum<E>> mutableEnumSetOf(elements: Collection<E>): MutableEnumSet<E> = enumEntries<E>().let {
	(if (it.size <= 64) regularEnumSetOf(it) else jumboEnumSetOf(it)).apply { addAll(elements) }
}

/**
 * Creates a new [(Immutable) EnumSet][EnumSet] of the appropriate type for the given enum type [E] with the given elements.
 */
inline fun <reified E : Enum<E>> enumSetOf(elements: Collection<E>): EnumSet<E> = enumEntries<E>().let {
	if (it.size <= 64) immutableRegularEnumSetOf(it, elements) else immutableJumboEnumSetOf(it, elements)
}

//   endregion Generic

//   region Typed

/**
 * Creates a new [RegularEnumSet] for the given enum type [E] with no elements.
 *
 * @throws IllegalArgumentException If the enum type [E] has more than 64 elements.
 */
inline fun <reified E : Enum<E>> regularEnumSetOf(enumEntries: EnumEntries<E> = enumEntries()): RegularEnumSet<E> = RegularEnumSet(enumEntries)

/**
 * Creates a new [JumboEnumSet] for the given enum type [E] with no elements.
 */
inline fun <reified E : Enum<E>> jumboEnumSetOf(enumEntries: EnumEntries<E> = enumEntries()): JumboEnumSet<E> = JumboEnumSet(enumEntries)

/**
 * Creates a new [RegularEnumSet] for the given enum type [E] with the given elements.
 *
 * @throws IllegalArgumentException If the enum type [E] has more than 64 elements.
 */
inline fun <reified E : Enum<E>> regularEnumSetOf(enumEntries: EnumEntries<E> = enumEntries(), vararg elements: E): RegularEnumSet<E> = RegularEnumSet(enumEntries).apply { addAll(elements) }

/**
 * Creates a new [JumboEnumSet] for the given enum type [E] with the given elements.
 */
inline fun <reified E : Enum<E>> jumboEnumSetOf(enumEntries: EnumEntries<E> = enumEntries(), vararg elements: E): JumboEnumSet<E> = JumboEnumSet(enumEntries).apply { addAll(elements) }

/**
 * Creates a new [RegularEnumSet] for the given enum type [E] with the given elements.
 *
 * @throws IllegalArgumentException If the enum type [E] has more than 64 elements.
 */
inline fun <reified E : Enum<E>> regularEnumSetOf(enumEntries: EnumEntries<E> = enumEntries(), elements: Collection<E>): RegularEnumSet<E> = RegularEnumSet(enumEntries).apply { addAll(elements) }

/**
 * Creates a new [JumboEnumSet] for the given enum type [E] with the given elements.
 */
inline fun <reified E : Enum<E>> jumboEnumSetOf(enumEntries: EnumEntries<E> = enumEntries(), elements: Collection<E>): JumboEnumSet<E> = JumboEnumSet(enumEntries).apply { addAll(elements) }

/**
 * Creates a new [ImmutableRegularEnumSet] for the given enum type [E] with no elements.
 *
 * @throws IllegalArgumentException If the enum type [E] has more than 64 elements.
 */
inline fun <reified E : Enum<E>> immutableRegularEnumSetOf(enumEntries: EnumEntries<E> = enumEntries()): ImmutableRegularEnumSet<E> = ImmutableRegularEnumSet(enumEntries)

/**
 * Creates a new [ImmutableJumboEnumSet] for the given enum type [E] with no elements.
 */
inline fun <reified E : Enum<E>> immutableJumboEnumSetOf(enumEntries: EnumEntries<E> = enumEntries()): ImmutableJumboEnumSet<E> = ImmutableJumboEnumSet(enumEntries)

/**
 * Creates a new [ImmutableRegularEnumSet] for the given enum type [E] with the given elements.
 *
 * @throws IllegalArgumentException If the enum type [E] has more than 64 elements.
 */
inline fun <reified E : Enum<E>> immutableRegularEnumSetOf(enumEntries: EnumEntries<E> = enumEntries(), vararg elements: E): ImmutableRegularEnumSet<E> = RegularEnumSet(enumEntries).apply { addAll(elements) }.immutableCopyOf()

/**
 * Creates a new [ImmutableJumboEnumSet] for the given enum type [E] with the given elements.
 */
inline fun <reified E : Enum<E>> immutableJumboEnumSetOf(enumEntries: EnumEntries<E> = enumEntries(), vararg elements: E): ImmutableJumboEnumSet<E> = JumboEnumSet(enumEntries).apply { addAll(elements) }.immutableCopyOf()

/**
 * Creates a new [ImmutableRegularEnumSet] for the given enum type [E] with the given elements.
 *
 * @throws IllegalArgumentException If the enum type [E] has more than 64 elements.
 */
inline fun <reified E : Enum<E>> immutableRegularEnumSetOf(enumEntries: EnumEntries<E> = enumEntries(), elements: Collection<E>): ImmutableRegularEnumSet<E> = RegularEnumSet(enumEntries).apply { addAll(elements) }.immutableCopyOf()

/**
 * Creates a new [ImmutableJumboEnumSet] for the given enum type [E] with the given elements.
 */
inline fun <reified E : Enum<E>> immutableJumboEnumSetOf(enumEntries: EnumEntries<E> = enumEntries(), elements: Collection<E>): ImmutableJumboEnumSet<E> = JumboEnumSet(enumEntries).apply { addAll(elements) }.immutableCopyOf()
//   endregion Typed

// endregion EnumSets