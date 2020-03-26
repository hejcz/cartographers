package com.github.hejcz.cartographers

interface ScoreCard {
    fun evaluate(board: Board): Int
}

object ForestTower28 : ScoreCard {
    override fun evaluate(board: Board): Int = board
        .all { it == Terrain.FOREST }
        .count { p -> board.adjacent(p).all { board.terrainAt(it) != Terrain.EMPTY } }
}

object ForestGuard26 : ScoreCard {
    override fun evaluate(board: Board): Int = board
        .all { it == Terrain.FOREST }
        .count { board.isOnBorder(it) }
}

object Coppice27 : ScoreCard {
    override fun evaluate(board: Board): Int = board
        .all { it == Terrain.FOREST }
        .let { forest -> forest.distinctBy { it.x }.size + forest.distinctBy { it.y }.size }
}

object MountainWoods29 : ScoreCard {
    override fun evaluate(board: Board): Int {
        val mountains = board.all { it == Terrain.MOUNTAIN }
        val connectedForests = board.connectedTerrains(Terrain.FOREST)
        return 3 * mountains.count { m1 ->
            mountains.filter { it != m1 }
                .any { m2 ->
                    val m1Adjacent = board.adjacent(m1)
                    val m2Adjacent = board.adjacent(m2)
                    connectedForests.any { m1Adjacent.any(it::contains) && m2Adjacent.any(it::contains) }
                }
        }
    }
}

object HugeCity35 : ScoreCard {
    override fun evaluate(board: Board): Int =
        board.connectedTerrains(Terrain.CITY)
            .filter { city -> city.all { board.adjacent(it).all { p -> board.terrainAt(p) != Terrain.MOUNTAIN } } }
            .maxBy { it.size }?.size ?: 0
}

object Fortress37 : ScoreCard {
    override fun evaluate(board: Board): Int =
        board.connectedTerrains(Terrain.CITY).let {
            when {
                it.isEmpty() || it.size == 1 -> 0
                else -> 2 *
                        (it.sortedByDescending { cluster -> cluster.size }.drop(1).firstOrNull()?.size ?: 0)
            }
        }
}

object Colony34 : ScoreCard {
    override fun evaluate(board: Board): Int =
        8 * board.connectedTerrains(Terrain.CITY).filter { it.size >= 6 }.count()
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
                    if (points.any { board.isOnBorder(it) }) {
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
        .count { p -> board.adjacent(p).all { board.terrainAt(it) != Terrain.EMPTY } }
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