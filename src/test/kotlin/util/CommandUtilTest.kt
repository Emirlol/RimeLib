package util

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import me.rime.rimelib.RimeLib
import me.rime.rimelib.util.literalClient
import me.rime.rimelib.util.literalServer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CommandUtilTest {
	@Test
	fun clientTest() {
		val a = ClientCommandManager.literal(RimeLib.NAMESPACE)
			.then(
				ClientCommandManager.literal("example")
					.then(ClientCommandManager.literal("subcommand"))
					.then(ClientCommandManager.argument("subcommand2", StringArgumentType.word()))
			).build()
		val b = literalClient(RimeLib.NAMESPACE) {
			literal("example") {
				literal("subcommand") {}
				argument("subcommand2", StringArgumentType.word()) {}
			}
		}.build()
		// compare map elements
		assertTrue(compareCommands(a, b))
	}

	@Test
	fun serverTest() {
		val a = CommandManager.literal(RimeLib.NAMESPACE)
			.then(
				CommandManager.literal("example")
					.then(CommandManager.literal("subcommand"))
					.then(CommandManager.argument("subcommand2", StringArgumentType.word()))
			).build()
		val b = literalServer(RimeLib.NAMESPACE) {
			literal("example") {
				literal("subcommand") {}
				argument("subcommand2", StringArgumentType.word()) {}
			}
		}.build()
		// compare map elements
		assertTrue(compareCommands(a, b))
	}

	fun compareCommands(a: CommandNode<out CommandSource>, b: CommandNode<out CommandSource>): Boolean {
		if (a.children.size != b.children.size) {
			eprintln(
				"""Size mismatch:
				|  a: ${a.children.size}
				|  b: ${b.children.size}
			""".trimIndent()
			)
			return false
		}
		if (a.name != b.name) {
			eprintln(
				"""Name mismatch:
				|  a: ${a.name}
				|  b: ${b.name}
			""".trimIndent()
			)
			return false
		}
		if (a is ArgumentCommandNode<out CommandSource, *> && b is ArgumentCommandNode<out CommandSource, *>
			&& a.type != b.type && a.type.javaClass != b.type.javaClass
		) {
			eprintln(
				"""Argument type mismatch:
				|  a: ${a.type.javaClass}
				|  b: ${b.type.javaClass}
				""".trimIndent()
			)
			return false
		}
		if (a.children.isEmpty()) return true

		val aIterator = a.children.iterator()
		val bIterator = b.children.iterator()
		repeat(a.children.size) {
			val a = aIterator.next()
			val b = bIterator.next()
			if (!compareCommands(a, b)) return false // The failure will be printed in the recursive call
		}

		return true
	}

	fun eprintln(message: String) {
		System.err.println(message)
	}
}