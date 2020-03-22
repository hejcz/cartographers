package com.github.hejcz.cartographers.custom

import com.github.hejcz.cartographers.Card
import com.github.hejcz.cartographers.Shape
import com.github.hejcz.cartographers.Terrain

object Barricades101 : Card {
    override fun number(): String = "101"

    private val availableShapes =
        Shape.create(
            """
            [ ]  [ ]
            [ ]  [ ]
            [ ]  [ ]
            [ ]  [ ]
            """
        ).createAllVariations()

    private val matchingTerrains = setOf(
        Terrain.PLAINS,
        Terrain.CITY
    )

    override fun availableShapes(): Set<Shape> = availableShapes

    override fun isValid(terrain: Terrain): Boolean = terrain in matchingTerrains

    override fun points(): Int = 3

    override fun givesCoin(shape: Shape): Boolean = false
}
