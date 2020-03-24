package com.github.hejcz.cartographers

interface ScoreCard {
    fun evaluate(board: Board): Int
}

object ForestTower28 : ScoreCard {
    override fun evaluate(board: Board): Int {
        val forests = board.all { it == Terrain.FOREST }
        return forests.fold(0) { total, p ->
            when {
                p.x < 0 && board.terrainAt(p.moveX(-1)) == Terrain.EMPTY ||
                        p.x > -10 && board.terrainAt(p.moveX(1)) == Terrain.EMPTY ||
                        p.y > 0 && board.terrainAt(p.moveY(-1)) == Terrain.EMPTY ||
                        p.y < 10 && board.terrainAt(p.moveY(1)) == Terrain.EMPTY -> {
                    total
                }
                else -> total + 1
            }
        }
    }
}

object ForestGuard26 : ScoreCard {
    override fun evaluate(board: Board): Int =
        // rows does not count first and last so it does not duplicate in total score
        // first row
        (1..9).count { board.terrainAt(Point(0, it)) == Terrain.FOREST } +
                // last row
                (1..9).count { board.terrainAt(Point(-10, it)) == Terrain.FOREST } +
                // first column
                (0..10).count { board.terrainAt(Point(-it, 0)) == Terrain.FOREST } +
                // last column
                (0..10).count { board.terrainAt(Point(-it, 10)) == Terrain.FOREST }
}

object Coppice27 : ScoreCard {
    override fun evaluate(board: Board): Int =
        (0..10).count { row -> (0..10).any { board.terrainAt(Point(-row, it)) == Terrain.FOREST } } +
                (0..10).count { col -> (0..10).any { board.terrainAt(Point(-it, col)) == Terrain.FOREST } }
}

object MountainWoods29 : ScoreCard {
    override fun evaluate(board: Board): Int {
        val mountains = board.all { it == Terrain.MOUNTAIN }
        val connectedForests = board.connectedTerrains(Terrain.FOREST)
        val mountainsPairs = mountains.flatMap { mountain1 -> mountains.map { mountain1 to it } }
            .filter { (m1, m2) -> m1 != m2 && (m1.x > m2.x || m1.x == m2.x && m1.y > m2.y)  }
        return 3 * mountainsPairs.filter { (m1, m2) ->
            val m1Adjacent = board.adjacent(m1)
            val m2Adjacent = board.adjacent(m2)
            connectedForests.any { m1Adjacent.any(it::contains) && m2Adjacent.any(it::contains) }
        }
            // if m1 is connected to m2 and m2 to m3 we don't want to count pairs but mountain connected to another
            .flatMap { setOf(it.first, it.second) }
            .distinct()
            .count()
    }
}

object HugeCity35 : ScoreCard {
    override fun evaluate(board: Board): Int =
        board.connectedTerrains(Terrain.CITY).filter { city ->
            city.flatMap { board.adjacent(it) }.toSet().all { board.terrainAt(it) != Terrain.MOUNTAIN }
        }.maxBy { it.size }?.size ?: 0
}

object Fortress37 : ScoreCard {
    override fun evaluate(board: Board): Int =
        board.connectedTerrains(Terrain.CITY).let {
            when {
                it.isEmpty() || it.size == 1 -> 0
                else -> 2 *
                        (it.sortedByDescending { cityCluster -> cityCluster.size }.drop(1).firstOrNull()?.size ?: 0)
            }
        }
}

object Colony34 : ScoreCard {
    override fun evaluate(board: Board): Int =
        6 * board.connectedTerrains(Terrain.CITY).filter { it.size >= 6 }.count()
}

object FertilePlain36 : ScoreCard {
    override fun evaluate(board: Board): Int =
        3 * board.connectedTerrains(Terrain.CITY).count { city ->
            city.flatMap { board.adjacent(it) }
                .groupBy { board.terrainAt(it) }
                .keys
                .filter { it != Terrain.EMPTY && it != Terrain.CITY }
                .count() >= 3
        }
}

object FieldPuddle30 : ScoreCard {
    override fun evaluate(board: Board): Int =
        board.all { it == Terrain.WATER }
            .count { lake -> board.adjacent(lake).any { board.terrainAt(it) == Terrain.PLAINS } } +
                board.all { it == Terrain.PLAINS }
                    .count { plains -> board.adjacent(plains).any { board.terrainAt(it) == Terrain.WATER } }
}

object MagesValley31 : ScoreCard {
    override fun evaluate(board: Board): Int =
        2 * board.all { it == Terrain.WATER }
            .count { lake -> board.adjacent(lake).any { board.terrainAt(it) == Terrain.MOUNTAIN } } +
                board.all { it == Terrain.PLAINS }
                    .count { plains -> board.adjacent(plains).any { board.terrainAt(it) == Terrain.MOUNTAIN } }
}

object VastEnbankment33 : ScoreCard {
    override fun evaluate(board: Board): Int = 3 * (
            listOf(
                Terrain.WATER to Terrain.PLAINS,
                Terrain.PLAINS to Terrain.WATER
            ).sumBy { (terrain, forbidden) ->
                board.connectedTerrains(terrain).count { points ->
                    if (points.any { (x, y) -> x == 0 || x == -10 || y == 0 || y == 10 }) {
                        false
                    } else {
                        val adjacent = points.flatMap { board.adjacent(it) }.toSet()
                        adjacent.none { board.terrainAt(it) == forbidden }
                    }
                }
            }
        )
}

object GoldenBreadbasket32 : ScoreCard {
    override fun evaluate(board: Board): Int =
        3 * board.all { it == Terrain.PLAINS }
            .count { board.hasRuinsOn(it) } +
                board.all { it == Terrain.WATER }
                    .count { water -> board.adjacent(water).any { board.hasRuinsOn(it) } }
}

object Hideouts41 : ScoreCard {
    override fun evaluate(board: Board): Int = board.allEmpty()
        .fold(0) { total, p ->
            when {
                p.x < 0 && board.terrainAt(p.moveX(-1)) == Terrain.EMPTY ||
                        p.x > -10 && board.terrainAt(p.moveX(1)) == Terrain.EMPTY ||
                        p.y > 0 && board.terrainAt(p.moveY(-1)) == Terrain.EMPTY ||
                        p.y < 10 && board.terrainAt(p.moveY(1)) == Terrain.EMPTY -> {
                    total
                }
                else -> total + 1
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