package me.ancientri.rimelib.util.scheduler

import java.util.concurrent.CompletableFuture

data class ScheduledTask<T>(
	@Volatile internal var executionTick: ULong,
	private val periodTicks: UInt,
	private val executor: TaskExecutor<T>,
	private val cancellationState: CancellationState = CancellationState() // Additional cancellation state that we can propagate to copies so that a canceled task can't be rescheduled
) : CompletableFuture<T>(), Comparable<ScheduledTask<*>> {
	/**
	 * Executes this task.
	 * Note that this will not remove the task from the scheduler, it will still be fired when its time comes.
	 * @throws IllegalArgumentException If the task was canceled before.
	 */
	fun execute() {
		require(!isCancelled) { "Cannot execute a cancelled task." }

		executor.execute(this)
	}

	override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
		cancellationState.cancel()
		return super.cancel(mayInterruptIfRunning)
	}

	override fun isCancelled(): Boolean = super.isCancelled() || cancellationState.isCancelled

	fun isCyclic(): Boolean = periodTicks > 0u

	// Required for the priority queue
	override fun compareTo(other: ScheduledTask<*>): Int = executionTick.compareTo(other.executionTick)

	/**
	 * Creates a copy of this task with a new execution tick.
	 */
	fun reschedule(currentTick: ULong): ScheduledTask<T> {
		require(isCyclic()) { "Cannot reschedule a non-cyclic task." }
		return copy(currentTick + periodTicks.toULong(), periodTicks, executor, cancellationState)
	}
}