package com.github.hejcz

interface Card {
    fun availableShapes(): Set<Shape>

    // positions are sorted from left to right and from top to bottom
    fun isValid(shape: Shape): Boolean =
        shape.normalize().let { s -> availableShapes().any { it == s } }

    fun points(): Int

    fun givesCoin(shape: Shape): Boolean
    fun isValid(terrain: Terrain): Boolean
}

object TreeFortress14 : Card {
    private val availableShapes =
        Shape.create(
            """
                  [ ][ ]
            [ ][ ][ ]
            """
        ).createAllVariations()

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.CITY || terrain == Terrain.FOREST

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false
}

object BigRiver7 : Card {
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

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.WATER

    override fun points(): Int = 1

    override fun givesCoin(shape: Shape): Boolean = shape.size() == 3
}

object ForgottenForest10 : Card {
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

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.FOREST

    override fun points(): Int = 1

    override fun givesCoin(shape: Shape): Boolean = shape.size() == 2
}

object Orchard13 : Card {
    private val availableShapes =
        Shape.create(
            """
            [ ][ ][ ]
                  [ ]
            """
        ).createAllVariations()

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.FOREST || terrain == Terrain.PLAINS

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false
}

object City9 : Card {
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

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.CITY

    override fun points(): Int = 1

    override fun givesCoin(shape: Shape): Boolean = shape.size() == 3
}

object Ruins : Card {

    override fun availableShapes(): Set<Shape> = emptySet()

    override fun isValid(terrain: Terrain): Boolean = false

    override fun points(): Int = 0

    override fun givesCoin(shape: Shape): Boolean = false
}

object RuralStream11 : Card {
    private val availableShapes =
        Shape.create(
            """
            [ ][ ][ ]
            [ ]
            [ ]
            """
        ).createAllVariations()

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.WATER || terrain == Terrain.PLAINS

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false
}

object Cracks17 : Card {
    private val availableShapes =
        Shape.create(
            """
            [ ]
            """
        ).createAllVariations()

    private val matchingTerrains = setOf(Terrain.PLAINS, Terrain.WATER, Terrain.CITY, Terrain.FOREST, Terrain.MONSTER)

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains

    override fun points(): Int = 0

    override fun givesCoin(shape: Shape): Boolean = false
}

object Farm12 : Card {
    private val availableShapes =
        Shape.create(
            """
            [ ]
            [ ][ ]
            [ ]
            """
        ).createAllVariations()

    private val matchingTerrains = setOf(Terrain.PLAINS, Terrain.CITY)

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false
}

object Fends15 : Card {
    private val availableShapes =
        Shape.create(
            """
            [ ]
            [ ][ ][ ]
            [ ]
            """
        ).createAllVariations()

    private val matchingTerrains = setOf(Terrain.FOREST, Terrain.WATER)

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false
}

object Fields08 : Card {
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

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain == Terrain.PLAINS

    override fun points(): Int = 1

    override fun givesCoin(shape: Shape): Boolean = shape.size() == 2
}

object FishermanVillage16 : Card {
    private val availableShapes =
        Shape.create(
            """
            [ ][ ][ ][ ]
            """
        ).createAllVariations()

    private val matchingTerrains = setOf(Terrain.CITY, Terrain.WATER)

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains

    override fun points(): Int = 2

    override fun givesCoin(shape: Shape): Boolean = false
}