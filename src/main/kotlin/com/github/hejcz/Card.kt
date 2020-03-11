package com.github.hejcz

interface Card {
    // positions are sorted from left to right and from top to bottom
    fun isValid(shape: Shape): Boolean

    fun points(): Int

    fun givesCoin(shape: Shape): Boolean
}

// Nadrzewna osada
object Card14 : Card {
    private val availableShapes =
        Shape.create(setOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 3 to 1))
            .createAllVariations()

    override fun isValid(shape: Shape) =
        shape.normalize().let { s -> availableShapes.any { it == s } }

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false
}