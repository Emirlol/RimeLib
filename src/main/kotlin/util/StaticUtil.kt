package me.rime.rimelib.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

inline val client: MinecraftClient @Environment(EnvType.CLIENT) get() = MinecraftClient.getInstance()
inline val player: ClientPlayerEntity? @Environment(EnvType.CLIENT) get() = client.player
@JvmSuppressWildcards
inline val fabricLoader: FabricLoader get() = FabricLoader.getInstance()