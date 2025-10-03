@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util.text

import me.ancientri.rimelib.util.color.Color
import net.minecraft.entity.EntityType
import net.minecraft.item.ItemStack
import net.minecraft.text.*
import net.minecraft.text.ClickEvent.*
import net.minecraft.text.HoverEvent.*
import net.minecraft.util.Identifier
import java.io.File
import java.net.URI
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

/**
 * A builder for creating [Style] objects in a more concise and idiomatic way for Kotlin.
 *
 * This is meant to be used in conjunction with the [TextBuilder] to create styled text, so the example usage is:
 * ```kotlin
 * text {
 *     "Hello, world!" styled {
 *         color = ColorPalette.TEXT
 *         bold
 *         obfuscated
 *         italic
 *         clickEvent = runCommand("/say Hello, world!")
 *         hoverEvent = showText {
 *             "This is a styled text!\n" colored ColorPalette.SKY
 *             "Click to run a command." colored ColorPalette.TEXT
 *         }
 *     }
 * }
 * ```
 *
 * The [**bold**][bold], [*italic*][italic], [underlined], [strikeThrough] and [obfuscated] text styles can be set using the respective properties without needing to call a method, as the getters themselves set the value of the property.
 * There are also [notBold], [notItalic], [notUnderlined], [notStrikeThrough] and [notObfuscated] properties to unset these styles.
 *
 * If you want to shorten the code even more, you can use `;` to put the style setters in a single line, like `bold; italic; underlined`
 *
 * The [color], [shadowColor], [clickEvent], [hoverEvent], [insertion] and [font] properties are presented as variables that can be get or set directly, and there are convenience methods to create [ClickEvent] and [HoverEvent] instances.
 */
@TextDsl
class StyleBuilder() {
	var color: Color? = null
	var shadowColor: Color? = null
	var clickEvent: ClickEvent? = null
	var hoverEvent: HoverEvent? = null
	var insertion: String? = null
	var font: Identifier? = null

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

	/**
	 * Creates a new [StyleBuilder] with the same properties as the given [style].
	 */
	constructor(style: Style) : this() {
		color = style.color?.let { Color(it.rgb) }
		shadowColor = style.shadowColor?.let(::Color)
		clickEvent = style.clickEvent
		hoverEvent = style.hoverEvent
		insertion = style.insertion
		font = style.font
		if (style.isBold) bold
		if (style.isItalic) italic
		if (style.isUnderlined) underlined
		if (style.isStrikethrough) strikeThrough
		if (style.isObfuscated) obfuscated
	}

	fun build(): Style = Style(
		color?.value?.let { TextColor.fromRgb(it) },
		shadowColor?.value,
		decorations and 0b10000u != 0.toUByte(),
		decorations and 0b01000u != 0.toUByte(),
		decorations and 0b00100u != 0.toUByte(),
		decorations and 0b00010u != 0.toUByte(),
		decorations and 0b00001u != 0.toUByte(),
		clickEvent,
		hoverEvent,
		insertion,
		font
	)

	/**
	 * Convenience method for creating a [ClickEvent] that opens an url.
	 * @param url The url to open.
	 */
	inline fun openUrl(url: String): OpenUrl = openUrl(URI(url))

	/**
	 * Convenience method for creating a [ClickEvent] that opens an url.
	 * @param url The url to open.
	 */
	inline fun openUrl(url: URI): OpenUrl = OpenUrl(url)

	/**
	 * Convenience method for creating a [ClickEvent] that opens a file.
	 * @param file The file to open.
	 */
	inline fun openFile(file: String): OpenFile = OpenFile(file)

	/**
	 * Convenience method for creating a [ClickEvent] that opens a file.
	 * @param path The file to open.
	 */
	inline fun openFile(path: Path): OpenFile = openFile(path.absolutePathString())

	/**
	 * Convenience method for creating a [ClickEvent] that opens a file.
	 * @param file The file to open.
	 */
	inline fun openFile(file: File): OpenFile = openFile(file.absolutePath)

	/**
	 * Convenience method for creating a [ClickEvent] that runs a command.
	 * @param command The command to run.
	 */
	inline fun runCommand(command: String): RunCommand = RunCommand(command)

	/**
	 * Convenience method for creating a [ClickEvent] that suggests a command.
	 * @param command The command to suggest.
	 */
	inline fun suggestCommand(command: String): SuggestCommand = SuggestCommand(command)

	/**
	 * Convenience method for creating a [ClickEvent] that changes the page of a book.
	 * @param page The page number to change to.
	 */
	inline fun changePage(page: String): ChangePage = changePage(page.toInt())

	/**
	 * Convenience method for creating a [ClickEvent] that changes the page of a book.
	 * @param page The page to change to.
	 */
	inline fun changePage(page: Int): ChangePage = ChangePage(page)

	/**
	 * Convenience method for creating a [ClickEvent] that copies text to the clipboard.
	 * @param text The text to copy.
	 */
	inline fun copyToClipboard(text: String): CopyToClipboard = CopyToClipboard(text)

	/**
	 * Convenience method for creating a [HoverEvent] that shows text.
	 * @param text The text to show.
	 */
	inline fun showText(text: Text): ShowText = ShowText(text)

	/**
	 * Convenience method for creating a [HoverEvent] that shows text.
	 * @param string The text to show.
	 */
	inline fun showText(string: String): ShowText = showText(string.text)

	/**
	 * Convenience method for creating a [HoverEvent] that shows text.
	 * @param builder The text builder to build the text to show.
	 */
	inline fun showText(builder: TextBuilder.() -> Unit): ShowText = showText(text(builder))

	/**
	 * Convenience method for creating a [HoverEvent] that shows an item.
	 * @param item The item to show.
	 */
	inline fun showItem(item: ItemStack): ShowItem = ShowItem(item)

	/**
	 * Convenience method for creating a [HoverEvent] that shows an entity.
	 * @param entityType The type of the entity.
	 * @param uuid The uuid of the entity.
	 * @param name The name of the entity.
	 */
	inline fun showEntity(entityType: EntityType<*>, uuid: UUID, name: Text? = null): ShowEntity = ShowEntity(EntityContent(entityType, uuid, name))

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

	companion object {
		// The default font ID from Minecraft's Style class. Included for convenience.
		inline val DEFAULT_FONT_ID: Identifier get() = Style.DEFAULT_FONT_ID
	}
}