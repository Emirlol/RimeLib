@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util.command

import com.google.common.base.Predicates
import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.SingleRedirectModifier
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.brigadier.tree.RootCommandNode
import net.minecraft.command.CommandSource
import java.util.*
import java.util.function.Predicate

inline fun <S : CommandSource> command(literal: String, block: LiteralArgumentBuilder<S>.() -> Unit): LiteralCommandNode<S> = LiteralArgumentBuilder<S>(literal).apply(block).build()

abstract class ArgumentBuilder<S> where S : CommandSource {
	val root: RootCommandNode<S> = RootCommandNode<S>()
	var command: Command<S>? = null
	var requirement: Predicate<S> = Predicates.alwaysTrue<S>()
	var redirect: CommandNode<S>? = null
		private set
	var modifier: RedirectModifier<S>? = null
		private set
	var forks: Boolean = false
		private set

	/**
	 * Convenience method that allows for a nicer syntax for adding a requirement.
	 */
	inline fun requires(requirement: Predicate<S>) {
		this.requirement = requirement
	}

	/**
	 * Convenience method to set [this.command][ArgumentBuilder.command].
	 *
	 * @param command The command to execute.
	 */
	inline fun executes(command: Command<S>) {
		this.command = command
	}

	/**
	 * Convenience function to set [this.command][ArgumentBuilder.command] to a lambda that doesn't return a value.
	 *
	 * @param command The command to execute.
	 */
	inline fun executes(crossinline command: CommandContext<S>.() -> Unit) = executes(Command<S> {
		it.command()
		Command.SINGLE_SUCCESS
	})

	fun redirect(target: CommandNode<S>, modifier: SingleRedirectModifier<S>? = null) {
		forward(target, false, if (modifier == null) null else RedirectModifier { Collections.singleton(modifier.apply(it)) })
	}

	fun fork(target: CommandNode<S>, modifier: RedirectModifier<S>?) {
		forward(target, true, modifier)
	}

	// This is different from the original because having a functional interface as the last parameter allows for a trailing lambda syntax in kotlin, and it's more idiomatic.
	fun forward(target: CommandNode<S>, forks: Boolean, modifier: RedirectModifier<S>?) {
		require(root.children.isEmpty()) { "Cannot forward a node with children" }
		this.redirect = target
		this.forks = forks
		this.modifier = modifier
	}

	inline fun literal(literal: String) {
		LiteralArgumentBuilder<S>(literal).also { root.addChild(it.build()) }
	}

	inline fun literal(literal: String, block: LiteralArgumentBuilder<S>.() -> Unit) {
		LiteralArgumentBuilder<S>(literal).apply(block).also { root.addChild(it.build()) }
	}

	inline fun <T> argument(name: String, type: ArgumentType<T>) {
		RequiredArgumentBuilder<S, T>(name, type).also { root.addChild(it.build()) }
	}

	inline fun <T> argument(name: String, type: ArgumentType<T>, block: RequiredArgumentBuilder<S, T>.() -> Unit) {
		RequiredArgumentBuilder<S, T>(name, type).apply(block).also { root.addChild(it.build()) }
	}

	abstract fun build(): CommandNode<S>
}