package com.github.hejcz.cartographers

interface Card {
    fun availableShapes(): Set<Shape>

    // positions are sorted from left to right and from top to bottom
    fun isValid(shape: Shape): Boolean =
        shape.normalize().let { s -> availableShapes().any { it == s } }

    fun points(): Int

    fun givesCoin(shape: Shape): Boolean
    fun isValid(terrain: Terrain): Boolean
    fun number(): String
}

object TreeFortress14 : Card {
    override fun number(): String = "14"

    private val availableShapes =
        Shape.create(
            """
                  [ ][ ]
            [ ][ ][ ]
            """
        ).createAllVariations()

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.CITY || terrain == Terrain.FOREST

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false

    override fun toString(): String = javaClass.simpleName
}

object BigRiver07 : Card {
    override fun number(): String = "07"

    private val availableShapes =
        Shape.create(
            """
            [ ]
            [ ]
            [ ]
            """
        ).createAllVariations() +
                Shape.create(
                    """
                  [ ]
               [ ][ ]
            [ ][ ]
            """
                ).createAllVariations()

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.WATER

    override fun points(): Int = 1

    override fun givesCoin(shape: Shape): Boolean = shape.size() == 3

    override fun toString(): String = javaClass.simpleName
}

object ForgottenForest10 : Card {
    override fun number(): String = "10"

    private val availableShapes =
        Shape.create(
            """
            [ ]
               [ ]
            """
        ).createAllVariations() +
                Shape.create(
                    """
            [ ]
            [ ][ ]
               [ ]
            """
                ).createAllVariations()

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.FOREST

    override fun points(): Int = 1

    override fun givesCoin(shape: Shape): Boolean = shape.size() == 2

    override fun toString(): String = javaClass.simpleName
}

object Orchard13 : Card {
    override fun number(): String = "13"

    private val availableShapes =
        Shape.create(
            """
            [ ][ ][ ]
                  [ ]
            """
        ).createAllVariations()

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.FOREST || terrain == Terrain.PLAINS

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false

    override fun toString(): String = javaClass.simpleName
}

object City09 : Card {
    override fun number(): String = "09"

    private val availableShapes =
        Shape.create(
            """
            [ ]
            [ ][ ]
            """
        ).createAllVariations() +
                Shape.create(
                    """
            [ ][ ][ ]
            [ ][ ]
            """
                ).createAllVariations()

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.CITY

    override fun points(): Int = 1

    override fun givesCoin(shape: Shape): Boolean = shape.size() == 3

    override fun toString(): String = javaClass.simpleName
}

sealed class Ruins : Card {
    override fun availableShapes(): Set<Shape> = emptySet()

    override fun isValid(terrain: Terrain): Boolean = false

    override fun points(): Int = 0

    override fun givesCoin(shape: Shape): Boolean = false

    override fun toString(): String = javaClass.simpleName
}

object Ruins05 : Ruins() {
    override fun number(): String = "05"
}

object Ruins06 : Ruins() {
    override fun number(): String = "06"
}

object RuralStream11 : Card {
    override fun number(): String = "11"

    private val availableShapes =
        Shape.create(
            """
            [ ][ ][ ]
            [ ]
            [ ]
            """
        ).createAllVariations()

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.WATER || terrain == Terrain.PLAINS

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false

    override fun toString(): String = javaClass.simpleName
}

object Cracks17 : Card {
    override fun number(): String = "17"

    private val availableShapes =
        Shape.create(
            """
            [ ]
            """
        ).createAllVariations()

    private val matchingTerrains = setOf(
        Terrain.PLAINS,
        Terrain.WATER,
        Terrain.CITY,
        Terrain.FOREST,
        Terrain.MONSTER
    )

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains

    override fun points(): Int = 0

    override fun givesCoin(shape: Shape): Boolean = false

    override fun toString(): String = javaClass.simpleName
}

object Farm12 : Card {
    override fun number(): String = "12"

    private val availableShapes =
        Shape.create(
            """
            [ ]
            [ ][ ]
            [ ]
            """
        ).createAllVariations()

    private val matchingTerrains = setOf(
        Terrain.PLAINS,
        Terrain.CITY
    )

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false

    override fun toString(): String = javaClass.simpleName
}

object Fends15 : Card {
    override fun number(): String = "15"

    private val availableShapes =
        Shape.create(
            """
            [ ]
            [ ][ ][ ]
            [ ]
            """
        ).createAllVariations()

    private val matchingTerrains = setOf(
        Terrain.FOREST,
        Terrain.WATER
    )

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false

    override fun toString(): String = javaClass.simpleName
}

object Fields08 : Card {
    override fun number(): String = "08"

    private val availableShapes =
        Shape.create(
            """
            [ ]
            [ ]
            """
        ).createAllVariations() +
                Shape.create(
                    """
               [ ]
            [ ][ ][ ]
               [ ]
            """
                ).createAllVariations()

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.PLAINS

    override fun points(): Int = 1

    override fun givesCoin(shape: Shape): Boolean = shape.size() == 2

    override fun toString(): String = javaClass.simpleName
}

object FishermanVillage16 : Card {
    override fun number(): String = "16"

    private val availableShapes =
        Shape.create(
            """
            [ ][ ][ ][ ]
            """
        ).createAllVariations()

    private val matchingTerrains = setOf(
        Terrain.CITY,
        Terrain.WATER
    )

    override fun availableShapes(): Set<Shape> =
        availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false

    override fun toString(): String = javaClass.simpleName
}