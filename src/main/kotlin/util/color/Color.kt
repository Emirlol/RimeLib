@file:Suppress("NOTHING_TO_INLINE")

package me.ancientri.rimelib.util.color

import com.danrusu.pods4k.immutableArrays.ImmutableByteArray
import com.danrusu.pods4k.immutableArrays.ImmutableFloatArray
import com.danrusu.pods4k.immutableArrays.immutableArrayOf
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec

/**
 * Represents a color in ARGB format, where each channel (alpha, red, green, blue) is an 8-bit integer packed into a single 32-bit integer.
 *
 * On the JVM, non-nullable values of this type are represented as the values of the primitive type `int`.
 */
@JvmInline
value class Color(val value: Int) {
	// Constructor is hidden to prevent direct instantiation with an invalid packed integer.
	// It wouldn't cause any errors to allow direct instantiation, but it might lead to an unexpected color if the channels are not in the correct range.
	// This helps ensure that the color is always created with valid channel values.
	private constructor(alpha: Int, red: Int, green: Int, blue: Int) : this((alpha shl 24) or (red shl 16) or (green shl 8) or blue)

	val red: Int
		get() = (value shr 16) and 0xFF
	val green: Int
		get() = (value shr 8) and 0xFF
	val blue: Int
		get() = value and 0xFF
	val alpha: Int
		get() = (value shr 24) and 0xFF

	inline fun isVisible(): Boolean = alpha > 0

	inline fun asOpaque(): Color = withAlpha(255)

	inline fun asTransparent(): Color = withAlpha(0)

	fun withAlpha(alpha: Int): Color {
		require(alpha in 0..255) { "Alpha channel must be between 0 and 255, but was $alpha" }
		return Color(alpha, red, green, blue)
	}

	fun withRed(red: Int): Color {
		require(red in 0..255) { "Red channel must be between 0 and 255, but was $red" }
		return Color(alpha, red, green, blue)
	}

	fun withGreen(green: Int): Color {
		require(green in 0..255) { "Green channel must be between 0 and 255, but was $green" }
		return Color(alpha, red, green, blue)
	}

	fun withBlue(blue: Int): Color {
		require(blue in 0..255) { "Blue channel must be between 0 and 255, but was $blue" }
		return Color(alpha, red, green, blue)
	}

	inline fun toFloatArray(): ImmutableFloatArray = immutableArrayOf(
		alpha / 255f,
		red / 255f,
		green / 255f,
		blue / 255f
	)

	inline fun toByteArray(): ImmutableByteArray = immutableArrayOf(
		(alpha and 0xFF).toByte(),
		(red and 0xFF).toByte(),
		(green and 0xFF).toByte(),
		(blue and 0xFF).toByte()
	)

	override fun toString(): String = "Color(alpha=$alpha, red=$red, green=$green, blue=$blue)"

	companion object {
		val CODEC: PrimitiveCodec<Int>
			get() = Codec.INT

		fun opaque(value: Int): Color = Color(value or 0xFF000000.toInt())

		fun transparent(value: Int): Color = Color(value and 0x00FFFFFF)

		/**
		 * Creates a color from the given RGB integer values with full opacity (alpha = 255).
		 * @param red The red channel (0-255).
		 * @param green The green channel (0-255).
		 * @param blue The blue channel (0-255).
		 * @return A Color instance representing the color.
		 * @throws IllegalArgumentException If any channel is out of range.
		 */
		fun opaque(red: Int, green: Int, blue: Int): Color {
			require(red in 0..255) { "Red channel must be between 0 and 255, but was $red" }
			require(green in 0..255) { "Green channel must be between 0 and 255, but was $green" }
			require(blue in 0..255) { "Blue channel must be between 0 and 255, but was $blue" }
			return Color(255, red, green, blue)
		}

		/**
		 * Creates a color from the given RGB float values with full opacity (alpha = 255).
		 * @param red The red channel (0.0-1.0).
		 * @param green The green channel (0.0-1.0).
		 * @param blue The blue channel (0.0-1.0).
		 * @return A Color instance representing the color.
		 * @throws IllegalArgumentException If any channel is out of range.
		 */
		fun opaque(red: Float, green: Float, blue: Float): Color {
			require(red in 0f..1f) { "Red channel must be between 0.0 and 1.0, but was $red" }
			require(green in 0f..1f) { "Green channel must be between 0.0 and 1.0, but was $green" }
			require(blue in 0f..1f) { "Blue channel must be between 0.0 and 1.0, but was $blue" }
			return opaque((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())
		}

		/**
		 * Creates a color from the given RGB integer values with full transparency (alpha = 0).
		 *
		 * @param red The red channel (0-255).
		 * @param green The green channel (0-255).
		 * @param blue The blue channel (0-255).
		 * @return A Color instance representing the color.
		 * @throws IllegalArgumentException If any channel is out of range.
		 */
		fun transparent(red: Int, green: Int, blue: Int): Color {
			require(red in 0..255) { "Red channel must be between 0 and 255, but was $red" }
			require(green in 0..255) { "Green channel must be between 0 and 255, but was $green" }
			require(blue in 0..255) { "Blue channel must be between 0 and 255, but was $blue" }
			return Color(0, red, green, blue)
		}

		/**
		 * Creates a color from the given RGB float values with full transparency (alpha = 0).
		 *
		 * @param red The red channel (0.0-1.0).
		 * @param green The green channel (0.0-1.0).
		 * @param blue The blue channel (0.0-1.0).
		 * @return A Color instance representing the color.
		 * @throws IllegalArgumentException If any channel is out of range.
		 */
		fun transparent(red: Float, green: Float, blue: Float): Color {
			require(red in 0f..1f) { "Red channel must be between 0.0 and 1.0, but was $red" }
			require(green in 0f..1f) { "Green channel must be between 0.0 and 1.0, but was $green" }
			require(blue in 0f..1f) { "Blue channel must be between 0.0 and 1.0, but was $blue" }
			return transparent((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())
		}

		/**
		 * Creates a color from the given ARGB integer values.
		 *
		 * @param alpha The alpha channel (0-255).
		 * @param red The red channel (0-255).
		 * @param green The green channel (0-255).
		 * @param blue The blue channel (0-255).
		 * @return A Color instance representing the color.
		 * @throws IllegalArgumentException If any channel is out of range.
		 */
		operator fun invoke(alpha: Int, red: Int, green: Int, blue: Int): Color {
			require(alpha in 0..255) { "Alpha channel must be between 0 and 255, but was $alpha" }
			require(red in 0..255) { "Red channel must be between 0 and 255, but was $red" }
			require(green in 0..255) { "Green channel must be between 0 and 255, but was $green" }
			require(blue in 0..255) { "Blue channel must be between 0 and 255, but was $blue" }
			return Color(alpha, red, green, blue)
		}

		/**
		 * Creates a color from the given ARGB float values.
		 *
		 * @param alpha The alpha channel (0.0-1.0).
		 * @param red The red channel (0.0-1.0).
		 * @param green The green channel (0.0-1.0).
		 * @param blue The blue channel (0.0-1.0).
		 * @return A Color instance representing the color.
		 * @throws IllegalArgumentException If any channel is out of range.
		 */
		operator fun invoke(alpha: Float, red: Float, green: Float, blue: Float): Color {
			require(alpha in 0f..1f) { "Alpha channel must be between 0.0 and 1.0, but was $alpha" }
			require(red in 0f..1f) { "Red channel must be between 0.0 and 1.0, but was $red" }
			require(green in 0f..1f) { "Green channel must be between 0.0 and 1.0, but was $green" }
			require(blue in 0f..1f) { "Blue channel must be between 0.0 and 1.0, but was $blue" }
			return Color((alpha * 255).toInt(), (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())
		}

		/**
		 * Creates a color from a given Java AWT color.
		 * @param color The AWT color to convert.
		 * @return A Color instance representing the AWT color.
		 */
		inline fun fromAwt(color: java.awt.Color) = Color(color.rgb)
	}
}