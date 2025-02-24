package me.rime.rimelib.util.events

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.MinecraftServer

/**
 * A class that will be ticked every server tick.
 *
 * Automatically registers [tick] to [ServerTickEvents.END_SERVER_TICK].
 */
@Environment(EnvType.SERVER)
abstract class ServerTickable() {
	init {
		ServerTickEvents.END_SERVER_TICK.register(::tick)
	}

	abstract fun tick(client: MinecraftServer)
}