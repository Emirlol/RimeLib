package me.ancientri.rimelib.util.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

object IncompleteCommand : Command<CommandSource> {
	override fun run(context: CommandContext<CommandSource>): Int {
		throw UnsupportedOperationException("This command is incomplete.")
	}

	fun <T: CommandSource> getInstance(): Command<T> {
		@Suppress("UNCHECKED_CAST")
		return this as Command<T>
	}
}
