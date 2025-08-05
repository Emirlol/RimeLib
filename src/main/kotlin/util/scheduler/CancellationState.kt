package me.ancientri.rimelib.util.scheduler

import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic

class CancellationState() {
	private val cancelled: AtomicBoolean = atomic(false)

	val isCancelled: Boolean
		get() = cancelled.value

	fun cancel() {
		cancelled.value = true
	}
}