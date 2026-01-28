package me.ancientri.rimelib.util

import kotlinx.coroutines.*
import me.ancientri.rimelib.RimeLib
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger

object CoroutineUtil {
	val globalJob = CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineName(RimeLib.MOD_NAME))
	/**
	 * Client dispatcher for running tasks on the main Minecraft client thread.
	 *
	 * Example use:
	 * ```kotlin
	 * withContext(CoroutineUtil.clientDispatcher) {
	 * 	// Code to run on the main Minecraft client thread
	 * }
	 * ```
 	 */
	val clientDispatcher = client.asCoroutineDispatcher()

	private val LOGGER: Logger = RimeLib.loggerFactory.createLogger(this)

	init {
		ClientLifecycleEvents.CLIENT_STOPPING.register(::exit)
	}

	private fun exit(client: MinecraftClient) {
		LOGGER.info("Cancelling all coroutines.")
		globalJob.cancel()
	}
}