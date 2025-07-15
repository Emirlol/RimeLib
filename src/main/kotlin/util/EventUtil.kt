package me.ancientri.rimelib.util

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.Identifier
import java.util.function.Function

/**
 * Utility extensions for `EventFactory` methods that provide reified type parameters to skip the need for explicit class tokens.
 */
object EventUtil {
	// Documentation is propagated from the EventFactory methods

	/**
	 * Create an "array-backed" Event instance.
	 *
	 * If your factory simply delegates to the listeners without adding custom behavior,
	 * consider using the other overload if performance of this event is critical.
	 *
	 * @param invokerFactory The invoker factory, combining multiple listeners into one instance.
	 * @param T              The listener type.
	 * @return The Event instance.
	 */
	inline fun <reified T> createArrayBacked(invokerFactory: Function<Array<T>, T>): Event<T> = EventFactory.createArrayBacked(T::class.java, invokerFactory)

	/**
	 * Create an "array-backed" Event instance with a custom empty invoker,
	 * for an event whose [invokerFactory] only delegates to the listeners.
	 *
	 * - If there is no listener, the custom empty invoker will be used.
	 * - **If there is only one listener, that one will be used as the invoker, and the factory will not be called.**
	 * - Only when there are at least two listeners will the factory be used.
	 *
	 *
	 * Having a custom empty invoker (of type (...) → {}) increases performance
	 * relative to iterating over an empty array; however, it only really matters
	 * if the event is executed thousands of times a second.
	 *
	 * @param emptyInvoker   The custom empty invoker.
	 * @param invokerFactory The invoker factory, combining multiple listeners into one instance.
	 * @param T              The listener type.
	 * @return The Event instance.
	 */
	inline fun <reified T> createArrayBacked(emptyInvoker: T, invokerFactory: Function<Array<T>, T>): Event<T> = EventFactory.createArrayBacked(T::class.java, emptyInvoker, invokerFactory)

	/**
	 * Create an array-backed event with a list of default phases that get invoked in order.
	 * Exposing the identifiers of the default phases as `public static final` constants is encouraged.
	 *
	 * An event phase is a named group of listeners, which may be ordered before or after other groups of listeners.
	 * This allows some listeners to take priority over other listeners.
	 * Adding separate events should be considered before making use of multiple event phases.
	 *
	 * Phases may be freely added to events created with any of the factory functions,
	 * however, using this function is preferred for widely used event phases.
	 * If more phases are necessary, discussion with the author of the Event is encouraged.
	 *
	 * Refer to [Event.addPhaseOrdering] for an explanation of event phases.
	 *
	 * @param invokerFactory The invoker factory, combining multiple listeners into one instance.
	 * @param defaultPhases  The default phases of this event, in the correct order. Must contain [Event.DEFAULT_PHASE].
	 * @param T              The listener type.
	 * @return The Event instance.
	 */
	inline fun <reified T> createWithPhases(invokerFactory: Function<Array<T>, T>, vararg defaultPhases: Identifier): Event<T> = EventFactory.createWithPhases(T::class.java, invokerFactory, *defaultPhases)
}