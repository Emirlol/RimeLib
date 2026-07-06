@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util

import it.unimi.dsi.fastutil.objects.ObjectLists
import me.ancientri.rimelib.mixins.accessors.CustomDataAccessor
import net.minecraft.core.component.DataComponentHolder
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.component.CustomData
import org.jetbrains.annotations.UnmodifiableView

/**
 * @return The nbt data of the [CustomData].
 *
 * This is a read-only view of the nbt tag to avoid copying for basic read-only operations.
 *
 * If you wish to modify the nbt, create a [copy][CustomData.copyTag] of the result instead.
 */
inline val CustomData.nbt get(): CompoundTag = (this as CustomDataAccessor).tag

/**
 * @return The custom nbt data of this [DataComponentHolder].
 *
 * This is a read-only view of the nbt tag to avoid copying for basic read-only operations.
 *
 * If you wish to modify the nbt, create a [copy][CustomData.copyTag] of the result instead.
 */
@Suppress("DEPRECATION")
inline val DataComponentHolder.customData: @UnmodifiableView CompoundTag get() = (components[DataComponents.CUSTOM_DATA] ?: CustomData.EMPTY).nbt

/**
 * @return The lore of the [DataComponentHolder] as a list of [Component] objects.
 *
 * This is a read-only view of the lore.
 */
inline val DataComponentHolder.lore: @UnmodifiableView List<Component> get() = components[DataComponents.LORE]?.lines ?: ObjectLists.emptyList()
