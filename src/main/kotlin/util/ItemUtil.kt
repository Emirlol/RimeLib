package me.ancientri.rimelib.util

import it.unimi.dsi.fastutil.objects.ObjectLists
import net.minecraft.component.ComponentHolder
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import org.jetbrains.annotations.UnmodifiableView

/**
 * This method exists to avoid copying the nbt when you only need to read it.
 *
 * **If you wish to modify the custom data nbt, create a [copy][NbtCompound.copy] of the result instead.**
 * @return The custom nbt data of the item stack. Pretend this is a read-only view of the nbt tag.
 */
@Suppress("DEPRECATION")
inline val ComponentHolder.customData: @UnmodifiableView NbtCompound get() = (components[DataComponentTypes.CUSTOM_DATA] ?: NbtComponent.DEFAULT).nbt

/**
 * @return The lore of the [ComponentHolder] as a list of [Text] objects.
 *         This is a read-only view of the lore, do not attempt to modify it.
 */
inline val ComponentHolder.lore: @UnmodifiableView List<Text> get() = components[DataComponentTypes.LORE]?.lines ?: ObjectLists.emptyList()
