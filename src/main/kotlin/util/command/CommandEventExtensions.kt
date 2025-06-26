package me.ancientri.rimelib.util.command

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.Event
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

/**
 * Convenience method for registering a command.
 *
 * Example usage:
 * ```kotlin
 * ClientCommandRegistrationCallback.EVENT.register("namespace") {
 *     literal("example") {
 *         literal("subcommand") {
 *             executes { // Explicitly defined `executes`
 *                 player?.sendText("This is a subcommand!".text(ColorPalette.TEXT))
 *             }
 *         }
 *         literal("subcommand2") executes { // Infix notation for `executes` for shorter syntax leaf nodes.
 *             player?.sendText("Hello".text)
 *         }
 *         argument("subcommand2", StringArgumentType.word()) executes {
 *             player?.sendText {
 *                 +"Hello ".text(ColorPalette.TEXT)
 *                 +it.getArgument<String>("subcommand2").text(ColorPalette.MAUVE)
 *                 +"!".text(ColorPalette.TEXT)
 *             }
 *             Command.SINGLE_SUCCESS // Explicit success return value. This is optional via an `executes` overload.
 *         }
 *     }
 * }
 * ```
 *
 * Which will result in the following commands:
 * - /namespace example subcommand
 * - /namespace example subcommand2
 * - /namespace example &lt;subcommand2&gt;
 *
 * @param namespace The namespace for the command, which will be used as the root command.
 * @param builder The command tree built using the [LiteralArgumentBuilder].
 */
@Environment(EnvType.CLIENT)
inline fun Event<ClientCommandRegistrationCallback>.register(namespace: String, crossinline builder: LiteralArgumentBuilder<FabricClientCommandSource>.() -> Unit) {
	register { dispatcher, _ ->
		dispatcher.root.addChild(command(namespace, builder))
	}
}

//TODO Make this doc code consistent with the server stuff, i.e. there is no `player` and it should be `source` instead.
/**
 * Convenience method for registering a command.
 *
 * Example usage:
 * ```kotlin
 * CommandRegistrationCallback.EVENT.register("namespace") {
 *     literal("example") {
 *         literal("subcommand") {
 *             executes { // Explicitly defined `executes`
 *                 player?.sendText("This is a subcommand!".text(ColorPalette.TEXT))
 *             }
 *         }
 *         literal("subcommand2") executes { // Infix notation for `executes` for shorter syntax leaf nodes.
 *             player?.sendText("Hello".text)
 *         }
 *         argument("subcommand3", StringArgumentType.word()) executes {
 *             player?.sendText {
 *                 +"Hello ".text(ColorPalette.TEXT)
 *                 +it.getArgument<String>("subcommand2").text(ColorPalette.MAUVE)
 *                 +"!".text(ColorPalette.TEXT)
 *             }
 *             Command.SINGLE_SUCCESS // Explicit success return value. This is optional via an `executes` overload.
 *         }
 *     }
 * }
 * ```
 *
 * Which will result in the following commands:
 * - /namespace example subcommand
 * - /namespace example subcommand2
 * - /namespace example &lt;subcommand2&gt;
 *
 * @param namespace The namespace for the command, which will be used as the root command.
 * @param environment The environment in which the command should be registered. Defaults to [RegistrationEnvironment.ALL].
 * @param builder The command tree built using the [LiteralArgumentBuilder].
 */
@Environment(EnvType.SERVER)
inline fun Event<CommandRegistrationCallback>.register(namespace: String, environment: RegistrationEnvironment = RegistrationEnvironment.ALL, crossinline builder: LiteralArgumentBuilder<ServerCommandSource>.() -> Unit) {
	register { dispatcher, _, env ->
		if (environment == RegistrationEnvironment.ALL || environment == env)
			dispatcher.root.addChild(command(namespace, builder))
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
 * Delegate provider for command arguments, allowing the syntax:
 * ```kotlin
 * val argumentName: String by arguments()
 * ```
 * Which is equivalent to:
 * ```kotlin
 * val argumentName: String = context.getArgument("argumentName", String::class.java)
 * ```
 */
fun <T : Any> CommandContext<*>.arguments() = PropertyDelegateProvider<Nothing?, ReadOnlyProperty<Nothing?, T>> { _, property ->
	@Suppress("UNCHECKED_CAST")
	val kClass = property.returnType.classifier as? KClass<T> ?: throw IllegalArgumentException("Property '${property.name}' must have a KClass type")
	val argument = this.getArgument(property.name, kClass.java)
	ReadOnlyProperty { _, _ -> argument }
}
