package util

import me.ancientri.rimelib.util.color.ColorPalette
import me.ancientri.rimelib.util.text.text
import net.minecraft.text.ClickEvent.OpenUrl
import net.minecraft.text.ClickEvent.RunCommand
import net.minecraft.text.HoverEvent.ShowText
import net.minecraft.text.Text
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URI

class TextUtilTest {
	@Test
	fun test() {
		assertEquals(
			Text.empty()
				.append(Text.literal("Hello").withColor(ColorPalette.TEXT.value))
				.append(" ")
				.append(Text.literal("World").withColor(ColorPalette.ACCENT.value))
				.append("!"),
			text("") {
				"Hello" colored ColorPalette.TEXT
				+" "
				"World" colored ColorPalette.ACCENT
				+"!"
			}
		)

		assertEquals(
			Text.empty()
				.append(Text.literal("This is a clickable link.").styled {
					it.withClickEvent(RunCommand("/say Hello, World!"))
						.withHoverEvent(ShowText(Text.literal("Click me!")))
						.withColor(ColorPalette.ACCENT.value)
						.withItalic(true)
						.withBold(true)
						.withObfuscated(false)
						.withStrikethrough(false)
						.withUnderline(false)
				})
				.append(Text.literal("This will open a URL.").styled {
					it.withClickEvent(OpenUrl(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ")))
						.withHoverEvent(ShowText(Text.empty().append(Text.literal("Click me!").withColor(ColorPalette.GREEN.value))))
						.withUnderline(true)
						.withObfuscated(false)
						.withStrikethrough(false)
						.withItalic(false)
						.withBold(false)
						.withColor(ColorPalette.LAVENDER.value)
				}),
			text {
				"This is a clickable link." styled {
					clickEvent = runCommand("/say Hello, World!")
					color = ColorPalette.ACCENT
					hoverEvent = showText("Click me!")
					italic
					bold
				}
				"This will open a URL." styled {
					clickEvent = openUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
					color = ColorPalette.LAVENDER
					hoverEvent = showText { "Click me!" colored ColorPalette.GREEN }
					underlined
				}
			}
		)
	}
}