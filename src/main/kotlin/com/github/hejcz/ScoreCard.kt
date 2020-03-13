package com.github.hejcz

interface ScoreCard {
    fun evaluate(board: Board): Int
}

class ForestTower28 : ScoreCard {
    override fun evaluate(board: Board): Int {
        val forests = board.all { it == Terrain.FOREST }
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

class ForestGuard26 : ScoreCard {
    override fun evaluate(board: Board): Int =
        (0..10).count { board.terrainAt(0, it) == Terrain.FOREST } +
                (0..10).count { board.terrainAt(-10, it) == Terrain.FOREST } +
                (0..10).count { board.terrainAt(-it, 0) == Terrain.FOREST } +
                (0..10).count { board.terrainAt(-it, 10) == Terrain.FOREST }
}

class Coppice27 : ScoreCard {
    override fun evaluate(board: Board): Int =
        (0..10).count { row -> (0..10).any { board.terrainAt(-row, it) == Terrain.FOREST } } +
                (0..10).count { col -> (0..10).any { board.terrainAt(-it, col) == Terrain.FOREST } }
}

class MountainWoods29 : ScoreCard {
    override fun evaluate(board: Board): Int = 0
}

class Hideouts41 : ScoreCard {
    override fun evaluate(board: Board): Int {
        val forests = board.all { it == Terrain.EMPTY }
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

class LostDemesne39 : ScoreCard {
    override fun evaluate(board: Board): Int =
        3 * board.biggestSquareLength()
}

class Borderlands38 : ScoreCard {
    override fun evaluate(board: Board): Int =
        6 * board.countFullRowsAndColumns()
}

class TradingRoad40 : ScoreCard {
    override fun evaluate(board: Board): Int =
        3 * board.countLeftToBottomDiameters()
}