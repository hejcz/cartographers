package com.github.hejcz.cartographers

class Player(val nick: String, b: Board) {
    var left: Boolean = false
    var board: Board = b
    var coins = 0
    var lastCoins = 0
    var summaries = listOf<RoundSummary>()
    var lastShape: Shape = NoShape

    override fun toString(): String = "Player(nick='$nick', board=$board, coins=$coins, summaries=$summaries)"
}