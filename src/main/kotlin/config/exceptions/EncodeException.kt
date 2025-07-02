package me.ancientri.rimelib.config.exceptions

class EncodeException(message: String, cause: Throwable? = null) : Exception(message, cause) {
	constructor(cause: Throwable? = null) : this("Failed to encode config object!", cause)
}