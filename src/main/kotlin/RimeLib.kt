package me.rime.rimelib

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import net.minecraft.util.Identifier

@OptIn(ExperimentalSerializationApi::class)
object RimeLib {
	@Deprecated("This method is called by the Fabric Loader. Do not call this method manually.", level = DeprecationLevel.ERROR)
	fun init() {
//		Initializer // Initialize features.
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
