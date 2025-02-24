@file:Suppress("NOTHING_TO_INLINE")

package me.rime.rimelib.util

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.EntityType
import net.minecraft.item.ItemStack
import net.minecraft.text.*
import net.minecraft.text.HoverEvent.ItemStackContent
import net.minecraft.util.Formatting
import org.intellij.lang.annotations.Language
import java.awt.Color
import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

inline val String.text get(): MutableText = Text.literal(this)
inline fun String.text(formatting: Formatting): MutableText = text.formatted(formatting)
inline fun String.text(color: Int): MutableText = text.withColor(color)
inline fun String.text(color: Color): MutableText = text.withColor(color.rgb)
inline fun String.text(style: Style): MutableText = text.setStyle(style)
inline fun String.styled(builder: StyleBuilder.() -> Unit): MutableText = text.apply { style = StyleBuilder().apply(builder).build() }

inline val MutableText.emptyWrapper get(): MutableText = Text.empty().append(this)

inline val String.translatable get(): MutableText = Text.translatable(this)
inline fun String.translatable(vararg args: Any): MutableText = Text.translatable(this, args)

/**
 * Convenience method for creating a [TextBuilder], applying the builder on it and calling [build][TextBuilder.build].
 */
inline fun text(builder: TextBuilder.() -> Unit) = TextBuilder().apply(builder).build()

/**
 * Convenience method for creating a [TextBuilder] with an initial [Text] instance to build on top of, applying the builder on it and calling [build][TextBuilder.build].
 */
inline fun text(initial: Text, builder: TextBuilder.() -> Unit) = TextBuilder(initial).apply(builder).build()

@Environment(EnvType.CLIENT)
inline fun ClientPlayerEntity.sendText(text: Text) = sendMessage(text, false)

@Environment(EnvType.CLIENT)
inline fun ClientPlayerEntity.sendText(builder: TextBuilder.() -> Unit) = sendText(text(builder))

@Environment(EnvType.CLIENT)
inline fun ClientPlayerEntity.sendMiniMessage(@Language("XML") string: String) = sendMessage(MiniMessage.miniMessage().deserialize(string))

/**
 * A low-overhead builder for creating [MutableText] instances.
 *
 * This class always wraps the children text around [PlainTextContent.EMPTY] with the parent style set to [Style.EMPTY], so there will be no style inheritance from the main text.
 *
 * Example:
 * ```kotlin
 * text {
 *    +("Hello" + " ") + "World".text(Formatting.GOLD) + "!"
 * }
 * ```
 * is equal to
 * ```kotlin
 * Text.empty()
 *     .append(Text.literal("Hello" + " ")) // You wouldn't do this in practice, but it's just to show that the string concatenation has to be wrapped in parentheses.
 *     .append(Text.literal("World").formatted(Formatting.GOLD))
 *     .append(Text.literal("!"))
 * ```
 * The unaryPlus (+) and plus (+) operators for [TextBuilder] always return self, so they can be chained.
 * But this also means any string concatenation has to be wrapped in parentheses.
 */
class TextBuilder(private val children: MutableList<Text> = ObjectArrayList<Text>()) {
	/**
	 * Convenience constructor for editing an existing [Text] instance.
	 * See the next constructor for more information.
	 *
	 * Stupid KDoc doesn't allow referencing specific constructors, so there's no quick link here.
	 */
	constructor(text: Text) : this(text.content, text.siblings, text.style)

	/**
	 * Alternative constructor for editing an existing [Text] instance.
	 *
	 * Note: If the text content isn't [PlainTextContent.EMPTY], it will be prepended to the children list with the style of the text.
	 * If the style isn't [Style.EMPTY], any style inheritance that was applying to the children will be lost due to being converted to a child text.
	 *
	 * @param content The content of the text. Has to be [PlainTextContent].
	 * @param children The children of the text.
	 * @param style The style of the text.
	 * @throws IllegalArgumentException If the content is not [PlainTextContent].
	 */
	constructor(content: TextContent, children: List<Text>, style: Style = Style.EMPTY) : this(ObjectArrayList<Text>().apply {
		require(content is PlainTextContent) { "Text content must be `PlainTextContent`!" }
		if (content.string().isNotEmpty()) this += MutableText(content, children, style)
	})

	operator fun String.unaryPlus(): TextBuilder {
		children += Text.literal(this)
		return this@TextBuilder
	}

	operator fun Text.unaryPlus(): TextBuilder {
		children += this
		return this@TextBuilder
	}

	operator fun TextBuilder.plus(other: String): TextBuilder {
		children += Text.literal(other)
		return this
	}

	operator fun TextBuilder.plus(other: Text): TextBuilder {
		children += other
		return this
	}

	fun build(): MutableText = MutableText(PlainTextContent.EMPTY, children, Style.EMPTY)
}

class StyleBuilder() {
	var color: Color? = null
	var shadowColor: Color? = null
	var clickEvent: ClickEvent? = null
	var hoverEvent: HoverEvent? = null

	// bold, italic, underlined, strikethrough, obfuscated
	private var decorations: UByte = 0b00000u

	/**
	 * Sets the text to be obfuscated.
	 */
	val obfuscated: Unit
		get() {
			decorations = decorations or 0b00001u
		}

	/**
	 * Sets the text to be unobfuscated.
	 */
	val notObfuscated: Unit
		get() {
			decorations = decorations and 0b11110u
		}

	/**
	 * Sets the text to be strikethrough.
	 */
	val strikeThrough: Unit
		get() {
			decorations = decorations or 0b00010u
		}

	/**
	 * Sets the text to not be strikethrough.
	 */
	val notStrikeThrough: Unit
		get() {
			decorations = decorations and 0b11101u
		}

	/**
	 * Sets the text to be underlined.
	 */
	val underlined: Unit
		get() {
			decorations = decorations or 0b00100u
		}

	/**
	 * Sets the text to not be underlined.
	 */
	val notUnderlined: Unit
		get() {
			decorations = decorations and 0b11011u
		}

	/**
	 * Sets the text to be *italic*.
	 */
	val italic: Unit
		get() {
			decorations = decorations or 0b01000u
		}

	/**
	 * Sets the text to not be italic.
	 */
	val notItalic: Unit
		get() {
			decorations = decorations and 0b10111u
		}

	/**
	 * Sets the text to be **bold**.
	 */
	val bold: Unit
		get() {
			decorations = decorations or 0b10000u
		}

	/**
	 * Sets the text to not be bold.
	 */
	val notBold: Unit
		get() {
			decorations = decorations and 0b01111u
		}

	fun build(): Style = Style(
		color?.rgb?.let { TextColor.fromRgb(it) },
		shadowColor?.rgb,
		decorations and 0b10000u != 0.toUByte(),
		decorations and 0b01000u != 0.toUByte(),
		decorations and 0b00100u != 0.toUByte(),
		decorations and 0b00010u != 0.toUByte(),
		decorations and 0b00001u != 0.toUByte(),
		clickEvent,
		hoverEvent,
		null,
		null
	)

	/**
	 * Convenience method for creating a [ClickEvent] that opens an url.
	 * @param url The url to open.
	 */
	inline fun openUrl(url: String): ClickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)

	/**
	 * Convenience method for creating a [ClickEvent] that opens a file.
	 * @param file The file to open.
	 */
	inline fun openFile(file: String): ClickEvent = ClickEvent(ClickEvent.Action.OPEN_FILE, file)

	/**
	 * Convenience method for creating a [ClickEvent] that opens a file.
	 * @param path The file to open.
	 */
	inline fun openFile(path: Path): ClickEvent = openFile(path.absolutePathString())

	/**
	 * Convenience method for creating a [ClickEvent] that opens a file.
	 * @param file The file to open.
	 */
	inline fun openFile(file: File): ClickEvent = openFile(file.absolutePath)

	/**
	 * Convenience method for creating a [ClickEvent] that runs a command.
	 * @param command The command to run.
	 */
	inline fun runCommand(command: String): ClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)

	/**
	 * Convenience method for creating a [ClickEvent] that suggests a command.
	 * @param command The command to suggest.
	 */
	inline fun suggestCommand(command: String): ClickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)

	/**
	 * Convenience method for creating a [ClickEvent] that changes the page of a book.
	 * @param page The page number to change to.
	 */
	inline fun changePage(page: String): ClickEvent = ClickEvent(ClickEvent.Action.CHANGE_PAGE, page)

	/**
	 * Convenience method for creating a [ClickEvent] that changes the page of a book.
	 * @param page The page to change to.
	 */
	inline fun changePage(page: Int): ClickEvent = changePage(page.toString())

	/**
	 * Convenience method for creating a [ClickEvent] that copies text to the clipboard.
	 * @param text The text to copy.
	 */
	inline fun copyToClipboard(text: String): ClickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text)

	/**
	 * Convenience method for creating a [HoverEvent] that shows text.
	 * @param text The text to show.
	 */
	inline fun showText(text: Text) = HoverEvent(HoverEvent.Action.SHOW_TEXT, text)

	/**
	 * Convenience method for creating a [HoverEvent] that shows text.
	 * @param string The text to show.
	 */
	inline fun showText(string: String) = HoverEvent(HoverEvent.Action.SHOW_TEXT, string.text)

	/**
	 * Convenience method for creating a [HoverEvent] that shows text.
	 * @param builder The text builder to build the text to show.
	 */
	inline fun showText(builder: TextBuilder.() -> Unit) = showText(text(builder))

	/**
	 * Convenience method for creating a [HoverEvent] that shows an item.
	 * @param item The item to show.
	 */
	inline fun showItem(item: ItemStack) = HoverEvent(HoverEvent.Action.SHOW_ITEM, ItemStackContent(item))

	/**
	 * Convenience method for creating a [HoverEvent] that shows an entity.
	 * @param entityType The type of the entity.
	 * @param uuid The uuid of the entity.
	 * @param name The name of the entity.
	 */
	inline fun showEntity(entityType: EntityType<*>, uuid: UUID, name: Text? = null) = HoverEvent(HoverEvent.Action.SHOW_ENTITY, HoverEvent.EntityContent(entityType, uuid, name))

	/**
	 * Convenience method for creating a [HoverEvent] that shows an entity.
	 *
	 * This overload uses the [Uuid] class from kotlin.
	 * @param entityType The type of the entity.
	 * @param uuid The uuid of the entity.
	 * @param name The name of the entity.
	 */
	@OptIn(ExperimentalUuidApi::class)
	inline fun showEntity(entityType: EntityType<*>, uuid: Uuid, name: Text? = null) = showEntity(entityType, uuid.toJavaUuid(), name)
}