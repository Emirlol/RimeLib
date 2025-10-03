@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util

import it.unimi.dsi.fastutil.objects.ObjectLists
import me.ancientri.rimelib.mixins.accessors.NbtComponentAccessor
import net.minecraft.component.ComponentHolder
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import org.jetbrains.annotations.UnmodifiableView

/**
 * @return The nbt data of the [NbtComponent].
 *
 * This is a read-only view of the nbt tag to avoid copying for basic read-only operations.
 *
 * If you wish to modify the nbt, create a [copy][NbtCompound.copy] of the result instead.
 */
@Suppress("CAST_NEVER_SUCCEEDS") // That's how accessors work, you dummy
inline val NbtComponent.nbt get(): NbtCompound = (this as NbtComponentAccessor).nbt

/**
 * @return The custom nbt data of this [ComponentHolder].
 *
 * This is a read-only view of the nbt tag to avoid copying for basic read-only operations.
 *
 * If you wish to modify the nbt, create a [copy][NbtCompound.copy] of the result instead.
 */
@Suppress("DEPRECATION")
inline val ComponentHolder.customData: @UnmodifiableView NbtCompound get() = (components[DataComponentTypes.CUSTOM_DATA] ?: NbtComponent.DEFAULT).nbt

/**
 * @return The lore of the [ComponentHolder] as a list of [Text] objects.
 *
 * This is a read-only view of the lore.
 */
inline val ComponentHolder.lore: @UnmodifiableView List<Text> get() = components[DataComponentTypes.LORE]?.lines ?: ObjectLists.emptyList()
