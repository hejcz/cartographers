package com.github.hejcz.cartographers

import java.util.stream.IntStream
import kotlin.math.max

class BasicBoard(private val board: Map<Point, Terrain>, private val ruins: Set<Point>) :
    Board {
    override fun draw(shape: Shape, terrain: Terrain): Board {
        val newBoard = board.toMutableMap()
        for (p in shape.toPoints()) {
            newBoard[p] = terrain
        }
        return BasicBoard(
            newBoard.toMap().withDefault { Terrain.EMPTY }, ruins)
    }

    override fun all(predicate: (Terrain) -> Boolean): Set<Point> =
        board.filterValues(predicate).keys

    override fun allEmpty(): Set<Point> = (0 downTo -10).flatMap { x -> (0..10).map {
        Point(x, it)
    } }
        .filter { !board.containsKey(it) }
        .toSet()

    private fun allPoints(predicate: (Terrain) -> Boolean): Set<Point> =
        board.filterValues(predicate).keys

    override fun biggestSquareLength(): Int {
        var globalBest = 0
        for (x in 0 downTo -10) {
            for (y in 0..10) {
                val localBest = (1..11).takeWhile { x - it >= -11 && y + it <= 11 && isSquareOfSize(
                    Point(x, y), it) }
                    .lastOrNull() ?: 0
                globalBest = max(globalBest, localBest)
            }
        }
        return globalBest
    }

    // assuming that there is square staring at x,y of size: size - 1
    private fun isSquareOfSize(point: Point, size: Int): Boolean = when (size) {
        1 -> terrainAt(point) !in setOf(
            Terrain.EMPTY,
            Terrain.OUTSIDE_THE_MAP
        )
        else -> {
            val offset = size - 1
            val row = point.x - offset
            val col = point.y + offset
            IntStream.range(0, size)
                .allMatch {
                    terrainAt(Point(row, point.y + it)) !in setOf(
                        Terrain.EMPTY,
                        Terrain.OUTSIDE_THE_MAP
                    )
                            && terrainAt(Point(point.x - it, col)) !in setOf(
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
        (0 downTo -10).count { x -> (0..(10 + x)).all { y -> terrainAt(
            Point(x - y,
                y)) != Terrain.EMPTY
        } }

    override fun connectedTerrains(terrain: Terrain): Set<Set<Point>> {
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
            .mapValues { (_, v) -> v.map { it.key }.toSet() }
            .values
            .toSet()
    }

    override fun adjacent(point: Point): Set<Point> = point.adjacent(-10, 0, 0, 10).toSet()

    override fun hasRuinsOn(point: Point): Boolean = point in ruins

    override fun noPlaceToDraw(shapes: Set<Shape>): Boolean = shapes.none { shape ->
        val maxX = shape.toPoints().maxBy { it.x }?.x ?: throw RuntimeException("Empty shape")
        val points = shape.toPoints().map { it.x - maxX to it.y }
        val minX = points.minBy { it.first }?.first ?: throw RuntimeException("Empty shape")
        val maxY = points.maxBy { it.second }?.second ?: throw RuntimeException("Empty shape")
        val width = maxY + 1
        val height = -minX + 1
        for (row in (0..(11 - height))) {
            for (col in (0..(11 - width))) {
                if (points.all { terrainAt(Point(it.first - row,
                        it.second + col)) == Terrain.EMPTY
                    }) {
                    return@none true
                }
            }
        }
        false
    }

    override fun anyRuins(predicate: (Point) -> Boolean): Boolean =
        ruins.any { predicate(it) }

    override fun isAnyPossibleContaining(point: Point, shapes: Set<Shape>): Boolean {
        for (s in shapes) {
            for (v in s.createAllVariations()) {
                for (version in v.allVersionsContaining(point)) {
                    val impossible = version.anyMatches { (x, y) -> terrainAt(
                        Point(x,
                            y)) != Terrain.EMPTY
                    }
                    if (!impossible) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun allPoints(): Set<BoardElement> =
        board.map { (p, terrain) -> BoardElement(p.x, p.y, terrain) }.toSet()

    override fun terrainAt(point: Point): Terrain = when {
        point.x > 0 || point.x < -10 || point.y < 0 || point.y > 10 -> Terrain.OUTSIDE_THE_MAP
        else -> board.getValue(point)
    }

    override fun toString(): String =
        (0 downTo -10).joinToString("-") { x ->
            (0..10).joinToString("") { y ->
                this.terrainAt(Point(x, y)).toString()
            }
        }

    override fun prettyPrint(): String =
        "\n" + (0 downTo -10).joinToString("\n") { x ->
            (0..10).joinToString("") { y ->
                this.terrainAt(Point(x, y)).toString()
            }
        } + "\n"

    override fun isAnyOutsideTheMapOrTaken(points: Set<Point>) = points.any {
        val terrain = terrainAt(it)
        terrain != Terrain.EMPTY
    }

    override fun canDrawShapeOnRuins(shapes: Set<Shape>): Boolean = anyRuins {
        terrainAt(it) == Terrain.EMPTY && isAnyPossibleContaining(it, shapes)
    }

    override fun isOnBorder(point: Point): Boolean =
        point.x == 0 || point.x == -10 || point.y == 0 || point.y == 10

    override fun ruins(): Set<Point> = ruins
}