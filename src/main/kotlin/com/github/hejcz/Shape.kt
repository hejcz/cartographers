package com.github.hejcz

interface Shape {
    fun createAllVariations(): Set<Shape>
    fun anyMatches(predicate: (Pair<Int, Int>) -> Boolean): Boolean
    fun toXYPoints(): Set<Pair<Int, Int>>
    fun normalize(): Shape
    fun size(): Int

    companion object {
        fun create(points: Set<Pair<Int, Int>>): Shape =
            PointGroupShape(points)

        fun create(img: String): Shape =
            create(
                img.trimIndent()
                    .lines()
                    .asSequence()
                    .filter { it.isNotBlank() }
                    .map { it.trimEnd() }
                    .mapIndexed { rowIdx, it ->
                        it.mapIndexedNotNull { colIdx, c -> if (c == '[') (-rowIdx to colIdx / 3) else null }
                    }
                    .flatten()
                    .toSet()
            ).normalize()
    }
}

data class PointGroupShape(
    private val points: Set<Pair<Int, Int>>
) : Shape {

    override fun createAllVariations(): Set<Shape> =
        setOf(
            this,
            moveTopLeftToZeroZero(points.map { -it.first to it.second }),
            moveTopLeftToZeroZero(points.map { -it.first to -it.second }),
            moveTopLeftToZeroZero(points.map { it.first to -it.second }),
            moveTopLeftToZeroZero(points.map { it.second to -it.first }),
            moveTopLeftToZeroZero(points.map { -it.second to -it.first }),
            moveTopLeftToZeroZero(points.map { -it.second to it.first }),
            moveTopLeftToZeroZero(points.map { it.second to it.first })
        )

    override fun anyMatches(predicate: (Pair<Int, Int>) -> Boolean): Boolean =
        points.any(predicate)

    override fun toXYPoints(): Set<Pair<Int, Int>> = points

    override fun normalize(): Shape = moveTopLeftToZeroZero(points)

    override fun size(): Int = points.size

    companion object {
        private fun moveTopLeftToZeroZero(positions: Collection<Pair<Int, Int>>): Shape {
            val (leftX, topY) =
                positions.sortedWith(compareBy<Pair<Int, Int>> { it.second }.thenByDescending { it.first }).first()
            return PointGroupShape(positions.map { it.first - leftX to it.second - topY }.toSet())
        }

        fun create(points: Set<Pair<Int, Int>>) = moveTopLeftToZeroZero(points)
    }
}