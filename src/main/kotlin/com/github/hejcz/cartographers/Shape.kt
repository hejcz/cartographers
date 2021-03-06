package com.github.hejcz.cartographers

interface Shape {
    fun createAllVariations(): Set<Shape>
    fun anyMatches(predicate: (Point) -> Boolean): Boolean
    fun toPoints(): Set<Point>
    fun normalize(): Shape
    fun size(): Int
    fun allVersionsContaining(point: Point): Sequence<Shape>
    fun isEmpty(): Boolean

    companion object {
        fun create(points: Set<Point>): Shape =
            PointGroupShape(points)

        fun create(img: String): Shape =
            create(
                img.trimIndent()
                    .lines()
                    .dropWhile { it.isBlank() }
                    .dropLastWhile { it.isBlank() }
                    .asSequence()
                    .map { it.trimEnd() }
                    .mapIndexed { rowIdx, it ->
                        it.mapIndexedNotNull { colIdx, c -> if (c == '[') Point(-rowIdx, colIdx / 3) else null }
                    }
                    .flatten()
                    .toSet()
            ).normalize()
    }
}

object NoShape : Shape {
    override fun createAllVariations(): Set<Shape> = emptySet()

    override fun anyMatches(predicate: (Point) -> Boolean): Boolean = false

    override fun toPoints(): Set<Point> = emptySet()

    override fun normalize(): Shape = NoShape

    override fun size(): Int = 0

    override fun allVersionsContaining(point: Point): Sequence<Shape> = emptySequence()

    override fun isEmpty(): Boolean = true
}

data class PointGroupShape(
    private val points: Set<Point>
) : Shape {

    override fun createAllVariations(): Set<Shape> =
        setOf(
            this,
            moveTopLeftToZeroZero(points.map { Point(-it.x, it.y) }),
            moveTopLeftToZeroZero(points.map { Point(-it.x, -it.y) }),
            moveTopLeftToZeroZero(points.map { Point(it.x, -it.y) }),
            moveTopLeftToZeroZero(points.map { Point(it.y, -it.x) }),
            moveTopLeftToZeroZero(points.map { Point(-it.y, -it.x) }),
            moveTopLeftToZeroZero(points.map { Point(-it.y, it.x) }),
            moveTopLeftToZeroZero(points.map { Point(it.y, it.x) })
        )

    override fun anyMatches(predicate: (Point) -> Boolean): Boolean =
        points.any(predicate)

    override fun toPoints(): Set<Point> = points

    override fun normalize(): Shape = moveTopLeftToZeroZero(points)

    override fun size(): Int = points.size

    override fun allVersionsContaining(point: Point): Sequence<Shape> =
        points.asSequence().map { (x, y) ->
            val xShift = point.x - x
            val yShift = point.y - y
            PointGroupShape(points.map { it.moveX(xShift).moveY(yShift) }.toSet())
        }

    override fun isEmpty(): Boolean = points.isEmpty()

    companion object {
        private fun moveTopLeftToZeroZero(positions: Collection<Point>): Shape {
            val (leftX, topY) =
                positions.sortedWith(compareBy<Point> { it.y }.thenByDescending { it.x }).first()
            return PointGroupShape(positions.map { it.moveX(-leftX).moveY(-topY) }.toSet())
        }

    }
}