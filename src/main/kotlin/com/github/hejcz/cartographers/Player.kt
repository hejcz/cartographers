package com.github.hejcz.cartographers

class Player(val nick: String) {
    var left: Boolean = false
    var board: Board =
        Board.create()
    var coins = 0
    var summaries = listOf<RoundSummary>()

    override fun toString(): String = "Player(nick='$nick', board=$board, coins=$coins, summaries=$summaries)"
}