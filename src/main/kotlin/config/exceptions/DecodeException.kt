package me.ancientri.rimelib.config.exceptions

class DecodeException(message: String, cause: Throwable? = null) : Exception(message, cause) {
	constructor(cause: Throwable? = null) : this("Failed to decode config object!", cause)
}
