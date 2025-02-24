package me.rime.rimelib.util

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.Event
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

/**
 * Meant to be used on leafs of a command tree that do not have actual executors.
 *
 * This helps tell the user that the command they entered is incomplete.
 */
@SuppressWarnings("kotlin:S6516") // This is a false positive, it's meant for non-named objects.
object IncompleteCommand : Command<CommandSource> {
	override fun run(context: CommandContext<CommandSource>): Int {
		when (context.source) {
			is FabricClientCommandSource -> player?.sendText("Incomplete command: ${context.input}".text(ColorPalette.RED))
			is ServerCommandSource -> (context.source as ServerCommandSource).sendMessage("Incomplete command: ${context.input}".text(ColorPalette.RED))
		}
		return Command.SINGLE_SUCCESS
	}
}

/**
 * Convenience method for getting an argument from a command context.
 *
 * Example:
 * ```
 * context.getArgument<String>("argumentName")
 * // or
 * val string: String = context.getArgument("argumentName")
 * ```
 */
inline fun <reified T> CommandContext<out CommandSource>.getArgument(name: String): T {
	return getArgument(name, T::class.java)
}

/**
 * Convenience method for registering a command.
 *
 * Example usage:
 * ```kotlin
 * ClientCommandRegistrationCallback.EVENT.register("namespace") {
 *     literal("example") {
 *         literal("subcommand") {
 *             executes {
 *                 player?.sendText("This is a subcommand!".text(ColorPalette.TEXT))
 *             }
 *         }
 *         argument("subcommand2", StringArgumentType.word()) {
 *             executes {
 *                 player?.sendText {
 *                     +"Hello ".text(ColorPalette.TEXT)
 *                     +getArgument<String>("subcommand2").text(ColorPalette.MAUVE)
 *                     +"!".text(ColorPalette.TEXT)
 *                 }
 *             }
 *         }
 *         executes(IncompleteCommand) // Missing subcommand.
 *     }
 * }
 * ```
 *
 * Which will result in the following commands:
 * - /namespace example subcommand
 * - /namespace example <subcommand2>
 *
 * @param builder The command tree built using the [ClientCommandBuilder].
 * @see IncompleteCommand
 */
@Environment(EnvType.CLIENT)
inline fun Event<ClientCommandRegistrationCallback>.register(namespace: String, crossinline builder: ClientCommandBuilder.() -> Unit) {
	register { dispatcher, _ ->
		dispatcher.register(literalClient(namespace, builder)) // Command trees start out of our namespaced literal command, so this cast is safe.
	}
}

/**
 * Convenience method for registering a command.
 *
 * Example usage:
 * ```kotlin
 * CommandRegistrationCallback.EVENT.register("namespace") {
 *     literal("example") {
 *         literal("subcommand") {
 *             executes {
 *                 player?.sendText("This is a subcommand!".text(ColorPalette.TEXT))
 *             }
 *         }
 *         argument("subcommand2", StringArgumentType.word()) {
 *             executes {
 *                 player?.sendText {
 *                     +"Hello ".text(ColorPalette.TEXT)
 *                     +getArgument<String>("subcommand2").text(ColorPalette.MAUVE)
 *                     +"!".text(ColorPalette.TEXT)
 *                 }
 *             }
 *         }
 *         executes(IncompleteCommand) // Missing subcommand.
 *     }
 * }
 * ```
 *
 * Which will result in the following commands:
 * - /namespace example subcommand
 * - /namespace example <subcommand2>
 *
 * @param builder The command tree built using the [ServerCommandBuilder].
 * @see IncompleteCommand
 */
@Environment(EnvType.SERVER)
inline fun Event<CommandRegistrationCallback>.register(namespace: String, environment: RegistrationEnvironment = RegistrationEnvironment.ALL, crossinline builder: ServerCommandBuilder.() -> Unit) {
	register { dispatcher, _, env ->
		if (environment == RegistrationEnvironment.ALL || environment == env)
			dispatcher.register(literalServer(namespace, builder)) // Command trees start out of our namespaced literal command, so this cast is safe.
	}
}

/**
 * Convenience method for using the [ClientCommandBuilder]. This is mostly meant for unit testing.
 * @see ClientCommandBuilder
 */
@Environment(EnvType.CLIENT)
inline fun literalClient(name: String, crossinline builder: ClientCommandBuilder.() -> Unit) = ClientCommandBuilder(ClientCommandManager.literal(name)).apply(builder).node as LiteralArgumentBuilder<FabricClientCommandSource>

/**
 * Convenience method for using the [ServerCommandBuilder]. This is mostly meant for unit testing.
 * @see ServerCommandBuilder
 */
@Environment(EnvType.SERVER)
inline fun literalServer(name: String, crossinline builder: ServerCommandBuilder.() -> Unit) = ServerCommandBuilder(literal(name)).apply(builder).node as LiteralArgumentBuilder<ServerCommandSource>

/**
 * Wrapper around [ClientCommandManager] for more kotlin-like command registration.
 *
 * It's not recommended to use this class directly, but rather use the [register] function.
 *
 * @param node The current node in the command tree. For [register] this is the root node with the namespace as the command.
 */
@Suppress("NOTHING_TO_INLINE")
@Environment(EnvType.CLIENT)
class ClientCommandBuilder(val node: ArgumentBuilder<FabricClientCommandSource, *>) {
	inline fun literal(name: String, builder: ClientCommandBuilder.() -> Unit) {
		node.then(ClientCommandManager.literal(name).apply { ClientCommandBuilder(this).apply(builder) })
	}

	inline fun argument(name: String, argumentType: ArgumentType<*>, builder: ClientCommandBuilder.() -> Unit) {
		node.then(ClientCommandManager.argument(name, argumentType).apply { ClientCommandBuilder(this).apply(builder) })
	}

	/**
	 * How the command will be executed.
	 * @param executor The function that will be executed when the command is run.
	 *                 This command will always return 1, signifying the command was successful.
	 */
	inline fun executes(crossinline executor: CommandContext<FabricClientCommandSource>.() -> Unit) {
		node.executes { executor(it); Command.SINGLE_SUCCESS }
	}

	/**
	 * Pass-through function for [executes] that allows for a more concise syntax.
	 */
	inline fun executes(executor: Command<FabricClientCommandSource>) {
		node.executes(executor)
	}

	inline val incomplete: Unit
		get() {
			node.executes(IncompleteCommand as Command<FabricClientCommandSource>)
			Unit
		}
}

/**
 * Wrapper around [CommandManager] for more kotlin-like command registration.
 *
 * It's not recommended to use this class directly, but rather use the [literal] function. Or better yet, use the [register] function.
 *
 * @param node The current node in the command tree. For [register] this is the root node with the namespace as the command.
 */
@Suppress("NOTHING_TO_INLINE")
// @Environment(EnvType.SERVER) // Breaks tests, since the tests are not in a server environment.
class ServerCommandBuilder(val node: ArgumentBuilder<ServerCommandSource, *>) {
	inline fun literal(name: String, builder: ServerCommandBuilder.() -> Unit) {
		node.then(CommandManager.literal(name).apply { ServerCommandBuilder(this).apply(builder) })
	}

	inline fun argument(name: String, argumentType: ArgumentType<*>, builder: ServerCommandBuilder.() -> Unit) {
		node.then(CommandManager.argument(name, argumentType).apply { ServerCommandBuilder(this).apply(builder) })
	}

	/**
	 * How the command will be executed.
	 * @param executor The function that will be executed when the command is run.
	 *                 This command will always return 1, signifying the command was successful.
	 */
	inline fun executes(crossinline executor: CommandContext<ServerCommandSource>.() -> Unit) {
		node.executes { executor(it); Command.SINGLE_SUCCESS }
	}

	/**
	 * Pass-through function for [executes] that allows for a more concise syntax.
	 */
	inline fun executes(executor: Command<ServerCommandSource>) {
		node.executes(executor)
	}

	inline val incomplete: Unit
		get() {
			node.executes(IncompleteCommand as Command<ServerCommandSource>)
			Unit
		}
}