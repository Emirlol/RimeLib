package me.rime.rimelib

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import me.rime.rimelib.ui.debug.DebugScreen
import me.rime.rimelib.util.client
import me.rime.rimelib.util.fabricLoader
import me.rime.rimelib.util.register
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.minecraft.util.Identifier

@OptIn(ExperimentalSerializationApi::class)
object RimeLib {
	@Deprecated("This method is called by the Fabric Loader. Do not call this method manually.", level = DeprecationLevel.ERROR)
	fun init() {
//		Initializer // Initialize features.
		when (fabricLoader.environmentType) {
			EnvType.CLIENT -> initClient()
			EnvType.SERVER -> initServer()
			null -> throw IllegalStateException("Environment type is null")
		}
	}

	private fun initClient() {
		ClientCommandRegistrationCallback.EVENT.register(NAMESPACE) {
			literal("openScreen") {
				executes {
					client.send {
						client.setScreen(DebugScreen())
					}
				}
			}
		}
	}

	private fun initServer() {

	}

	fun identifier(path: String): Identifier = Identifier.of(NAMESPACE, path)

	const val MOD_ID = "rimelib"
	const val NAMESPACE = "rimelib"
	val JSON_COMPACT = Json {
		ignoreUnknownKeys = true
		explicitNulls = true
	}
	val JSON = Json {
		prettyPrint = true
		prettyPrintIndent = "\t"
		ignoreUnknownKeys = true
		explicitNulls = true
	}
	val CBOR = Cbor {
		ignoreUnknownKeys = true
	}
}
