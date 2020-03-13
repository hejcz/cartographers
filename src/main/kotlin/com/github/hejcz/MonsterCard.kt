package com.github.hejcz

enum class Direction {
    CLOCKWISE, COUNTERCLOCKWISE
}

interface MonsterCard : Card {
    fun direction(): Direction

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.MONSTER
    override fun points(): Int = 0
    override fun givesCoin(shape: Shape): Boolean = false
}

object GoblinsAttack01 : MonsterCard {
    private val availableShapes =
        Shape.create(
            """
            [ ]
               [ ]
                  [ ]
            """
        ).createAllVariations()

    override fun direction(): Direction = Direction.COUNTERCLOCKWISE

    override fun availableShapes(): Set<Shape> = availableShapes
}

object BogeymanAssault02 : MonsterCard {
    private val availableShapes =
        Shape.create(
            """
            [ ]   [ ]
            [ ]   [ ]
            """
        ).createAllVariations()

    override fun direction(): Direction = Direction.CLOCKWISE

    override fun availableShapes(): Set<Shape> = availableShapes
}

object GnollsInvasion04 : MonsterCard {
    private val availableShapes =
        Shape.create(
            """
            [ ][ ]
            [ ]
            [ ][ ]
            """
        ).createAllVariations()

    override fun direction(): Direction = Direction.COUNTERCLOCKWISE

    override fun availableShapes(): Set<Shape> = availableShapes
}

object CoboldsCharge03 : MonsterCard {
    private val availableShapes =
        Shape.create(
            """
            [ ]
            [ ][ ]
            [ ]
            """
        ).createAllVariations()

    override fun direction(): Direction = Direction.CLOCKWISE

    override fun availableShapes(): Set<Shape> = availableShapes
}