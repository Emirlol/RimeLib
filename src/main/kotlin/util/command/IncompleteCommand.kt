package me.ancientri.rimelib.util.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandSource

object IncompleteCommand : Command<CommandSource> {
	override fun run(context: CommandContext<CommandSource>): Int {
		throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create()
	}

	fun <T : CommandSource> getInstance(): Command<T> {
		@Suppress("UNCHECKED_CAST")
		return this as Command<T>
	}
}
