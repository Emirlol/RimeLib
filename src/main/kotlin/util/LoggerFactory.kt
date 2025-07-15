@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

/**
 * A [slf4j LoggerFactory][org.slf4j.LoggerFactory] wrapper that allows you to create loggers with a specific prefix using MDC (Mapped Diagnostic Context).
 *
 * This is meant to be kept in a single instance, so you can create it once with your mod's name and use it everywhere while keeping it obvious which mod the log is from.
 */
class LoggerFactory(private val prefix: String) {
	/**
	 * Creates a logger with the given name, prefixed with the provided prefix.
	 *
	 * @param name The name of the logger.
	 */
	fun createLogger(name: String): Logger = createMdcProxy(LoggerFactory.getLogger(name))

	/**
	 * Creates a logger with the name of the class.
	 *
	 * @param clazz The class to create the logger for.
	 */
	inline fun createLogger(clazz: KClass<*>): Logger = createLogger(clazz.qualifiedName!!)

	/**
	 * Creates a logger with the name of the class.
	 *
	 * @param clazz The class to create the logger for.
	 */
	inline fun createLogger(clazz: Class<*>): Logger = createLogger(clazz.name)

	/**
	 * Creates a logger with the name of the class.
	 *
	 * @param this The class to create the logger for.
	 */
	inline fun createLogger(`this`: Any): Logger = createLogger(`this`::class.qualifiedName!!)

	private fun createMdcProxy(logger: Logger): Logger = Proxy.newProxyInstance(
		logger.javaClass.classLoader,
		arrayOf(Logger::class.java)
	) { _, method, args ->
		val old = MDC.get(PREFIX_KEY)
		try {
			MDC.put(PREFIX_KEY, prefix)
			method.invoke(logger, *args ?: emptyArray())
		} finally {
			if (old != null) MDC.put(PREFIX_KEY, old)
			else MDC.remove(PREFIX_KEY)
		}
	} as Logger

	companion object {
		const val PREFIX_KEY = "prefix"
	}
}