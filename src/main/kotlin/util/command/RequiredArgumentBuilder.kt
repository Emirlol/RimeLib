@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.ArgumentCommandNode
import net.minecraft.command.CommandSource

class RequiredArgumentBuilder<S, T>(private val name: String, private val type: ArgumentType<T>) : ArgumentBuilder<S>() where S : CommandSource {
	var suggestionsProvider: SuggestionProvider<S>? = null

	/**
	 * Convenience method that allows for a nicer syntax for adding a suggestion provider.
	 */
	inline fun suggests(provider: SuggestionProvider<S>) {
		this.suggestionsProvider = provider
	}

	override fun build(): ArgumentCommandNode<S, T> = ArgumentCommandNode(name, type, command, requirement, redirect, modifier, forks, suggestionsProvider).also { arguments.children.forEach(it::addChild) }
}