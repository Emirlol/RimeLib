package me.ancientri.rimelib.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer

inline val client: Minecraft @Environment(EnvType.CLIENT) get() = Minecraft.getInstance()

inline val player: LocalPlayer? @Environment(EnvType.CLIENT) get() = client.player

/**
 * The Fabric Loader instance.
 *
 * This is a utility property to skip the need to call `FabricLoader.getInstance()` every time.
 */
inline val FabricLoader: FabricLoader get() = net.fabricmc.loader.api.FabricLoader.getInstance()