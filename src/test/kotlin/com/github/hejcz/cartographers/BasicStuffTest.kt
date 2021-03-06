package com.github.hejcz.cartographers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BasicStuffTest {
    private val defaultMap = """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""

    @Test
    internal fun `has ruins in expected places`() {
        val board = Board.create()
        Assertions.assertTrue(board.hasRuinsOn(Point(-1, 5)))
        Assertions.assertTrue(board.hasRuinsOn(Point(-2, 1)))
        Assertions.assertTrue(board.hasRuinsOn(Point(-2, 9)))
        Assertions.assertTrue(board.hasRuinsOn(Point(-8, 1)))
        Assertions.assertTrue(board.hasRuinsOn(Point(-8, 9)))
        Assertions.assertTrue(board.hasRuinsOn(Point(-9, 5)))
    }

    @Test
    internal fun `creating board from image works`() {
        val img = """
[C][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][C][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][C][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][C][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][C][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][C][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][C][ ][ ][ ]
[C][ ][M][ ][ ][ ][ ][ ][C][ ][ ]
[ ][C][ ][ ][ ][ ][ ][M][ ][C][ ]
[C][ ][C][ ][ ][ ][ ][ ][ ][ ][C]
"""
        Assertions.assertEquals(img, Board.create(img).prettyPrint())
    }

    @Test
    internal fun `still has ruins in place where some shape was written`() {
        val board = Board.create(
            """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][C][ ][M][W][W][ ][ ][ ][ ][ ]
[ ][C][ ][ ][W][W][ ][ ][M][C][ ]
[ ][C][ ][ ][ ][W][ ][ ][C][C][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][C][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][C][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][C][M][D][ ][D][ ][ ][ ][P][ ]
[ ][ ][ ][D][ ][D][ ][M][ ][ ][P]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
        )
        Assertions.assertTrue(board.hasRuinsOn(Point(-1, 5)))
        Assertions.assertTrue(board.hasRuinsOn(Point(-2, 1)))
        Assertions.assertTrue(board.hasRuinsOn(Point(-2, 9)))
        Assertions.assertTrue(board.hasRuinsOn(Point(-8, 1)))
        Assertions.assertTrue(board.hasRuinsOn(Point(-8, 9)))
        Assertions.assertTrue(board.hasRuinsOn(Point(-9, 5)))
    }

    @Test
    fun `test default board`() {
        Assertions.assertEquals(Board.create().prettyPrint(), defaultMap)
    }

    @Test
    internal fun `apply shape to board`() {
        val game = (GameImplementation(
            deck = listOf(TreeFortress14, BigRiver07),
            monstersDeck = emptyList()
        ) { cards -> cards } as Game)
            .join("julian")
            .start("julian")
            .draw("julian", setOf(Point(-9, 6), Point(-8, 6), Point(-8, 7), Point(-7, 7), Point(-6, 7)), Terrain.FOREST)
        Assertions.assertEquals(
            game.boardOf("julian").prettyPrint(),
            """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][F][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][F][ ][ ][ ]
[ ][ ][M][ ][ ][ ][F][F][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][F][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
""",
            "shape was added to board"
        )
    }

    @Test
    internal fun `apply shape with different terrain to board`() {
        val game = (GameImplementation(
            deck = listOf(TreeFortress14, BigRiver07),
            monstersDeck = emptyList()
        ) { cards -> cards } as Game)
            .join("julian")
            .start("julian")
            .draw("julian", setOf(Point(-9, 6), Point(-8, 6), Point(-8, 7), Point(-7, 7), Point(-6, 7)), Terrain.CITY)
        Assertions.assertEquals(
            game.boardOf("julian").prettyPrint(),
            """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][C][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][C][ ][ ][ ]
[ ][ ][M][ ][ ][ ][C][C][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][C][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
""",
            "shape was added to board"
        )
    }

    @Test
    internal fun `terrain is validated`() {
        val game = (GameImplementation(
            deck = listOf(TreeFortress14),
            monstersDeck = emptyList()
        ) { cards -> cards } as Game)
            .join("julian")
            .start("julian")
            .draw("julian", setOf(Point(-9, 6), Point(-8, 6), Point(-8, 7), Point(-7, 7), Point(-6, 7)), Terrain.WATER)
        Assertions.assertEquals(
            game.boardOf("julian").prettyPrint(),
            defaultMap,
            "shape was not added to board cause tree fortress accepts city and forest only"
        )
    }

    @Test
    internal fun `can't add shape on another shape`() {
        val game = (GameImplementation(
            deck = listOf(BigRiver07, ForgottenForest10, BigRiver07),
            monstersDeck = emptyList()
        ) { cards -> cards } as Game)
            .join("julian")
            .start("julian")
            .draw("julian", setOf(Point(-9, 6), Point(-8, 6), Point(-8, 7), Point(-7, 7), Point(-7, 8)), Terrain.WATER)
        val board1 = game.boardOf("julian")
        Assertions.assertEquals(
            board1.prettyPrint(),
            """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][W][W][ ][ ]
[ ][ ][M][ ][ ][ ][W][W][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][W][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
""",
            "shape was not added to board"
        )
        Assertions.assertEquals(
            board1.prettyPrint(),
            game.draw("julian", setOf(Point(-7, 7), Point(-6, 6)), Terrain.FOREST).boardOf("julian").prettyPrint(),
            "shape was placed on another shape"
        )
        Assertions.assertEquals(
            """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][F][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][F][W][W][ ][ ]
[ ][ ][M][ ][ ][ ][W][W][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][W][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
""",
            game.draw("julian", setOf(Point(-6, 5), Point(-7, 6)), Terrain.FOREST).boardOf("julian").prettyPrint(),
            "shape was not added to board"
        )
    }

    @Test
    internal fun `ruins flow`() {
        val game = (GameImplementation(
            deck = listOf(Ruins06, BigRiver07, ForgottenForest10, City09),
            monstersDeck = emptyList()
        ) { cards -> cards } as Game)
            .join("julian")
            .start("julian")
        Assertions.assertNotEquals(
            game.draw("julian", setOf(Point(-9, 6), Point(-8, 6), Point(-8, 7), Point(-7, 7)), Terrain.WATER).boardOf("julian").prettyPrint(),
            """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][W][W][ ][ ]
[ ][ ][M][ ][ ][ ][W][W][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][W][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
""",
            "shape was not added on ruins even though ruins were forced"
        )
        val game1 = game.draw(
            "julian",
            setOf(Point(-9, 5), Point(-9, 6), Point(-8, 6), Point(-8, 7), Point(-7, 7)),
            Terrain.WATER
        )
        Assertions.assertEquals(
            """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][W][ ][ ][ ]
[ ][ ][M][ ][ ][ ][W][W][ ][ ][ ]
[ ][ ][ ][ ][ ][W][W][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
""",
            game1.boardOf("julian").prettyPrint(),
            "shape was not added to board"
        )
        Assertions.assertEquals(
            """
[ ][F][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[F][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][W][ ][ ][ ]
[ ][ ][M][ ][ ][ ][W][W][ ][ ][ ]
[ ][ ][ ][ ][ ][W][W][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
""",
            game1.draw("julian", setOf(Point(-1, 0), Point(0, 1)), Terrain.FOREST).boardOf("julian").prettyPrint(),
            "next shape does not need to be on ruins"
        )
    }

    @Test
    internal fun `ruins is not enforced when none is left`() {
        // TODO
    }

    @Test
    internal fun `can place 1 on 1 on this board`() {
        val b = Board.create("""
            [P][P][P][F][F][F][C][C][C][C][C]
            [P][D][F][M][F][F][C][F][F][C][C]
            [P][ ][D][F][F][F][F][F][M][P][C]
            [D][F][D][D][F][ ][D][F][D][C][C]
            [D][F][D][P][P][ ][D][D][D][C][C]
            [ ][F][F][P][ ][M][C][C][C][C][C]
            [ ][ ][ ][P][ ][C][C][ ][C][C][C]
            [P][P][P][P][W][C][ ][C][C][C][ ]
            [P][P][M][F][W][C][P][W][W][W][W]
            [P][ ][ ][F][W][C][P][M][ ][ ][ ]
            [P][P][F][F][F][C][P][P][P][ ][ ]
        """.trimIndent())
        Assertions.assertTrue(b.noPlaceToDraw(Fends15.availableShapes()))
    }

    @Test
    internal fun `ruins when are available but shape does not match this point`() {
        val b = Board.create("""
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
            [ ][ ][ ][M][ ][P][ ][ ][ ][ ][ ]
            [ ][P][ ][ ][ ][ ][ ][ ][M][P][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][F][F][ ]
            [ ][P][M][ ][ ][ ][ ][ ][F][ ][ ]
            [ ][ ][ ][ ][ ][P][ ][M][F][F][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
            """.trimIndent())
        Assertions.assertFalse(b.isAnyPossibleContaining(Point(-8, 9), BigRiver07.availableShapes()))
    }

    @Test
    internal fun `ruins when are available but shape does not match this point 2`() {
        val b = Board.create("""
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
            [ ][ ][ ][M][ ][P][ ][ ][ ][ ][ ]
            [ ][P][ ][ ][ ][ ][ ][ ][M][P][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][F][F][ ]
            [ ][P][M][ ][ ][ ][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][P][ ][M][F][F][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
            """.trimIndent())
        Assertions.assertTrue(b.isAnyPossibleContaining(Point(-8, 9), BigRiver07.availableShapes()))
    }
}