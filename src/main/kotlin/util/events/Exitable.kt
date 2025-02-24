package me.rime.rimelib.util.events

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient

/**
 * A convenience class that will have its exit method called when the client is stopping.
 *
 * Automatically registers [exit] to [ClientLifecycleEvents.CLIENT_STOPPING].
 */
@Environment(EnvType.CLIENT)
abstract class Exitable() {
	init {
		ClientLifecycleEvents.CLIENT_STOPPING.register(::exit)
	}

	abstract fun exit(client: MinecraftClient)
}
