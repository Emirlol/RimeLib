package me.rime.rimelib.util

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import me.rime.rimelib.util.events.Exitable
import me.rime.symbols.init.AutoInit
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger

@AutoInit(-2)
object CoroutineUtil: Exitable() {
	val globalJob = CoroutineScope(SupervisorJob() + CoroutineName("RimeLib"))
	private val LOGGER: Logger = LogUtil.createLogger("RimeLib CoroutineUtil")

	override fun exit(client: MinecraftClient) {
		LOGGER.info("Cancelling all coroutines.")
		globalJob.cancel()
	}
}