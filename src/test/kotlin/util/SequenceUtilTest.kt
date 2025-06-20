package util

import me.ancientri.rimelib.util.interleaveWith
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SequenceUtilTest {
	@Test
	fun test() {
		val sequence = sequenceOf(1, 2, 3, 4, 5)
		val interleaved = sequence.interleaveWith(0)
		assertEquals(listOf(1, 0, 2, 0, 3, 0, 4, 0, 5), interleaved.toList())

		val sequence2 = sequenceOf("a", "b", "c", "d", "e")
		val interleaved2 = sequence2.interleaveWith("\n").reduce { acc, s -> acc + s }
		assertEquals("a\nb\nc\nd\ne", interleaved2)
	}
}