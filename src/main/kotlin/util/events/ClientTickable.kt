package me.ancientri.rimelib.util.events

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient

/**
 * A class that will be ticked every client tick.
 *
 * Automatically registers [tick] to [ClientTickEvents.END_CLIENT_TICK].
 */
@Environment(EnvType.CLIENT)
abstract class ClientTickable {
	init {
		ClientTickEvents.END_CLIENT_TICK.register(::tick)
	}

	abstract fun tick(client: MinecraftClient)
}