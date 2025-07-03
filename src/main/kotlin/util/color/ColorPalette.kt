package me.ancientri.rimelib.util.color

import com.danrusu.pods4k.immutableArrays.immutableArrayOf
import me.ancientri.rimelib.util.color.ColorPalette.GREEN
import me.ancientri.rimelib.util.color.ColorPalette.MAUVE
import me.ancientri.rimelib.util.color.ColorPalette.PEACH
import me.ancientri.rimelib.util.color.ColorPalette.RED
import me.ancientri.rimelib.util.color.ColorPalette.SURFACE1
import me.ancientri.rimelib.util.color.ColorPalette.TEXT

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
 */
object ColorPalette { // The official java package for catppuccin colors sucks so this is my solution. It doesn't have all 4 palettes but it's good enough.
	val ROSEWATER = Color.opaque(0xf5e0dc)
	val FLAMINGO = Color.opaque(0xf2cdcd)
	val PINK = Color.opaque(0xf5c2e7)
	val MAUVE = Color.opaque(0xcba6f7)
	val ACCENT get() = MAUVE
	val RED = Color.opaque(0xf38ba8)
	inline val ERROR get() = RED     // alias
	val MAROON = Color.opaque(0xeba0ac)
	val PEACH = Color.opaque(0xfab387)
	inline val WARNING get() = PEACH // alias
	val YELLOW = Color.opaque(0xf9e2af)
	val GREEN = Color.opaque(0xa6e3a1)
	inline val SUCCESS get() = GREEN // alias
	val TEAL = Color.opaque(0x94e2d5)
	val SKY = Color.opaque(0x89dceb)
	val SAPPHIRE = Color.opaque(0x74c7ec)
	val BLUE = Color.opaque(0x89b4fa)
	val LAVENDER = Color.opaque(0xb4befe)
	val TEXT = Color.opaque(0xcdd6f4)
	val SUBTEXT1 = Color.opaque(0xbac2de)
	val SUBTEXT2 = Color.opaque(0xa6adc8)
	val OVERLAY1 = Color.opaque(0x7f849c)
	val OVERLAY2 = Color.opaque(0x6c7086)
	val SURFACE1 = Color.opaque(0x585b70)
	val SURFACE2 = Color.opaque(0x45475a)
	val SURFACE3 = Color.opaque(0x313244)
	val BASE = Color.opaque(0x1e1e2e)
	val MANTLE = Color.opaque(0x181825)
	val CRUST = Color.opaque(0x11111b)

	val ACCENTS = immutableArrayOf(ROSEWATER, FLAMINGO, PINK, MAUVE, RED, MAROON, PEACH, YELLOW, GREEN, TEAL, SKY, SAPPHIRE, BLUE, LAVENDER)
}