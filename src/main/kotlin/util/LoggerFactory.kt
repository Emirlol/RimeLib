@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.LoggerConfig
import org.apache.logging.slf4j.Log4jLogger
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
	 * This logger will be set to DEBUG level in development environments (when launched via loom or with a debug flag) for convenience.
	 *
	 * @param name The name of the logger.
	 */
	fun createLogger(name: String): Logger {
		val logger = LoggerFactory.getLogger("$prefix | $name")

		if (Debug.isEnabled && logger is Log4jLogger) {
			val ctx = LogManager.getContext(false) as LoggerContext
			var loggerConfig = ctx.configuration.getLoggerConfig(logger.name)
			if (loggerConfig.name != logger.name) {
				loggerConfig = LoggerConfig(logger.name, Level.DEBUG, true)
				val rootLoggerConfig = ctx.configuration.rootLogger
				for ((key, appender) in rootLoggerConfig.appenders) {
					if (key != "SysOut" && key != "DebugFile" && key != "LatestFile") continue
					loggerConfig.addAppender(appender, Level.ALL, null)
				}
				ctx.configuration.addLogger(logger.name, loggerConfig)
			} else {
				loggerConfig.level = Level.DEBUG
			}
			ctx.updateLoggers()
		}
		return logger
	}

	/**
	 * Creates a logger with the name of the class.
	 *
	 * @param clazz The class to create the logger for.
	 */
	inline fun createLogger(clazz: KClass<*>): Logger = createLogger(clazz.simpleName!!)

	/**
	 * Creates a logger with the name of the class.
	 *
	 * @param clazz The class to create the logger for.
	 */
	inline fun createLogger(clazz: Class<*>): Logger = createLogger(clazz.simpleName)

	/**
	 * Creates a logger with the name of the class.
	 *
	 * @param this The class to create the logger for.
	 */
	inline fun createLogger(`this`: Any): Logger = createLogger(`this`::class.simpleName!!)
}