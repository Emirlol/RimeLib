package util.collections.enums

import me.rime.rimelib.util.collections.enums.mutableEnumSetOf
import me.rime.rimelib.util.collections.enums.set.mutable.JumboEnumSet
import org.junit.jupiter.api.Test

class MutableEnumSetTest {
	enum class TestEnum {
		A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
		AA, AB, AC, AD, AE, AF, AG, AH, AI, AJ, AK, AL, AM, AN, AO, AP, AQ, AR, AS, AT, AU, AV, AW, AX, AY, AZ,
		BA, BB, BC, BD, BE, BF, BG, BH, BI, BJ, BK, BL, BM, BN, BO, BP, BQ, BR, BS, BT, BU, BV, BW, BX, BY, BZ
	}

	@Test
	fun test() {
		val set = mutableEnumSetOf<TestEnum>().complemented() as JumboEnumSet<TestEnum>
		set.removeAll(TestEnum.B, TestEnum.C, TestEnum.D, TestEnum.E)
		set.addAll(TestEnum.F, TestEnum.G, TestEnum.H, TestEnum.I)
		val expected = TestEnum.entries.toMutableSet().apply { removeAll(listOf(TestEnum.B, TestEnum.C, TestEnum.D, TestEnum.E)) }
		assert(set.containsAll(expected))
	}
}