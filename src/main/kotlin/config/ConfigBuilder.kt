package me.ancientri.rimelib.config

/**
 * A builder interface for creating instances of a config.
 *
 * Implementations are generated with the `@Config` annotation.
 *
 * @param C the type of the config to be built.
 */
fun interface ConfigBuilder<C: Any> {

	/**
	 * Creates a new instance of the config.
	 */
	fun build(): C
}