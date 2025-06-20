package me.ancientri.rimelib

import me.ancientri.rimelib.util.LoggerFactory
import me.ancientri.rimelib.util.command.register
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.util.Identifier

object RimeLib {
	@Deprecated("This method is called by the Fabric Loader. Do not call this method manually.", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith(""))
	fun init() {
		Initializer // Initialize features.
		CommandRegistrationCallback.EVENT.register(NAMESPACE) {
			literal("asd") executes {
				it.source
			}
		}
	}

	fun identifier(path: String): Identifier = Identifier.of(NAMESPACE, path)

	const val MOD_NAME = "RimeLib"
	const val NAMESPACE = "rimelib"

	val loggerFactory = LoggerFactory(MOD_NAME)
}
