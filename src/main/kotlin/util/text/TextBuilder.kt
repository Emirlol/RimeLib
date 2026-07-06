@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util.text

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import me.ancientri.rimelib.util.color.Color
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentContents
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.contents.TranslatableContents

/**
 * A low-overhead builder for creating [Component] instances with a fluent API.
 * The only extra overhead is the creation of a [TextBuilder] instance, everything else is inlined where possible.
 *
 * This builder allows you to create complex text structures with styles in a more concise way.
 *
 * There are 2 main ways of using this builder:
 * ### 1 - Using the `+` operator to append child texts:
 * ```kotlin
 * text {
 *     +"Hello".text(ColorPalette.TEXT) + " " + "world".text(ColorPalette.SKY) + "!" + "this.is.a.translation.key".translatable.text(ColorPalette.TEXT) + "this.is.a.translation.key.%s".translatable("with args").text(ColorPalette.SKY)
 * }
 * ```
 * This style uses the `+` operator to append text to the builder.
 *
 * #### Pros:
 * - You can chain calls, allowing for very concise text building.
 *
 * #### Cons:
 * - The chain has to start with an unaryPlus `+` operator that returns a [TextBuilder] to begin the chain, after which each plus `+` operator returns a [TextBuilder] for chaining.
 * - You have to use parentheses for string concatenation, otherwise it will be interpreted as a separate text appending operation.
 * - Chains might be harder to read due to the horizontal layout of the code, especially with the additional operators in between.
 *
 * ### 2 - Using infix functions to add text with specific styles:
 * ```kotlin
 * text {
 *     "Hello" colored ColorPalette.TEXT
 *     +" "
 *     "world" colored ColorPalette.SKY
 *     +"!"
 *     "this.is.a.translation.key".translatable colored ColorPalette.TEXT
 *     "this.is.a.translation.key.%s".translatable("with args") colored ColorPalette.SKY
 * }
 * ```
 * This style uses infix functions that create text with specific styles and append them to the builder in the same call.
 *
 * #### Pros:
 * - More readable due to how infix functions look like natural language.
 *
 * #### Cons:
 * - You can't chain calls after an infix function, forcing you to a more vertical layout of the code.
 * - You still have to use a method call to append plain [String]s, be it the [`+`][unaryPlus] operator or [append]`. There's no infix function for plain [String]s, because infix functions are meant to be used with an argument.
 *
 * You can use both styles in the same builder as they are not mutually exclusive,
 * but note the difference in method calls to create text with styles:
 *
 * The infix methods [colored], [formatted] and [styled] delegate to the `text` methods for text creation **and then add the created text to the builder**;
 * whereas the unaryPlus operator `+` expects a created [Component] instance, and does not create the text itself.
 */
@TextDsl
class TextBuilder(
	private val content: ComponentContents,
	private val style: Style,
	private val children: MutableList<Component> = ObjectArrayList()
) {
	/**
	 * Creates a [TextBuilder] from an existing [Component] instance.
	 */
	constructor(text: Component) : this(text.contents, text.style, text.siblings)

	/**
	 * Appends a child [Component] to [children].
	 */
	fun append(text: Component) {
		children += text
	}

	inline operator fun String.unaryPlus(): TextBuilder {
		append(this.text)
		return this@TextBuilder
	}

	inline operator fun Component.unaryPlus(): TextBuilder {
		append(this)
		return this@TextBuilder
	}

	inline operator fun ComponentContents.unaryPlus(): TextBuilder {
		append(MutableComponent.create(this))
		return this@TextBuilder
	}

	inline operator fun TextBuilder.plus(other: String): TextBuilder {
		append(other.text)
		return this
	}

	inline operator fun TextBuilder.plus(other: Component): TextBuilder {
		append(other)
		return this
	}

	inline operator fun TextBuilder.plus(other: ComponentContents): TextBuilder {
		append(MutableComponent.create(other))
		return this
	}

	/**
	 * Adds a [Component] created from this [String] with the [color] applied to this [TextBuilder]'s [children][TextBuilder.children].
	 *
	 * @param color The color to apply to the text.
	 */
	inline infix fun String.colored(color: Color) = colored(color.value)

	/**
	 * Adds a [Component] created from this [String] with the [color] applied to this [TextBuilder]'s [children][TextBuilder.children].
	 *
	 * @param color The color to apply to the text.
	 */
	inline infix fun String.colored(color: Int) = append(this.text(color))

	/**
	 * Adds a [Component] created from this [String] with the [formatting] applied to this [TextBuilder]'s [children][TextBuilder.children].
	 *
	 * @param formatting The formatting to apply to the text.
	 */
	inline infix fun String.formatted(formatting: ChatFormatting) = append(this.text(formatting))

	/**
	 * Adds a [Component] created from this [String] with the [style] applied to this [TextBuilder]'s [children][TextBuilder.children].
	 *
	 * @param style The style to apply to the text.
	 */
	inline infix fun String.styled(style: Style) = append(this.text(style))

	/**
	 * Adds a [Component] created from this [String] with the [builder][StyleBuilder] applied to this [TextBuilder]'s [children][TextBuilder.children].
	 *
	 * @param builder The style builder to apply to the text.
	 */
	inline infix fun String.styled(builder: StyleBuilder.() -> Unit) = styled(StyleBuilder().apply(builder).build())

	/**
	 * Adds a [Component] created from this [TranslatableContents] with the [color] applied to this [TextBuilder]'s [children][TextBuilder.children].
	 *
	 * @param color The color to apply to the text.
	 */
	inline infix fun TranslatableContents.colored(color: Color) = colored(color.value)

	/**
	 * Adds a [Component] created from this [TranslatableContents] with the [color] applied to this [TextBuilder]'s [children][TextBuilder.children].
	 *
	 * @param color The color to apply to the text.
	 */
	inline infix fun TranslatableContents.colored(color: Int) = append(MutableComponent(this, ObjectArrayList(), Style.EMPTY.withColor(color)))

	/**
	 * Adds a [Component] created from this [TranslatableContents] with the [formatting] applied to this [TextBuilder]'s [children][TextBuilder.children].
	 *
	 * @param formatting The formatting to apply to the text.
	 */
	inline infix fun TranslatableContents.formatted(formatting: ChatFormatting) = append(MutableComponent(this, ObjectArrayList(), Style.EMPTY.withColor(formatting)))

	/**
	 * Adds a [Component] created from this [TranslatableContents] with the [style] applied to this [TextBuilder]'s [children][TextBuilder.children].
	 *
	 * @param style The style to apply to the text.
	 */
	inline infix fun TranslatableContents.styled(style: Style) = append(MutableComponent(this, ObjectArrayList(), style))

	/**
	 * Adds a [Component] created from this [TranslatableContents] with the [builder][StyleBuilder] applied to this [TextBuilder]'s [children][TextBuilder.children].
	 *
	 * @param builder The style builder to apply to the text.
	 */
	inline infix fun TranslatableContents.styled(builder: StyleBuilder.() -> Unit) = styled(StyleBuilder().apply(builder).build())

	fun build(): MutableComponent = MutableComponent(content, children, style)
}