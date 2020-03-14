package com.github.hejcz.cartographers

import java.util.stream.IntStream
import kotlin.math.max

interface Board {
    fun draw(shape: Shape, terrain: Terrain): Board
    fun terrainAt(x: Int, y: Int): Terrain
    fun all(predicate: (Terrain) -> Boolean): Set<Pair<Int, Int>>
    fun allEmpty(): Set<Pair<Int, Int>>
    fun biggestSquareLength(): Int
    fun countFullRowsAndColumns(): Int
    fun countLeftToBottomDiameters(): Int
    fun connectedTerrains(terrain: Terrain): Set<Set<Pair<Int, Int>>>
    fun adjacent(xy: Pair<Int, Int>): Set<Pair<Int, Int>>
    fun hasRuinsOn(x: Int, y: Int): Boolean

    companion object {
        fun create(): Board =
            MapBoard(
                mapOf(
                    Point(-1, 3) to Terrain.MOUNTAIN,
                    Point(-2, 8) to Terrain.MOUNTAIN,
                    Point(-5, 5) to Terrain.MOUNTAIN,
                    Point(-8, 2) to Terrain.MOUNTAIN,
                    Point(-9, 7) to Terrain.MOUNTAIN
                ).withDefault { Terrain.EMPTY }
            )

        fun create(img: String): Board {
            val regex = "\\[[^ ]\\]".toRegex()
            return MapBoard(
                img.trimIndent().lines()
                    .filter { it.isNotBlank() }
                    .mapIndexed { lineNumber, line ->
                        regex.findAll(line).map {
                            Point(
                                -lineNumber,
                                it.range.first / 3
                            ) to Terrain.from(it.value)
                        }.toSet()
                    }
                    .flatten()
                    .toMap()
                    .withDefault { Terrain.EMPTY }
            )
        }
    }
}

class MapBoard(private val board: Map<Point, Terrain>) :
    Board {
    override fun draw(shape: Shape, terrain: Terrain): Board {
        val newBoard = board.toMutableMap()
        for ((x, y) in shape.toXYPoints()) {
            newBoard[Point(x, y)] = terrain
        }
        return MapBoard(newBoard.toMap().withDefault { Terrain.EMPTY })
    }

    override fun terrainAt(x: Int, y: Int): Terrain = terrainAt(
        Point(
            x,
            y
        )
    )

    override fun all(predicate: (Terrain) -> Boolean): Set<Pair<Int, Int>> =
        board.filterValues(predicate).keys.map { it.x to it.y }.toSet()

    override fun allEmpty(): Set<Pair<Int, Int>> = (0 downTo -10).flatMap { x -> (0..10).map { x to it } }
        .filter { !board.containsKey(Point(it.first, it.second)) }
        .toSet()

    private fun allPoints(predicate: (Terrain) -> Boolean): Set<Point> =
        board.filterValues(predicate).keys

    override fun biggestSquareLength(): Int {
        var globalBest = 0
        for (x in 0 downTo -10) {
            for (y in 0..10) {
                val localBest = (1..11).takeWhile { x - it >= -11 && y + it <= 11 && isSquareOfSize(x, y, it) }
                    .lastOrNull() ?: 0
                globalBest = max(globalBest, localBest)
            }
        }
        return globalBest
    }

    // assuming that there is square staring at x,y of size: size - 1
    private fun isSquareOfSize(x: Int, y: Int, size: Int): Boolean = when (size) {
        1 -> terrainAt(x, y) !in setOf(
            Terrain.EMPTY,
            Terrain.OUTSIDE_THE_MAP
        )
        else -> {
            val offset = size - 1
            val row = x - offset
            val col = y + offset
            IntStream.range(0, size)
                .allMatch {
                    terrainAt(row, y + it) !in setOf(
                        Terrain.EMPTY,
                        Terrain.OUTSIDE_THE_MAP
                    )
                            && terrainAt(x - it, col) !in setOf(
                        Terrain.EMPTY,
                        Terrain.OUTSIDE_THE_MAP
                    )
                }
        }
    }

    override fun countFullRowsAndColumns(): Int =
        board.entries.groupBy { it.key.x }.count { it.value.size == 11 } +
                board.entries.groupBy { it.key.y }.count { it.value.size == 11 }

    override fun countLeftToBottomDiameters(): Int =
        (0 downTo -10).count { x -> (0..(10 + x)).all { y -> terrainAt(x - y, y) != Terrain.EMPTY } }

    override fun connectedTerrains(terrain: Terrain): Set<Set<Pair<Int, Int>>> {
        val points = allPoints { it == terrain }
        var groupId = 0
        val pointToGroup = mutableMapOf(*(points.map { it to groupId++ }.toTypedArray()))
        for (p in points) {
            for (a in p.adjacent(-10, 0, 0, 10)) {
                if (pointToGroup[a] != null) {
                    val groupToChange = pointToGroup[a]
                    val newGroup = pointToGroup.getValue(p)
                    for ((p2, g) in pointToGroup) {
                        if (g == groupToChange) {
                            pointToGroup[p2] = newGroup
                        }
                    }
                }
            }
        }
        return pointToGroup.entries.groupBy { it.value }
            .mapValues { (_, v) -> v.map { it.key.x to it.key.y }.toSet() }
            .values
            .toSet()
    }

    override fun adjacent(xy: Pair<Int, Int>) =
        Point(xy.first, xy.second).adjacent(-10, 0, 0, 10)
            .map { it.x to it.y }
            .toSet()

    override fun hasRuinsOn(x: Int, y: Int): Boolean =
        x == -1 && y == 5
                || x == -2 && y == 1
                || x == -2 && y == 9
                || x == -8 && y == 1
                || x == -8 && y == 9
                || x == -9 && y == 5

    private fun terrainAt(p: Point): Terrain = when {
        p.x > 0 || p.x < -10 || p.y < 0 || p.y > 10 -> Terrain.OUTSIDE_THE_MAP
        else -> board.getValue(p)
    }

    override fun toString(): String =
        "\n" + (0 downTo -10).joinToString("\n") { x ->
            (0..10).joinToString("") { y ->
                this.terrainAt(x, y).toString()
            }
        } + "\n"
}

data class Point(val x: Int, val y: Int) {
    private fun moveX(offset: Int) = Point(x + offset, y)
    private fun moveY(offset: Int) = Point(x, y + offset)

    fun adjacent(minX: Int, maxX: Int, minY: Int, maxY: Int) =
        listOf(
            moveX(1),
            moveX(-1),
            moveY(1),
            moveY(-1)
        )
            .filter { it.x in minX..maxX && it.y in minY..maxY }
            .toSet()
}