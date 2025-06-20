package me.ancientri.rimelib.util

import kotlinx.coroutines.*
import me.ancientri.rimelib.RimeLib
import me.ancientri.rimelib.util.events.Exitable
import me.ancientri.symbols.init.AutoInit
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger

@AutoInit(-2)
object CoroutineUtil: Exitable() {
	val globalJob = CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineName(RimeLib.MOD_NAME))
	private val LOGGER: Logger = RimeLib.loggerFactory.createLogger(this)

	override fun exit(client: MinecraftClient) {
		LOGGER.info("Cancelling all coroutines.")
		globalJob.cancel()
	}
}