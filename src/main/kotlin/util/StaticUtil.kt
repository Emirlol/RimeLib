package me.ancientri.rimelib.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

inline val client: MinecraftClient @Environment(EnvType.CLIENT) get() = MinecraftClient.getInstance()

inline val player: ClientPlayerEntity? @Environment(EnvType.CLIENT) get() = client.player

/**
 * The Fabric Loader instance.
 *
 * This is a utility property to skip the need to call `FabricLoader.getInstance()` every time.
 */
inline val FabricLoader: FabricLoader get() = net.fabricmc.loader.api.FabricLoader.getInstance()