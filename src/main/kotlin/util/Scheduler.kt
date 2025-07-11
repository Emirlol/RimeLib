package me.ancientri.rimelib.util

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectLists
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.ancientri.rimelib.RimeLib
import me.ancientri.rimelib.util.Scheduler.AsynchronousTask.Companion.runAsyncTask
import me.ancientri.rimelib.util.Scheduler.SynchronousTask.Companion.runSyncTask
import me.ancientri.rimelib.util.events.ClientTickable
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import java.util.concurrent.CancellationException

typealias AsyncTask = suspend CoroutineScope.() -> Unit
typealias SyncTask = () -> Unit

@Environment(EnvType.CLIENT)
object Scheduler : ClientTickable() {
	private var currentTick: UInt = 0u
	private val LOGGER = RimeLib.loggerFactory.createLogger(this)
	private var tasks: MutableList<Task> = ObjectLists.synchronize(ObjectArrayList())

	override fun tick(client: MinecraftClient) {
		synchronized(tasks) {
			val iterator = tasks.listIterator()
			while (iterator.hasNext()) {
				val task = iterator.next()
				if (task.ticks > currentTick) continue

				task.execute()
				iterator.remove()

				if (task.cyclicDelay > 0u) {
					val nextExecutionTick = currentTick + task.cyclicDelay
					when (task) {
						is AsynchronousTask -> iterator.add(task.copy(nextExecutionTick))
						is SynchronousTask -> iterator.add(task.copy(nextExecutionTick))
					}
				}
			}
		}
		currentTick++
	}

	/**
	 * Schedules a task to run synchronously in the main thread after a certain number of ticks.
	 * @param delay The number of ticks to wait before running the task.
	 * @param task The task to run.
	 */
	fun schedule(delay: UInt, task: () -> Unit) {
		// This will still add the task to the list even if the delay is 0, perhaps executing in the next tick if the tick has already happened.
		// This is better than running the task immediately, because then the task would be executed in the calling thread, which is not what we want.
		tasks += SynchronousTask(currentTick + delay, block = task)
	}

	/**
	 * Schedules a task to run asynchronously after a certain number of ticks.
	 * @param delay The number of ticks to wait before running the task.
	 * @param task The task to run.
	 */
	fun scheduleAsync(delay: UInt, task: suspend CoroutineScope.() -> Unit) {
		tasks += AsynchronousTask(currentTick + delay, block = task)
	}

	/**
	 * Schedules a task to run synchronously after a certain number of ticks.
	 * @param delay The number of ticks to wait before running the task for the first time.
	 * @param ticks The number of ticks to wait before running the task again.
	 * @param task The task to run.
	 */
	fun scheduleCyclic(delay: UInt, ticks: UInt, task: () -> Unit) {
		require(ticks > 0u) { "Cyclic tasks must have a tick delay greater than 0" }
		if (delay == 0u) runSyncTask(task)
		tasks += SynchronousTask(currentTick + delay, ticks, task)
	}

	/**
	 * Schedules a task to run asynchronously after a certain number of ticks.
	 * @param delay The number of ticks to wait before running the task for the first time.
	 * @param ticks The number of ticks to wait before running the task again.
	 * @param task The task to run.
	 */
	fun scheduleCyclicAsync(delay: UInt, ticks: UInt, task: suspend CoroutineScope.() -> Unit) {
		require(ticks > 0u) { "Cyclic tasks must have a tick delay greater than 0" }
		if (delay == 0u) runAsyncTask(task)
		tasks += AsynchronousTask(currentTick + delay, ticks, task)
	}

	interface Task {
		val ticks: UInt
		val cyclicDelay: UInt
		fun execute()
	}

	data class AsynchronousTask(override val ticks: UInt, override val cyclicDelay: UInt = 0u, val block: AsyncTask) : Task {
		override fun execute() = runAsyncTask(block)

		companion object {
			fun runAsyncTask(task: AsyncTask) {
				CoroutineUtil.globalJob
					.launch(block = task)
					.invokeOnCompletion { result ->
						when (result) {
							null -> {} // Job completed normally
							is CancellationException -> LOGGER.error("Task cancelled, message: {}, cause: {}", result.message, result.cause)
							else -> LOGGER.error("Task failed, message: {}, cause: {}", result.message, result.cause)
						}
					}
			}
		}
	}

	data class SynchronousTask(override val ticks: UInt, override val cyclicDelay: UInt = 0u, val block: SyncTask) : Task {
		override fun execute() = runSyncTask(block)

		companion object {
			fun runSyncTask(task: SyncTask) {
				try {
					task()
				} catch (e: Exception) {
					LOGGER.error("Task failed, message: {}, cause: {}", e.message, e.cause)
				}
			}
		}
	}
}