package com.github.hejcz.cartographers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ProductionBugsTest {

    @Test
    internal fun `one cell fortress is not detected 24-03-2020`() {
        val points = Fortress37.evaluate(
            Board.create(
                """
[W][W][W][F][F][ ][ ][ ][ ][ ][ ]
[F][F][F][M][F][F][F][ ][ ][C][ ]
[F][C][F][F][ ][ ][ ][ ][M][C][C]
[F][F][ ][ ][ ][ ][ ][ ][ ][C][ ]
[ ][ ][ ][ ][W][W][W][ ][ ][ ][ ]
[ ][ ][ ][ ][W][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][W][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(1 * 2, points)
    }

    @Test
    internal fun `monsters on ruins impossible 25-03-2020 - actually it is possible to draw ruins here`() {
        val board = Board.create(
            """
[ ][C][ ][ ][ ][ ][ ][D][ ][D][ ]
[C][C][ ][M][ ][W][C][D][ ][D][ ]
[C][D][P][P][ ][ ][C][C][M][F][ ]
[C][D][D][C][ ][ ][ ][F][F][F][ ]
[ ][D][C][C][ ][ ][ ][ ][ ][F][ ]
[ ][W][W][W][F][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][F][F][F][ ][P][ ][ ]
[ ][W][ ][ ][F][P][ ][P][P][P][ ]
[ ][W][M][ ][ ][P][ ][ ][ ][ ][F]
[ ][W][W][W][ ][ ][ ][M][ ][W][F]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][F][F]
"""
        )

        Assertions.assertTrue(
            board.anyRuins {
                board.terrainAt(it) == Terrain.EMPTY
                        && board.isAnyPossibleContaining(it, GnollsInvasion04.availableShapes())
            }
        )
    }
}