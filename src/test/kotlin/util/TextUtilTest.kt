package util

import me.rime.rimelib.util.ColorPalette
import me.rime.rimelib.util.styled
import me.rime.rimelib.util.text
import net.minecraft.Bootstrap
import net.minecraft.SharedConstants
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class TextUtilTest {
	@Test
	fun test() {
		assertEquals(
			Text.empty()
				.append(Text.literal("Hello").withColor(ColorPalette.TEXT.rgb))
				.append(" ")
				.append(Text.literal("World").withColor(ColorPalette.ACCENT.rgb))
				.append("!"),
			text {
				+"Hello".text(ColorPalette.TEXT)
				+" "
				+"World".text(ColorPalette.ACCENT)
				+"!".text
			}
		)

		assertEquals(
			Text.empty()
				.append(Text.literal("This is a clickable link.").styled {
					it.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/say Hello, World!"))
						.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click me!")))
						.withColor(ColorPalette.ACCENT.rgb)
						.withItalic(true)
						.withBold(true)
						.withObfuscated(false)
						.withStrikethrough(false)
						.withUnderline(false)
				})
				.append(Text.literal("This will open a URL.").styled {
					it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
						.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.empty().append(Text.literal("Click me!").withColor(ColorPalette.GREEN.rgb))))
						.withUnderline(true)
						.withObfuscated(false)
						.withStrikethrough(false)
						.withItalic(false)
						.withBold(false)
						.withColor(ColorPalette.LAVENDER.rgb)
				}),
			text {
				+"This is a clickable link.".styled {
					clickEvent = runCommand("/say Hello, World!")
					color = ColorPalette.ACCENT
					hoverEvent = showText("Click me!")
					italic
					bold
				}
				+"This will open a URL.".styled {
					clickEvent = openUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
					color = ColorPalette.LAVENDER
					hoverEvent = showText { +"Click me!".text(ColorPalette.GREEN) }
					underlined
				}
			}
		)
	}

	companion object {
		@BeforeAll
		@JvmStatic
		fun setupEnvironment() {
			SharedConstants.createGameVersion()
			Bootstrap.initialize()
		}
	}
}