package me.rime.rimelib.util

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

fun <A, T> dataClassCodecBuilder(constructor: (A) -> T, codec: RecordCodecBuilder<T, A>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec).apply(it, constructor)
}

fun <A, B, T> dataClassCodecBuilder(constructor: (A, B) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2).apply(it, constructor)
}

fun <A, B, C, T> dataClassCodecBuilder(constructor: (A, B, C) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3).apply(it, constructor)
}

fun <A, B, C, D, T> dataClassCodecBuilder(constructor: (A, B, C, D) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4).apply(it, constructor)
}

fun <A, B, C, D, E, T> dataClassCodecBuilder(constructor: (A, B, C, D, E) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5).apply(it, constructor)
}

fun <A, B, C, D, E, F, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6).apply(it, constructor)
}

fun <A, B, C, D, E, F, G, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F, G) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>, codec7: RecordCodecBuilder<T, G>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6, codec7).apply(it, constructor)
}

fun <A, B, C, D, E, F, G, H, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F, G, H) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>, codec7: RecordCodecBuilder<T, G>, codec8: RecordCodecBuilder<T, H>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6, codec7, codec8).apply(it, constructor)
}

fun <A, B, C, D, E, F, G, H, I, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F, G, H, I) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>, codec7: RecordCodecBuilder<T, G>, codec8: RecordCodecBuilder<T, H>, codec9: RecordCodecBuilder<T, I>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9).apply(it, constructor)
}

fun <A, B, C, D, E, F, G, H, I, J, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F, G, H, I, J) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>, codec7: RecordCodecBuilder<T, G>, codec8: RecordCodecBuilder<T, H>, codec9: RecordCodecBuilder<T, I>, codec10: RecordCodecBuilder<T, J>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10).apply(it, constructor)
}

fun <A, B, C, D, E, F, G, H, I, J, K, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F, G, H, I, J, K) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>, codec7: RecordCodecBuilder<T, G>, codec8: RecordCodecBuilder<T, H>, codec9: RecordCodecBuilder<T, I>, codec10: RecordCodecBuilder<T, J>, codec11: RecordCodecBuilder<T, K>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11).apply(it, constructor)
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F, G, H, I, J, K, L) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>, codec7: RecordCodecBuilder<T, G>, codec8: RecordCodecBuilder<T, H>, codec9: RecordCodecBuilder<T, I>, codec10: RecordCodecBuilder<T, J>, codec11: RecordCodecBuilder<T, K>, codec12: RecordCodecBuilder<T, L>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12).apply(it, constructor)
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>, codec7: RecordCodecBuilder<T, G>, codec8: RecordCodecBuilder<T, H>, codec9: RecordCodecBuilder<T, I>, codec10: RecordCodecBuilder<T, J>, codec11: RecordCodecBuilder<T, K>, codec12: RecordCodecBuilder<T, L>, codec13: RecordCodecBuilder<T, M>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13).apply(it, constructor)
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>, codec7: RecordCodecBuilder<T, G>, codec8: RecordCodecBuilder<T, H>, codec9: RecordCodecBuilder<T, I>, codec10: RecordCodecBuilder<T, J>, codec11: RecordCodecBuilder<T, K>, codec12: RecordCodecBuilder<T, L>, codec13: RecordCodecBuilder<T, M>, codec14: RecordCodecBuilder<T, N>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14).apply(it, constructor)
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>, codec7: RecordCodecBuilder<T, G>, codec8: RecordCodecBuilder<T, H>, codec9: RecordCodecBuilder<T, I>, codec10: RecordCodecBuilder<T, J>, codec11: RecordCodecBuilder<T, K>, codec12: RecordCodecBuilder<T, L>, codec13: RecordCodecBuilder<T, M>, codec14: RecordCodecBuilder<T, N>, codec15: RecordCodecBuilder<T, O>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14, codec15).apply(it, constructor)
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, T> dataClassCodecBuilder(constructor: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> T, codec: RecordCodecBuilder<T, A>, codec2: RecordCodecBuilder<T, B>, codec3: RecordCodecBuilder<T, C>, codec4: RecordCodecBuilder<T, D>, codec5: RecordCodecBuilder<T, E>, codec6: RecordCodecBuilder<T, F>, codec7: RecordCodecBuilder<T, G>, codec8: RecordCodecBuilder<T, H>, codec9: RecordCodecBuilder<T, I>, codec10: RecordCodecBuilder<T, J>, codec11: RecordCodecBuilder<T, K>, codec12: RecordCodecBuilder<T, L>, codec13: RecordCodecBuilder<T, M>, codec14: RecordCodecBuilder<T, N>, codec15: RecordCodecBuilder<T, O>, codec16: RecordCodecBuilder<T, P>): Codec<T> = RecordCodecBuilder.create {
	it.group(codec, codec2, codec3, codec4, codec5, codec6, codec7, codec8, codec9, codec10, codec11, codec12, codec13, codec14, codec15, codec16).apply(it, constructor)
}
