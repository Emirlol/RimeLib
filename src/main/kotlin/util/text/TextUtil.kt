@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util.text

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import me.ancientri.rimelib.util.client
import me.ancientri.rimelib.util.color.Color
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentContents
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.contents.PlainTextContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.entity.player.Player

// region Text creation utilities
/**
 * Creates a plain [MutableComponent] with the given [String] as its content.
 */
inline val String.text: MutableComponent get(): MutableComponent = Component.literal(this)

/**
 * Creates a [MutableComponent] with the given [String] as its content, applying the given [formatting] to the style.
 *
 * @param formatting The formatting to apply to the text.
 */
inline fun String.text(formatting: ChatFormatting): MutableComponent = text.withStyle(formatting)

/**
 * Creates a [MutableComponent] with the given [String] as its content, applying the given [color] to the style.
 *
 * @param color The color to apply to the text, represented as an integer ARGB value.
 */
inline fun String.text(color: Int): MutableComponent = text.withColor(color)

/**
 * Creates a [MutableComponent] with the given [String] as its content, applying the given [color] to the style.
 *
 * @param color The color to apply to the text, represented as a [Color] object.
 */
inline fun String.text(color: Color): MutableComponent = text.withColor(color.value)

/**
 * Creates a [MutableComponent] with the given [String] as its content, styled with the given [style].
 *
 * @param style The style to apply to the text, which can include color, formatting, and other style properties.
 */
inline fun String.text(style: Style): MutableComponent = text.setStyle(style)

/**
 * Creates a [MutableComponent] with the given [String] as its content, applying the given [builder] to the style.
 *
 * This allows you to use a [StyleBuilder] to create a more complex style for the text.
 *
 * @param builder A lambda that receives a [StyleBuilder] to configure the style of the text.
 */
inline fun String.text(builder: StyleBuilder.() -> Unit): MutableComponent = text.apply { style = StyleBuilder().apply(builder).build() }

/**
 * Creates a [net.minecraft.network.chat.contents.TranslatableContents] with the given this string as the key, and no arguments.
 *
 * @receiver The key of the translatable text, which is usually a translation key in the language files.
 * @return A [net.minecraft.network.chat.contents.TranslatableContents] with the given key and no arguments.
 */
inline val String.translatable: TranslatableContents get(): TranslatableContents = TranslatableContents(this, null, TranslatableContents.NO_ARGS)

/**
 * Creates a [TranslatableContents] with this string as the key and [args].
 *
 * The only allowed placeholder in the key is `%s`, which will either be replaced with a [Component] object or the result of the object's [toString()][Any.toString] method.
 *
 * @receiver The key of the translatable text, which is usually a translation key in the language files.
 * @return A [TranslatableContents] with the given key and arguments.
 */
inline fun String.translatable(vararg args: Any): TranslatableContents = TranslatableContents(this, null, args)

/**
 * Convenience function to create a [MutableComponent] with the given [content], [style], and [children].
 */
inline fun mutableTextOf(content: ComponentContents, style: Style = Style.EMPTY, children: List<Component> = ObjectArrayList()): MutableComponent = MutableComponent(content, children, style)

/**
 * Convenience function to create a [MutableComponent] with this text content.
 */
inline val ComponentContents.text: MutableComponent get() = mutableTextOf(this)

/**
 * Creates a [MutableComponent] with this text content, applying the given [color] to the style.
 *
 * @param color The color to apply to the text, represented as an integer ARGB value.
 */
inline fun ComponentContents.text(color: Int): MutableComponent = mutableTextOf(this, Style.EMPTY.withColor(color))

/**
 * Creates a [MutableComponent] with this text content, applying the given [color] to the style.
 *
 * @param color The color to apply to the text, represented as a [Color] object.
 */
inline fun ComponentContents.text(color: Color): MutableComponent = text(color.value)

/**
 * Creates a [MutableComponent] with this text content, applying the given [formatting] to the style.
 *
 * @param formatting The formatting to apply to the text.
 */
inline fun ComponentContents.text(formatting: ChatFormatting): MutableComponent = mutableTextOf(this, Style.EMPTY.withColor(formatting))

/**
 * Creates a [MutableComponent] with this text content and the given [style].
 *
 * @param style The style to apply to the text, which can include color, formatting, and other style properties.
 */
inline fun ComponentContents.text(style: Style): MutableComponent = mutableTextOf(this, style)

/**
 * Creates a [MutableComponent] with this text content, applying the given [builder] to the style.
 *
 * This allows you to use a [StyleBuilder] to create a more complex style for the text.
 *
 * @param builder A lambda that receives a [StyleBuilder] to configure the style of the text.
 */
inline fun ComponentContents.text(builder: StyleBuilder.() -> Unit): MutableComponent = text(StyleBuilder().apply(builder).build())

/**
 * Creates an empty-wrapped [MutableComponent] with the [builder].
 *
 * This will cause the root text to have no content, and the children won't inherit the style from the root text.
 */
inline fun text(builder: TextBuilder.() -> Unit): MutableComponent = text(PlainTextContents.EMPTY, Style.EMPTY, builder)

/**
 * Creates an empty-wrapped [MutableComponent] with the given [style] and [builder].
 *
 * There will be no content in the root text, but there will be a style applied to all children that are missing a style.
 */
inline fun text(style: Style, builder: TextBuilder.() -> Unit): MutableComponent = text(PlainTextContents.EMPTY, style, builder)

/**
 * Creates a [MutableComponent] with the given [content] and [style], applying the [builder] on it.
 */
inline fun text(content: ComponentContents, style: Style = Style.EMPTY, builder: TextBuilder.() -> Unit): MutableComponent = TextBuilder(content, style).apply(builder).build()

/**
 * Creates a [MutableComponent] with the given [string] and [style], applying the [builder] on it.
 *
 * This is a convenience method to create text with a specific style.
 */
inline fun text(string: String, style: Style = Style.EMPTY, builder: TextBuilder.() -> Unit): MutableComponent = TextBuilder(PlainTextContents.create(string), style).apply(builder).build()

/**
 * Creates a [MutableComponent] with the given [string] and [color], applying the [builder] on it.
 *
 * This is a convenience method to create text with a specific color.
 */
inline fun text(string: String, color: Int, builder: TextBuilder.() -> Unit): MutableComponent = text(PlainTextContents.create(string), Style.EMPTY.withColor(color), builder)

/**
 * Creates a [MutableComponent] with the given [string] and [color], applying the [builder] on it.
 *
 * This is a convenience method to create text with a specific color.
 */
inline fun text(string: String, color: Color, builder: TextBuilder.() -> Unit): MutableComponent = text(string, color.value, builder)

/**
 * Creates a [MutableComponent] with the given [string] and [formatting], applying the [builder] on it.
 *
 * This is a convenience method to create text with a specific formatting.
 */
inline fun text(string: String, formatting: ChatFormatting, builder: TextBuilder.() -> Unit): MutableComponent = text(PlainTextContents.create(string), Style.EMPTY.withColor(formatting), builder)

/**
 * Creates a [MutableComponent] from an existing [Component] instance, applying the [builder] on it.
 *
 * This is useful for editing an existing text without having to create a new one.
 */
inline fun text(initial: Component, builder: TextBuilder.() -> Unit): MutableComponent = TextBuilder(initial).apply(builder).build()

/**
 * Applies a new style to the given [MutableComponent] instance via the [builder], overriding any existing style.
 */
inline fun MutableComponent.withStyle(builder: StyleBuilder.() -> Unit): MutableComponent = apply { style = StyleBuilder().apply(builder).build() }

/**
 * Updates the existing style of the [MutableComponent] instance with a [builder].
 */
inline fun MutableComponent.styled(builder: StyleBuilder.() -> Unit): MutableComponent = apply { style = StyleBuilder(style).apply(builder).build() }
//endregion

// region Extension functions for trailing lambda syntax

/**
 * Convenience method that sends a [Component] message to the player, using a [TextBuilder] to create the text.
 *
 * This allows the use of the trailing lambda syntax with the [TextBuilder] in a reader-friendly way.
 */
inline fun Player.sendText(builder: TextBuilder.() -> Unit) = sendSystemMessage(text(builder))

/**
 * Convenience method that adds a [Component] message to the chat hud.
 *
 * This allows the use of the trailing lambda syntax with the [TextBuilder] in a reader-friendly way.
 */
inline fun ChatComponent.addMessage(builder: TextBuilder.() -> Unit) = addClientSystemMessage(text(builder))

/**
 * Convenience method for drawing text to the screen with a [builder]. Uses the client's [TextRenderer] instance.
 */
inline fun GuiGraphicsExtractor.drawText(x: Int, y: Int, color: Color = Color(-1), shadow: Boolean = true, builder: TextBuilder.() -> Unit) = drawText(client.font, x, y, color, shadow, builder)

/**
 * Convenience method for drawing text to the screen with a [builder] and a specific [TextRenderer].
 */
// Explicit overload for textRenderer to match positional arguments of the original drawText methods
inline fun GuiGraphicsExtractor.drawText(textRenderer: Font, x: Int, y: Int, color: Color = Color(-1), shadow: Boolean = true, builder: TextBuilder.() -> Unit) = text(textRenderer, text(builder), x, y, color.value, shadow)

/**
 * Convenience method for drawing centered text to the screen with a [builder]. Uses the client's [TextRenderer] instance.
 */
inline fun GuiGraphicsExtractor.drawCenteredText(x: Int, y: Int, color: Color = Color(-1), shadow: Boolean = true, builder: TextBuilder.() -> Unit) = drawCenteredText(client.font, x, y, color, shadow, builder)

/**
 * Convenience method for drawing centered text to the screen with a [builder] and a specific [TextRenderer].
 */
// Explicit overload for textRenderer to match positional arguments of the original drawCenteredText methods
inline fun GuiGraphicsExtractor.drawCenteredText(textRenderer: Font, x: Int, y: Int, color: Color = Color(-1), shadow: Boolean = true, builder: TextBuilder.() -> Unit) {
	val text = text(builder) // Have to build the text here, can't delegate to drawText above or we build twice
	this.text(textRenderer, text, x - textRenderer.width(text) / 2, y, color.value, shadow)
}

// endregion
