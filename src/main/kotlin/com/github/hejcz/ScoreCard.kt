package com.github.hejcz

interface ScoreCard {
    fun evaluate(board: Board): Int
}

object ForestTower28 : ScoreCard {
    override fun evaluate(board: Board): Int {
        val forests = board.all { it == Terrain.FOREST }
        return forests.fold(0) { total, (x, y) ->
            when {
                x < 0 && board.terrainAt(x - 1, y) == Terrain.EMPTY ||
                    x > -10 && board.terrainAt(x + 1, y) == Terrain.EMPTY ||
                    y > 0 && board.terrainAt(x, y - 1) == Terrain.EMPTY ||
                    y < 10 && board.terrainAt(x, y + 1) == Terrain.EMPTY  -> {
                    total
                }
                else -> total + 1
            }
        }
    }
}

object ForestGuard26 : ScoreCard {
    override fun evaluate(board: Board): Int =
        (0..10).count { board.terrainAt(0, it) == Terrain.FOREST } +
                (0..10).count { board.terrainAt(-10, it) == Terrain.FOREST } +
                (0..10).count { board.terrainAt(-it, 0) == Terrain.FOREST } +
                (0..10).count { board.terrainAt(-it, 10) == Terrain.FOREST }
}

object Coppice27 : ScoreCard {
    override fun evaluate(board: Board): Int =
        (0..10).count { row -> (0..10).any { board.terrainAt(-row, it) == Terrain.FOREST } } +
                (0..10).count { col -> (0..10).any { board.terrainAt(-it, col) == Terrain.FOREST } }
}

object MountainWoods29 : ScoreCard {
    override fun evaluate(board: Board): Int {
        val mountains = board.all { it == Terrain.MOUNTAIN }
        val connectedForests = board.connectedTerrains(Terrain.FOREST)
        val roads = mountains.flatMap { (x1, y1) ->
            mountains.mapNotNull { (x2, y2) ->
                when {
                    y1 < y2 || y1 == y2 && x1 > x2 -> (x1 to y1) to (x2 to y2)
                    else -> null
                }
            }
        }
        return roads.count { (m1, m2) ->
            val m1Adjacent = board.adjacent(m1)
            val m2Adjacent = board.adjacent(m2)
            connectedForests.any {
                m1Adjacent.any(it::contains) && m2Adjacent.any(it::contains)
            }
        }
    }
}

object HugeCity35 : ScoreCard {
    override fun evaluate(board: Board): Int =
        board.connectedTerrains(Terrain.CITY).filter { city ->
            city.flatMap { board.adjacent(it) }.toSet().all { (x, y) -> board.terrainAt(x, y) != Terrain.MOUNTAIN }
        }.maxBy { it.size }?.size ?: 0
}

object Fortress37 : ScoreCard {
    override fun evaluate(board: Board): Int =
        2 * (board.connectedTerrains(Terrain.CITY).drop(1).firstOrNull()?.size ?: 0)
}

object Colony34 : ScoreCard {
    override fun evaluate(board: Board): Int =
        6 * board.connectedTerrains(Terrain.CITY).filter { it.size >= 6 }.count()
}

object FertilePlain36 : ScoreCard {
    override fun evaluate(board: Board): Int =
        board.connectedTerrains(Terrain.CITY).count { city ->
            city.flatMap { board.adjacent(it) }.groupBy { (x, y) -> board.terrainAt(x, y) }
                .keys
                .filter { it != Terrain.EMPTY }
                .count() >= 3
        }
}

object FieldPuddle30 : ScoreCard {
    override fun evaluate(board: Board): Int =
        board.all {it == Terrain.WATER }
            .count { lake -> board.adjacent(lake).any { (x, y) -> board.terrainAt(x, y) == Terrain.PLAINS } } +
        board.all {it == Terrain.PLAINS }
            .count { plains -> board.adjacent(plains).any { (x, y) -> board.terrainAt(x, y) == Terrain.WATER } }
}

object MagesValley30 : ScoreCard {
    override fun evaluate(board: Board): Int =
        2 * board.all {it == Terrain.WATER }
            .count { lake -> board.adjacent(lake).any { (x, y) -> board.terrainAt(x, y) == Terrain.MOUNTAIN } } +
        board.all {it == Terrain.PLAINS }
            .count { plains -> board.adjacent(plains).any { (x, y) -> board.terrainAt(x, y) == Terrain.MOUNTAIN } }
}

object VastEnbankment33 : ScoreCard {
    override fun evaluate(board: Board): Int  = 3 * (
        board.connectedTerrains(Terrain.WATER).count { lake ->
            val adjacent = lake.flatMap { board.adjacent(it) }.toSet()
            if (adjacent.any { (x, y) -> x == 0 || x == -10 || y == 0 || y == 10 }) {
                false
            } else {
                adjacent.none { (x, y) -> board.terrainAt(x, y) != Terrain.PLAINS }
            }
        } +
        board.connectedTerrains(Terrain.PLAINS).count { lake ->
            val adjacent = lake.flatMap { board.adjacent(it) }.toSet()
            if (adjacent.any { (x, y) -> x == 0 || x == -10 || y == 0 || y == 10 }) {
                false
            } else {
                adjacent.none { (x, y) -> board.terrainAt(x, y) != Terrain.WATER }
            }
        }
    )
}

object GoldenBreadbasket : ScoreCard {
    override fun evaluate(board: Board): Int =
        3 * board.all { it == Terrain.PLAINS }
            .count { (x, y) -> board.hasRuinsOn(x, y) } +
        board.all { it == Terrain.WATER }
            .count { board.adjacent(it).any { (x, y) -> board.hasRuinsOn(x, y) } }
}

object Hideouts41 : ScoreCard {
    override fun evaluate(board: Board): Int {
        val emptyTerrains = board.allEmpty()
        return emptyTerrains.fold(0) { total, (x, y) ->
            when {
                x < 0 && board.terrainAt(x - 1, y) == Terrain.EMPTY ||
                    x > -10 && board.terrainAt(x + 1, y) == Terrain.EMPTY ||
                    y > 0 && board.terrainAt(x, y - 1) == Terrain.EMPTY ||
                    y < 10 && board.terrainAt(x, y + 1) == Terrain.EMPTY  -> {
                    total
                }
                else -> total + 1
            }
        }
    }
}

object LostDemesne39 : ScoreCard {
    override fun evaluate(board: Board): Int =
        3 * board.biggestSquareLength()
}

object Borderlands38 : ScoreCard {
    override fun evaluate(board: Board): Int =
        6 * board.countFullRowsAndColumns()
}

object TradingRoad40 : ScoreCard {
    override fun evaluate(board: Board): Int =
        3 * board.countLeftToBottomDiameters()
}