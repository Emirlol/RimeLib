@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util

inline fun unsupported(): Nothing = throw UnsupportedOperationException()

inline fun unsupported(cause: Throwable): Nothing = throw UnsupportedOperationException(cause)

inline fun unsupported(message: String): Nothing = throw UnsupportedOperationException(message)

inline fun unsupported(message: Any): Nothing = throw UnsupportedOperationException(message.toString())

inline fun unsupported(cause: Throwable, message: String): Nothing = throw UnsupportedOperationException(message, cause)

inline fun unsupported(cause: Throwable, lazyMessage: () -> String): Nothing = throw UnsupportedOperationException(lazyMessage(), cause)

inline fun unsupported(cause: Throwable, message: Any): Nothing = throw UnsupportedOperationException(message.toString(), cause)
