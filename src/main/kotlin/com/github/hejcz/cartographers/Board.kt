package com.github.hejcz.cartographers

interface Board {
    fun draw(shape: Shape, terrain: Terrain): Board
    fun terrainAt(point: Point): Terrain
    fun all(predicate: (Terrain) -> Boolean): Set<Point>
    fun allEmpty(): Set<Point>
    fun biggestSquareLength(): Int
    fun countFullRowsAndColumns(): Int
    fun countLeftToBottomDiameters(): Int
    fun connectedTerrains(terrain: Terrain): Set<Set<Point>>
    fun adjacent(point: Point): Set<Point>
    fun hasRuinsOn(point: Point): Boolean
    fun noPlaceToDraw(shapes: Set<Shape>): Boolean
    fun anyRuins(predicate: (Point) -> Boolean): Boolean
    fun isAnyPossibleContaining(point: Point, shapes: Set<Shape>): Boolean
    fun allPoints(): Set<BoardElement>
    fun prettyPrint(): String
    fun isAnyOutsideTheMapOrTaken(points: Set<Point>): Boolean
    fun canDrawShapeOnRuins(shapes: Set<Shape>): Boolean
    fun isOnBorder(point: Point): Boolean
    fun ruins(): Set<Point>

    companion object {
        fun create(): Board =
            Board11x11(
                mapOf(
                    Point(-1, 3) to Terrain.MOUNTAIN,
                    Point(-2, 8) to Terrain.MOUNTAIN,
                    Point(-5, 5) to Terrain.MOUNTAIN,
                    Point(-8, 2) to Terrain.MOUNTAIN,
                    Point(-9, 7) to Terrain.MOUNTAIN
                ).withDefault { Terrain.EMPTY },
                basicBoardRuins
            )

        fun create(img: String): Board {
            return Board11x11(
                parseToBoard(img),
                basicBoardRuins
            )
        }

        fun createAdvanced(): Board =
            Board11x11(
                mapOf(
                    Point(-1, 8) to Terrain.MOUNTAIN,
                    Point(-2, 3) to Terrain.MOUNTAIN,
                    Point(-7, 5) to Terrain.MOUNTAIN,
                    Point(-8, 9) to Terrain.MOUNTAIN,
                    Point(-9, 2) to Terrain.MOUNTAIN,
                    Point(-3, 5) to Terrain.ABYSS,
                    Point(-4, 4) to Terrain.ABYSS,
                    Point(-4, 5) to Terrain.ABYSS,
                    Point(-5, 4) to Terrain.ABYSS,
                    Point(-5, 5) to Terrain.ABYSS,
                    Point(-5, 6) to Terrain.ABYSS,
                    Point(-6, 5) to Terrain.ABYSS
                ).withDefault { Terrain.EMPTY },
                advancedBoardRuins
            )

        fun createAdvanced(img: String): Board {
            return Board11x11(
                parseToBoard(img),
                advancedBoardRuins
            )
        }

        private fun parseToBoard(
                img: String): Map<Point, Terrain> {
            val regex = "\\[[^ ]\\]".toRegex()
            val board = img.trimIndent().lines()
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
            return board
        }

        private val basicBoardRuins = setOf(
            Point(-1, 5),
            Point(-2, 1),
            Point(-2, 9),
            Point(-8, 1),
            Point(-8, 9),
            Point(-9, 5)
        )

        private val advancedBoardRuins = setOf(
            Point(-1, 6),
            Point(-2, 2),
            Point(-4, 6),
            Point(-6, 1),
            Point(-7, 8),
            Point(-9, 3)
        )
    }
}

data class Point(val x: Int, val y: Int) {
    fun moveX(offset: Int) = Point(x + offset, y)
    fun moveY(offset: Int) = Point(x, y + offset)

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