package me.ancientri.rimelib.util

import kotlinx.coroutines.*
import me.ancientri.rimelib.RimeLib
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger

object CoroutineUtil {
	val globalJob = CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineName(RimeLib.MOD_NAME))
	private val LOGGER: Logger = RimeLib.loggerFactory.createLogger(this)

	init {
		ClientLifecycleEvents.CLIENT_STOPPING.register(::exit)
	}

	private fun exit(client: MinecraftClient) {
		LOGGER.info("Cancelling all coroutines.")
		globalJob.cancel()
	}
}