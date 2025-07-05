@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util.text

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import me.ancientri.rimelib.util.color.Color
import net.minecraft.client.gui.hud.ChatHud
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.*
import net.minecraft.util.Formatting

// region Text creation utilities
/**
 * Creates a plain [MutableText] with the given [String] as its content.
 */
inline val String.text get(): MutableText = Text.literal(this)

/**
 * Creates a [MutableText] with the given [String] as its content, applying the given [formatting] to the style.
 *
 * @param formatting The formatting to apply to the text.
 */
inline fun String.text(formatting: Formatting): MutableText = text.formatted(formatting)

/**
 * Creates a [MutableText] with the given [String] as its content, applying the given [color] to the style.
 *
 * @param color The color to apply to the text, represented as an integer ARGB value.
 */
inline fun String.text(color: Int): MutableText = text.withColor(color)

/**
 * Creates a [MutableText] with the given [String] as its content, applying the given [color] to the style.
 *
 * @param color The color to apply to the text, represented as a [Color] object.
 */
inline fun String.text(color: Color): MutableText = text.withColor(color.value)

/**
 * Creates a [MutableText] with the given [String] as its content, styled with the given [style].
 *
 * @param style The style to apply to the text, which can include color, formatting, and other style properties.
 */
inline fun String.text(style: Style): MutableText = text.setStyle(style)

/**
 * Creates a [MutableText] with the given [String] as its content, applying the given [builder] to the style.
 *
 * This allows you to use a [StyleBuilder] to create a more complex style for the text.
 *
 * @param builder A lambda that receives a [StyleBuilder] to configure the style of the text.
 */
inline fun String.text(builder: StyleBuilder.() -> Unit): MutableText = text.apply { style = StyleBuilder().apply(builder).build() }

/**
 * Creates a [TranslatableTextContent] with the given this string as the key, and no arguments.
 *
 * @receiver The key of the translatable text, which is usually a translation key in the language files.
 * @return A [TranslatableTextContent] with the given key and no arguments.
 */
inline val String.translatable get(): TranslatableTextContent = TranslatableTextContent(this, null, TranslatableTextContent.EMPTY_ARGUMENTS)

/**
 * Creates a [TranslatableTextContent] with this string as the key and [args].
 *
 * The only allowed placeholder in the key is `%s`, which will either be replaced with a [Text] object or the result of the object's [toString()][Any.toString] method.
 *
 * @receiver The key of the translatable text, which is usually a translation key in the language files.
 * @return A [TranslatableTextContent] with the given key and arguments.
 */
inline fun String.translatable(vararg args: Any) = TranslatableTextContent(this, null, args)

/**
 * Convenience function to create a [MutableText] with the given [content], [style], and [children].
 */
inline fun mutableTextOf(content: TextContent, style: Style = Style.EMPTY, children: List<Text> = ObjectArrayList()): MutableText = MutableText(content, children, style)

/**
 * Convenience function to create a [MutableText] with this text content.
 */
inline val TextContent.text: MutableText get() = mutableTextOf(this)

/**
 * Creates a [MutableText] with this text content, applying the given [color] to the style.
 *
 * @param color The color to apply to the text, represented as an integer ARGB value.
 */
inline fun TextContent.text(color: Int): MutableText = mutableTextOf(this, Style.EMPTY.withColor(color))

/**
 * Creates a [MutableText] with this text content, applying the given [color] to the style.
 *
 * @param color The color to apply to the text, represented as a [Color] object.
 */
inline fun TextContent.text(color: Color): MutableText = text(color.value)

/**
 * Creates a [MutableText] with this text content, applying the given [formatting] to the style.
 *
 * @param formatting The formatting to apply to the text.
 */
inline fun TextContent.text(formatting: Formatting): MutableText = mutableTextOf(this, Style.EMPTY.withFormatting(formatting))

/**
 * Creates a [MutableText] with this text content and the given [style].
 *
 * @param style The style to apply to the text, which can include color, formatting, and other style properties.
 */
inline fun TextContent.text(style: Style): MutableText = mutableTextOf(this, style)

/**
 * Creates a [MutableText] with this text content, applying the given [builder] to the style.
 *
 * This allows you to use a [StyleBuilder] to create a more complex style for the text.
 *
 * @param builder A lambda that receives a [StyleBuilder] to configure the style of the text.
 */
inline fun TextContent.text(builder: StyleBuilder.() -> Unit): MutableText = text(StyleBuilder().apply(builder).build())

/**
 * Creates an empty-wrapped [MutableText] with the [builder].
 *
 * This will cause the root text to have no content, and the children won't inherit the style from the root text.
 */
inline fun text(builder: TextBuilder.() -> Unit) = text(PlainTextContent.EMPTY, Style.EMPTY, builder)

/**
 * Creates an empty-wrapped [MutableText] with the given [style] and [builder].
 *
 * There will be no content in the root text, but there will be a style applied to all children that are missing a style.
 */
inline fun text(style: Style, builder: TextBuilder.() -> Unit) = text(PlainTextContent.EMPTY, style, builder)

/**
 * Creates a [MutableText] with the given [content] and [style], applying the [builder] on it.
 */
inline fun text(content: TextContent, style: Style = Style.EMPTY, builder: TextBuilder.() -> Unit): MutableText = TextBuilder(content, style).apply(builder).build()

/**
 * Creates a [MutableText] with the given [string] and [style], applying the [builder] on it.
 *
 * This is a convenience method to create text with a specific style.
 */
inline fun text(string: String, style: Style = Style.EMPTY, builder: TextBuilder.() -> Unit): MutableText = TextBuilder(PlainTextContent.of(string), style).apply(builder).build()

/**
 * Creates a [MutableText] with the given [string] and [color], applying the [builder] on it.
 *
 * This is a convenience method to create text with a specific color.
 */
inline fun text(string: String, color: Int, builder: TextBuilder.() -> Unit): MutableText = text(PlainTextContent.of(string), Style.EMPTY.withColor(color), builder)

/**
 * Creates a [MutableText] with the given [string] and [color], applying the [builder] on it.
 *
 * This is a convenience method to create text with a specific color.
 */
inline fun text(string: String, color: Color, builder: TextBuilder.() -> Unit): MutableText = text(string, color.value, builder)

/**
 * Creates a [MutableText] with the given [string] and [formatting], applying the [builder] on it.
 *
 * This is a convenience method to create text with a specific formatting.
 */
inline fun text(string: String, formatting: Formatting, builder: TextBuilder.() -> Unit): MutableText = text(PlainTextContent.of(string), Style.EMPTY.withFormatting(formatting), builder)

/**
 * Creates a [MutableText] from an existing [Text] instance, applying the [builder] on it.
 *
 * This is useful for editing an existing text without having to create a new one.
 */
inline fun text(initial: Text, builder: TextBuilder.() -> Unit) = TextBuilder(initial).apply(builder).build()
//endregion

// region Extension functions for trailing lambda syntax

/**
 * Convenience method that sends a [Text] message to the player.
 *
 * This exists to avoid specifying the `false` parameter for `sendMessage` each time, as the overload with the default of `false` was removed in Minecraft 1.20.4 (I think? Not sure when it was removed).
 */
inline fun PlayerEntity.sendText(text: Text) = sendMessage(text, false)

/**
 * Convenience method that sends a [Text] message to the player, using a [TextBuilder] to create the text.
 *
 * This allows the use of the trailing lambda syntax with the [TextBuilder] in a reader-friendly way.
 */
inline fun PlayerEntity.sendText(builder: TextBuilder.() -> Unit) = sendText(text(builder))

/**
 * Convenience method that adds a [Text] message to the chat hud.
 *
 * This allows the use of the trailing lambda syntax with the [TextBuilder] in a reader-friendly way.
 */
inline fun ChatHud.addMessage(builder: TextBuilder.() -> Unit) = addMessage(text(builder))

// endregion
