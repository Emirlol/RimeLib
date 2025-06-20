package me.ancientri.rimelib.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * A [slf4j LoggerFactory][org.slf4j.LoggerFactory] wrapper that allows you to create loggers with a specific prefix.
 *
 * This is meant to be kept in a single instance, so you can create it once with your mod's name and use it everywhere while keeping it obvious which mod the log is from.
 */
class LoggerFactory(private val prefix: String) {
	/**
	 * Creates a logger with the given name, prefixed with the provided prefix.
	 *
	 * @param name The name of the logger.
	 */
	fun createLogger(name: String): Logger = LoggerFactory.getLogger("$prefix | $name")

	/**
	 * Creates a logger with the name of the class.
	 *
	 * @param clazz The class to create the logger for.
	 */
	fun createLogger(clazz: KClass<*>): Logger = createLogger(clazz.simpleName!!)

	/**
	 * Creates a logger with the name of the class.
	 *
	 * @param clazz The class to create the logger for.
	 */
	fun createLogger(clazz: Class<*>): Logger = createLogger(clazz.simpleName)

	/**
	 * Creates a logger with the name of the class.
	 *
	 * @param this The class to create the logger for.
	 */
	fun createLogger(`this`: Any): Logger = createLogger(`this`::class.simpleName!!)
}