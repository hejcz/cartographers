package com.github.hejcz.cartographers

import java.util.stream.IntStream
import kotlin.math.abs
import kotlin.math.max

class RectangularBoard(private val board: Map<Point, Terrain>, private val ruins: Set<Point>,
        private val height: Int, private val width: Int) : Board {
    private val minX = 0
    private val maxX = -(height - 1)
    private val minY = 0
    private val maxY = width - 1

    override fun draw(shape: Shape, terrain: Terrain): Board {
        val newBoard = board.toMutableMap()
        for (p in shape.toPoints()) {
            newBoard[p] = terrain
        }
        return RectangularBoard(
            newBoard.toMap().withDefault { Terrain.EMPTY }, ruins, height, width)
    }

    override fun all(predicate: (Terrain) -> Boolean): Set<Point> =
        board.filterValues(predicate).keys

    override fun allEmpty(): Set<Point> = (minX downTo maxX)
        .flatMap { x -> (minY..maxY).map { Point(x, it) } }
        .filter { !board.containsKey(it) }
        .toSet()

    private fun allPoints(predicate: (Terrain) -> Boolean): Set<Point> =
        board.filterValues(predicate).keys

    override fun biggestSquareLength(): Int {
        var globalBest = 0
        val maxLength = max(abs(maxX) + 1, maxY + 1)

        for (x in minX downTo maxX) {
            for (y in minY..maxY) {
                val localBest = (1..maxLength)
                    .takeWhile { x - it >= (maxX - 1) && y + it <= (maxY + 1) && isSquareOfSize(Point(x, y), it) }
                    .lastOrNull() ?: 0
                globalBest = max(globalBest, localBest)
            }
        }

        return globalBest
    }

    // assuming that there is square staring at x,y of size: size - 1
    private fun isSquareOfSize(point: Point, size: Int): Boolean {
        val forbidden = setOf(Terrain.EMPTY, Terrain.OUTSIDE_THE_MAP)
        return when (size) {
            1 -> terrainAt(point) !in forbidden
            else -> {
                val offset = size - 1
                val row = point.x - offset
                val col = point.y + offset
                IntStream.range(0, size)
                    .allMatch {
                        terrainAt(Point(row, point.y + it)) !in forbidden
                                && terrainAt(Point(point.x - it, col)) !in forbidden
                    }
            }
        }
    }

    override fun countFullRowsAndColumns(): Int =
        board.entries.groupBy { it.key.x }.count { it.value.size == abs(maxX - 1) } +
                board.entries.groupBy { it.key.y }.count { it.value.size == maxY + 1 }

    override fun countLeftToBottomDiameters(): Int =
        (minX downTo maxX).count { x ->
            (minY..(maxY + x)).all { y -> terrainAt(Point(x - y, y)) != Terrain.EMPTY } }

    override fun connectedTerrains(terrain: Terrain): Set<Set<Point>> {
        val points = allPoints { it == terrain }
        var groupId = 0
        val pointToGroup = mutableMapOf(*(points.map { it to groupId++ }.toTypedArray()))
        for (p in points) {
            for (a in p.adjacent(maxX, minX, minY, maxY)) {
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

    override fun adjacent(point: Point): Set<Point> = point.adjacent(maxX, minX, minY, maxY).toSet()

    override fun hasRuinsOn(point: Point): Boolean = point in ruins

    override fun noPlaceToDraw(shapes: Set<Shape>): Boolean = shapes.none { shape ->
        val maxX = shape.toPoints().maxBy { it.x }?.x ?: throw RuntimeException("Empty shape")
        val points = shape.toPoints().map { it.x - maxX to it.y }
        val minX = points.minBy { it.first }?.first ?: throw RuntimeException("Empty shape")
        val maxY = points.maxBy { it.second }?.second ?: throw RuntimeException("Empty shape")
        val width = maxY + 1
        val height = -minX + 1
        for (row in (this.minX..((abs(this.maxX) + 1) - height))) {
            for (col in (this.minY..((this.maxY + 1) - width))) {
                if (points.all { terrainAt(Point(it.first - row, it.second + col)) == Terrain.EMPTY}) {
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
                    val impossible = version.anyMatches { (x, y) -> terrainAt(Point(x, y)) != Terrain.EMPTY }
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
        point.x > minX || point.x < maxX || point.y < minY || point.y > maxY -> Terrain.OUTSIDE_THE_MAP
        else -> board.getValue(point)
    }

    override fun toString(): String =
        (minX downTo maxX).joinToString("-") { x ->
            (minY..maxY).joinToString("") { y ->
                this.terrainAt(Point(x, y)).toString()
            }
        }

    override fun prettyPrint(): String =
        "\n" + (minX downTo maxX).joinToString("\n") { x ->
            (minY..maxY).joinToString("") { y ->
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
        point.x == minX || point.x == maxX || point.y == minY || point.y == maxY

    override fun ruins(): Set<Point> = ruins
}