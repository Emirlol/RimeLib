package me.ancientri.rimelib

import me.ancientri.rimelib.util.CoroutineUtil
import me.ancientri.rimelib.util.LoggerFactory
import me.ancientri.rimelib.util.scheduler.Scheduler
import net.minecraft.util.Identifier

object RimeLib {
	@Deprecated("This method is called by the Fabric Loader. Do not call this method manually.", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith(""))
	fun init() {
		// Ensure objects are initialized
		CoroutineUtil
		Scheduler
	}

	fun identifier(path: String): Identifier = Identifier.of(NAMESPACE, path)

	const val MOD_NAME = "RimeLib"
	const val NAMESPACE = "rimelib"

	val loggerFactory = LoggerFactory(MOD_NAME)
}
