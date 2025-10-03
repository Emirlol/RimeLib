package me.ancientri.rimelib

import me.ancientri.rimelib.util.CoroutineUtil
import me.ancientri.rimelib.util.LoggerFactory
import me.ancientri.rimelib.util.scheduler.Scheduler

object RimeLib {
	@Deprecated("This method is called by the Fabric Loader. Do not call this method manually.", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith(""))
	fun init() {
		// Ensure objects are initialized
		CoroutineUtil
		Scheduler
	}

	internal const val MOD_NAME = "RimeLib"
	internal const val NAMESPACE = "rimelib"

	internal val loggerFactory = LoggerFactory(MOD_NAME)
}
