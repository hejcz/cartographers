package com.github.hejcz.cartographers

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

    override fun direction(): Direction =
        Direction.COUNTERCLOCKWISE

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun number(): String = "01"

    override fun toString(): String = javaClass.simpleName
}

object BogeymanAssault02 : MonsterCard {
    private val availableShapes =
        Shape.create(
            """
            [ ]   [ ]
            [ ]   [ ]
            """
        ).createAllVariations()

    override fun direction(): Direction =
        Direction.CLOCKWISE

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun number(): String = "02"

    override fun toString(): String = javaClass.simpleName
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

    override fun direction(): Direction =
        Direction.COUNTERCLOCKWISE

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun number(): String = "04"

    override fun toString(): String = javaClass.simpleName
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

    override fun direction(): Direction =
        Direction.CLOCKWISE

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun number(): String = "03"

    override fun toString(): String = javaClass.simpleName
}

object FlayerIncursionPromoA01 : MonsterCard {
    private val availableShapes =
        Shape.create(
            """
            [ ]
            [ ][ ]
            """
        ).createAllVariations()

    override fun direction(): Direction =
        Direction.COUNTERCLOCKWISE

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun number(): String = "PromoA01"

    override fun toString(): String = javaClass.simpleName
}

object InsectoidInvasionPromoA02 : MonsterCard {
    private val availableShapes =
        Shape.create(
            """
               [ ]
            [ ][ ]
            [ ]
            """
        ).createAllVariations()

    override fun direction(): Direction =
        Direction.CLOCKWISE

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun number(): String = "PromoA02"

    override fun toString(): String = javaClass.simpleName
}

object RatmanStrikePromoA03 : MonsterCard {
    private val availableShapes =
        Shape.create(
            """
            [ ][ ][ ]
            """
        ).createAllVariations()

    override fun direction(): Direction =
        Direction.CLOCKWISE

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun number(): String = "PromoA03"

    override fun toString(): String = javaClass.simpleName
}

object OgreChargePromoA04 : MonsterCard {
    private val availableShapes =
        Shape.create(
            """
            [ ][ ]
            [ ][ ]
            """
        ).createAllVariations()

    override fun direction(): Direction =
        Direction.COUNTERCLOCKWISE

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun number(): String = "PromoA04"

    override fun toString(): String = javaClass.simpleName
}
