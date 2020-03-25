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
}