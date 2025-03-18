package me.rime.rimelib.util

import com.danrusu.pods4k.immutableArrays.immutableArrayOf
import me.rime.rimelib.util.ColorPalette.GREEN
import me.rime.rimelib.util.ColorPalette.MAUVE
import me.rime.rimelib.util.ColorPalette.PEACH
import me.rime.rimelib.util.ColorPalette.RED
import me.rime.rimelib.util.ColorPalette.SURFACE1
import me.rime.rimelib.util.ColorPalette.TEXT
import me.rime.rimelib.util.ColorUtil.withAlpha
import java.awt.Color

/**
 * The catppuccin mocha color palette.
 *
 * | Usage                                  | Color      |
 * | -------------------------------------- | ---------- |
 * | Main color when nothing else applies   | [MAUVE]    |
 * | Positive, like success or profit       | [GREEN]    |
 * | Negative, like error or loss           | [RED]      |
 * | Warning or maxed progression           | [PEACH]    |
 * | Text                                   | [TEXT]     |
 * | Meh gray                               | [SURFACE1] |
 *
 * @see ColorUtil.withAlpha
 */
object ColorPalette { // The official java package for catppuccin colors sucks so this is my solution. It doesn't have all 4 palettes but it's good enough.
	val ROSEWATER = Color(0xf5e0dc)
	val FLAMINGO = Color(0xf2cdcd)
	val PINK = Color(0xf5c2e7)
	val MAUVE = Color(0xcba6f7)
	val ACCENT get() = MAUVE
	val RED = Color(0xf38ba8)
	val ERROR get() = RED     // alias
	val MAROON = Color(0xeba0ac)
	val PEACH = Color(0xfab387)
	val WARNING get() = PEACH // alias
	val YELLOW = Color(0xf9e2af)
	val GREEN = Color(0xa6e3a1)
	val SUCCESS get() = GREEN // alias
	val TEAL = Color(0x94e2d5)
	val SKY = Color(0x89dceb)
	val SAPPHIRE = Color(0x74c7ec)
	val BLUE = Color(0x89b4fa)
	val LAVENDER = Color(0xb4befe)
	val TEXT = Color(0xcdd6f4)
	val SUBTEXT1 = Color(0xbac2de)
	val SUBTEXT2 = Color(0xa6adc8)
	val OVERLAY1 = Color(0x7f849c)
	val OVERLAY2 = Color(0x6c7086)
	val SURFACE1 = Color(0x585b70)
	val SURFACE2 = Color(0x45475a)
	val SURFACE3 = Color(0x313244)
	val BASE = Color(0x1e1e2e)
	val MANTLE = Color(0x181825)
	val CRUST = Color(0x11111b)

	val ACCENTS = immutableArrayOf(ROSEWATER, FLAMINGO, PINK, MAUVE, RED, MAROON, PEACH, YELLOW, GREEN, TEAL, SKY, SAPPHIRE, BLUE, LAVENDER)
}