package com.github.hejcz

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
        Assertions.assertTrue(board.hasRuinsOn(-1, 5))
        Assertions.assertTrue(board.hasRuinsOn(-2, 1))
        Assertions.assertTrue(board.hasRuinsOn(-2, 9))
        Assertions.assertTrue(board.hasRuinsOn(-8, 1))
        Assertions.assertTrue(board.hasRuinsOn(-8, 9))
        Assertions.assertTrue(board.hasRuinsOn(-9, 5))
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
        Assertions.assertEquals(img, Board.create(img).toString())
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
        Assertions.assertTrue(board.hasRuinsOn(-1, 5))
        Assertions.assertTrue(board.hasRuinsOn(-2, 1))
        Assertions.assertTrue(board.hasRuinsOn(-2, 9))
        Assertions.assertTrue(board.hasRuinsOn(-8, 1))
        Assertions.assertTrue(board.hasRuinsOn(-8, 9))
        Assertions.assertTrue(board.hasRuinsOn(-9, 5))
    }

    @Test
    fun `test default board`() {
        Assertions.assertEquals(Board.create().toString(), defaultMap)
    }

    @Test
    internal fun `apply shape to board`() {
        val game = (GameImplementation(listOf(TreeFortress14), emptySet(), emptyMap()) { cards -> cards } as Game)
            .join("julian")
            .start()
            .draw("julian", setOf(-9 to 6, -8 to 6, -8 to 7, -7 to 7, -6 to 7), Terrain.FOREST)
        Assertions.assertEquals(
            game.boardOf("julian").toString(),
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
        val game = (GameImplementation(listOf(TreeFortress14), emptySet(), emptyMap()) { cards -> cards } as Game)
            .join("julian")
            .start()
            .draw("julian", setOf(-9 to 6, -8 to 6, -8 to 7, -7 to 7, -6 to 7), Terrain.CITY)
        Assertions.assertEquals(
            game.boardOf("julian").toString(),
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
        val game = (GameImplementation(listOf(TreeFortress14), emptySet(), emptyMap()) { cards -> cards } as Game)
            .join("julian")
            .start()
            .draw("julian", setOf(-9 to 6, -8 to 6, -8 to 7, -7 to 7, -6 to 7), Terrain.WATER)
        Assertions.assertEquals(
            game.boardOf("julian").toString(),
            defaultMap,
            "shape was not added to board cause tree fortress accepts city and forest only"
        )
    }

    @Test
    internal fun `can't add shape on another shape`() {
        val game = (GameImplementation(
            listOf(BigRiver07, ForgottenForest10, Ruins),
            emptySet(),
            emptyMap()
        ) { cards -> cards } as Game)
            .join("julian")
            .start()
            .draw("julian", setOf(-9 to 6, -8 to 6, -8 to 7, -7 to 7, -7 to 8), Terrain.WATER)
        val board1 = game.boardOf("julian")
        Assertions.assertEquals(
            board1.toString(),
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
            board1.toString(),
            game.draw("julian", setOf(-7 to 7, -6 to 6), Terrain.FOREST).boardOf("julian").toString(),
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
            game.draw("julian", setOf(-6 to 5, -7 to 6), Terrain.FOREST).boardOf("julian").toString(),
            "shape was not added to board"
        )
    }
}