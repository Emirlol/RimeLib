package me.rime.rimelib.util

import net.minecraft.util.profiler.Profilers

/**
 * Inserts a profile section into the profiler to measure the block of code.
 *
 * @param name The name of the profile section.
 * @param block The block of code to profile.
 * @param T The return type of the block. Can be anything, even [Unit] or `null`.
 */
inline fun <T : Any?> profiled(name: String, block: () -> T): T = with(Profilers.get()) {
	push(name)
	val t = block()
	pop()
	t
}
