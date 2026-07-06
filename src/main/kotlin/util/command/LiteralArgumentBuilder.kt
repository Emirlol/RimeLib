package me.ancientri.rimelib.util.command

import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandSource

class LiteralArgumentBuilder<S>(private val literal: String) : ArgumentBuilder<S>() {
	override fun build(): LiteralCommandNode<S> = LiteralCommandNode(literal, command, requirement, redirect, modifier, forks).apply { root.children.forEach(::addChild) }
}