package com.github.hejcz

interface Card {
    fun availableShapes(): Set<Shape>

    // positions are sorted from left to right and from top to bottom
    fun isValid(shape: Shape): Boolean =
        shape.normalize().let { s -> availableShapes().any { it == s } }

    fun points(): Int

    fun givesCoin(shape: Shape): Boolean
    fun isValid(shape: Terrain): Boolean
}

object TreeFortress14 : Card {
    private val availableShapes =
        Shape.create(
            """
                  [ ][ ]
            [ ][ ][ ]
            """.trimIndent()
        ).createAllVariations()

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(shape: Terrain): Boolean = shape == Terrain.CITY || shape == Terrain.FOREST

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false
}

object BigRiver7 : Card {
    private val availableShapes =
        Shape.create(setOf(0 to 0, -1 to 0, -2 to 0))
            .createAllVariations() +
        Shape.create(setOf(0 to 0, -1 to 0, -2 to 0))
            .createAllVariations()

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(shape: Terrain): Boolean = shape == Terrain.CITY || shape == Terrain.FOREST

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false
}