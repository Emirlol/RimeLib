package me.rime.rimelib.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

object LogUtil {
	/**
	 * Wrapper around [LoggerFactory.getLogger] to unify all logger creations under this class.
	 *
	 * @param name The name of the logger.
	 */
	fun createLogger(name: String): Logger = LoggerFactory.getLogger(name)

	/**
	 * Creates a logger with the given name and a prefix.
	 *
	 * The format of the logger name is "$prefix | $name".
	 *
	 * @param name The name of the logger.
	 * @param prefix The prefix to prepend to the name.
	 */
	fun createLogger(name: String, prefix: String = "RimeLib"): Logger = LoggerFactory.getLogger("$prefix | $name")

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