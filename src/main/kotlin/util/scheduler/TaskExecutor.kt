package me.ancientri.rimelib.util.scheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.future.future
import me.ancientri.rimelib.util.CoroutineUtil
import java.util.concurrent.CompletableFuture

sealed interface TaskExecutor<T> {
	fun execute(completableFuture: CompletableFuture<T>)

	/**
	 * Executes the task synchronously on the main thread.
	 */
	class Sync<T>(private val block: () -> T) : TaskExecutor<T> {
		// This is called by the scheduler ticked on the render thread, so this is guaranteed to run on the render thread.
		override fun execute(completableFuture: CompletableFuture<T>) {
			runCatching(block)
				.onSuccess(completableFuture::complete)
				.onFailure(completableFuture::completeExceptionally)
		}
	}

	/**
	 * Executes the task asynchronously on the [global job][me.ancientri.rimelib.util.CoroutineUtil.globalJob].
	 */
	class Async<T>(private val block: suspend CoroutineScope.() -> T) : TaskExecutor<T> {
		@OptIn(ExperimentalCoroutinesApi::class)
		override fun execute(completableFuture: CompletableFuture<T>) {
			// The fact that we can't construct a CompletableFutureCoroutine from an existing CompletableFuture is just sad.
			// You're also not supposed to manually extend AbstractCoroutine, so I can't even make my own implementation of it
			CoroutineUtil.globalJob.future { block() }
				.whenComplete { result, exception ->
					when (exception) {
						null -> completableFuture.complete(result)
						else -> completableFuture.completeExceptionally(exception)
					}
				}
		}
	}
}