package com.github.hejcz

interface ScoreCard {
    fun evaluate(board: Board): Int
}

// Leśna wieża
class Card28 : ScoreCard {
    override fun evaluate(board: Board): Int {
        val forests = board.all(Terrain.FOREST)
        return forests.fold(0) { total, (x, y) ->
            when {
                x > 0 && board.terrainAt(x - 1, y) == Terrain.EMPTY ||
                        x < 10 && board.terrainAt(x + 1, y) == Terrain.EMPTY ||
                        y < 0 && board.terrainAt(x, y - 1) == Terrain.EMPTY ||
                        y > -10 && board.terrainAt(x, y + 1) == Terrain.EMPTY -> {
                    total
                }
                else -> total + 1
            }
        }
    }
}