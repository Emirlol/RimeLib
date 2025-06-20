package me.ancientri.rimelib.util.events

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

/**
 * Convenience class for rendering things in the world.
 *
 * Automatically registers [render] to [WorldRenderEvents.AFTER_TRANSLUCENT].
 */
@Environment(EnvType.CLIENT)
abstract class WorldRenderable {
	init {
		WorldRenderEvents.AFTER_TRANSLUCENT.register(::render)
	}

	abstract fun render(context: WorldRenderContext)
}