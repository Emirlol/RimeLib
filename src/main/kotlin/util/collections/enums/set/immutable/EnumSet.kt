package me.ancientri.rimelib.util.collections.enums.set.immutable

import me.ancientri.rimelib.util.collections.enums.set.mutable.MutableEnumSet
import org.jetbrains.annotations.Unmodifiable
import kotlin.enums.EnumEntries

abstract class EnumSet<E : Enum<E>>(
	@Transient internal val enumEntries: EnumEntries<E>
) : AbstractSet<E>(), Cloneable {
	abstract fun mutableCopyOf(): MutableEnumSet<E>

	abstract fun immutableCopyOf(): @Unmodifiable EnumSet<E>

	interface EnumSetIterator<E : Enum<E>> : Iterator<E>
}