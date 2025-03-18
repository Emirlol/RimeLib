package me.rime.rimelib.ui.base

data class Padding(val left: Int, val right: Int, val top: Int, val bottom: Int) {
	constructor(horizontal: Int, vertical: Int) : this(horizontal, horizontal, vertical, vertical)
	constructor(all: Int) : this(all, all, all, all)

	companion object {
		val NONE = Padding(0)
	}
}
