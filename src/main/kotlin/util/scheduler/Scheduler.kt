package me.ancientri.rimelib.util.scheduler

import kotlinx.coroutines.CoroutineScope
import me.ancientri.rimelib.util.FabricLoader
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import java.util.*

object Scheduler {
	@Volatile
	private var currentTick: ULong = 0uL
	private val taskQueue = PriorityQueue<ScheduledTask<*>>()
	private val lock = Any()

	init {
		when (FabricLoader.environmentType) {
			EnvType.CLIENT -> ClientTickEvents.END_CLIENT_TICK.register { tick() }
			EnvType.SERVER -> ServerTickEvents.END_SERVER_TICK.register { tick() }
		}
	}

	private fun tick() {
		synchronized(lock) {
			while (taskQueue.peek()?.executionTick?.let { it <= currentTick } == true) {
				val task = taskQueue.poll()
				if (task.isCancelled) continue

				task.execute()

				if (task.isCyclic()) taskQueue += task.reschedule(currentTick)
			}
		}
		currentTick++
	}

	/**
	 * Schedules a task to be executed synchronously on the main thread after the specified number of [delayTicks].
	 * The task will be executed periodically if [periodTicks] is greater than `0`.
	 * @param delayTicks The number of ticks to wait before executing the task.
	 * @param periodTicks The number of ticks to wait before executing the task again. If `0`, the task will not be repeated.
	 * @param task The task to be executed synchronously.
	 * @return A [ScheduledTask] that can be used to cancel the task or check its status.
	 */
	fun <T> schedule(delayTicks: UInt, periodTicks: UInt = 0u, task: () -> T): ScheduledTask<T> = createTask(delayTicks, periodTicks, TaskExecutor.Sync(task))

	/**
	 * Schedules a task to be executed asynchronously on the [global job][me.ancientri.rimelib.util.CoroutineUtil.globalJob] after the specified number of [delayTicks].
	 * The task will be executed periodically if [periodTicks] is greater than `0`.
	 * This method is intended for tasks that are not dependent on the main thread, such as network requests or heavy computations.
	 * @param delayTicks The number of ticks to wait before executing the task.
	 * @param periodTicks The number of ticks to wait before executing the task again. If `0`, the task will not be repeated.
	 * @param task The task to be executed asynchronously. This should be a suspend function.
	 * @return A [ScheduledTask] that can be used to cancel the task or check its status.
	 */
	fun <T> scheduleAsync(delayTicks: UInt, periodTicks: UInt = 0u, task: suspend CoroutineScope.() -> T): ScheduledTask<T> = createTask(delayTicks, periodTicks, TaskExecutor.Async(task))

	private fun <T> createTask(
		delayTicks: UInt,
		periodTicks: UInt,
		executor: TaskExecutor<T>
	): ScheduledTask<T> {
		lateinit var task : ScheduledTask<T>
		synchronized(lock) {
			task = ScheduledTask(currentTick + delayTicks.toULong(), periodTicks, executor)
			taskQueue += task
		}
		return task
	}
}