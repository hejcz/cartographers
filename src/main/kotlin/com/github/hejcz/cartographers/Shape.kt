package com.github.hejcz.cartographers

interface Shape {
    fun createAllVariations(): Set<Shape>
    fun anyMatches(predicate: (Point) -> Boolean): Boolean
    fun toPoints(): Set<Point>
    fun normalize(): Shape
    fun size(): Int
    fun allVersionsContaining(point: Point): Sequence<Shape>

    companion object {
        fun create(points: Set<Point>): Shape =
            PointGroupShape(points)

        fun create(img: String): Shape =
            create(
                img.trimIndent()
                    .lines()
                    .asSequence()
                    .filter { it.isNotBlank() }
                    .map { it.trimEnd() }
                    .mapIndexed { rowIdx, it ->
                        it.mapIndexedNotNull { colIdx, c -> if (c == '[') Point(-rowIdx, colIdx / 3) else null }
                    }
                    .flatten()
                    .toSet()
            ).normalize()
    }
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
            PointGroupShape(points.map { (x, y) -> Point(x + xShift, y + yShift) }.toSet())
        }

    companion object {
        private fun moveTopLeftToZeroZero(positions: Collection<Point>): Shape {
            val (leftX, topY) =
                positions.sortedWith(compareBy<Point> { it.y }.thenByDescending { it.x }).first()
            return PointGroupShape(positions.map { (x, y) -> Point(x - leftX, y - topY) }.toSet())
        }

    }
}