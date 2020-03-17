package com.github.hejcz.cartographers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SinglePointWhenShapeCantBeDrawnTest {

    @Test
    internal fun `success when shape does not match`() {
        val board = Board.create(
            """
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][M][C][C][C][C][C][C][ ]
[C][C][C][C][C][C][C][C][M][C][ ]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][M][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][M][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][M][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
"""
        )
        Assertions.assertTrue(board.noPlaceToDraw(BigRiver07.availableShapes()))
    }

    @Test
    internal fun `success when shape does not match 2`() {
        val board = Board.create(
            """
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][M][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][M][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][M][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][M][C][C][C][C][C][ ][C][C]
[C][C][C][C][C][C][C][M][ ][ ][C]
[C][C][C][C][C][C][C][C][C][ ][C]
"""
        )
        Assertions.assertTrue(board.noPlaceToDraw(BigRiver07.availableShapes()))
    }

    @Test
    internal fun `error when shape matches`() {
        val board = Board.create(
            """
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][M][C][C][C][C][ ][ ][ ]
[C][C][C][C][C][C][C][C][M][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][M][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][M][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][M][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
"""
        )
        Assertions.assertFalse(board.noPlaceToDraw(BigRiver07.availableShapes()))
    }

    @Test
    internal fun `error when shape matches 2`() {
        val board = Board.create(
            """
[C][C][C][C][C][C][C][C][ ][C][C]
[C][C][C][M][C][C][C][C][ ][ ][C]
[C][C][C][C][C][C][C][C][M][ ][ ]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][M][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][M][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][M][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
"""
        )
        Assertions.assertFalse(board.noPlaceToDraw(BigRiver07.availableShapes()))
    }

    @Test
    internal fun `error when shape matches 3`() {
        val board = Board.create(
            """
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][M][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][M][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][M][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][M][C][C][C][C][C][ ][C][C]
[C][C][C][C][C][C][C][M][ ][ ][C]
[C][C][C][C][C][C][C][C][C][ ][ ]
"""
        )
        Assertions.assertFalse(board.noPlaceToDraw(BigRiver07.availableShapes()))
    }
}