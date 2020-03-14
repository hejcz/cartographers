package com.github.hejcz

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ScoreCardTest {
    @Test
    internal fun `counting diameters works`() {
        val points = TradingRoad40.evaluate(
            Board.create(
                """
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
            )
        )
        Assertions.assertEquals(3 * 3, points)
    }

    @Test
    internal fun `counting diameters works 2`() {
        val points = TradingRoad40.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[C][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[C][C][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][C][C][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][C][ ][ ][ ][ ][ ][ ][ ]
[C][ ][ ][C][C][ ][ ][M][ ][ ][ ]
[C][C][ ][ ][C][C][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(4 * 3, points)
    }

    @Test
    internal fun `diameters in different directions dont work`() {
        val points = TradingRoad40.evaluate(
            Board.create(
                """
[ ][C][ ][ ][ ][ ][ ][ ][ ][C][ ]
[C][ ][ ][M][ ][ ][ ][ ][ ][ ][C]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][C]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][C][ ]
"""
            )
        )
        Assertions.assertEquals(0 * 3, points)
    }

    @Test
    internal fun `diamatere with missing field`() {
        val points = TradingRoad40.evaluate(
            Board.create(
                """
[C][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][C][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][C][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][C][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][C][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][C][ ][ ][ ][ ]
[C][ ][ ][ ][ ][ ][ ][C][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][C][ ][ ]
[C][ ][C][ ][ ][ ][ ][M][ ][C][ ]
[ ][ ][ ][C][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(0 * 3, points)
    }

    @Test
    internal fun `borderlands handle columns`() {
        val points = Borderlands38.evaluate(
            Board.create(
                """
[ ][C][ ][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][ ][M][C][ ][ ][ ][ ][ ][ ]
[ ][C][ ][ ][C][ ][ ][ ][M][ ][ ]
[ ][C][ ][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][ ][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][ ][ ][C][M][ ][ ][ ][ ][ ]
[ ][C][ ][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][ ][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][M][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][ ][ ][C][ ][ ][M][ ][ ][ ]
[ ][C][ ][ ][C][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(2 * 6, points)
    }

    @Test
    internal fun `borderlands accept mountains`() {
        val points = Borderlands38.evaluate(
            Board.create(
                """
[ ][C][C][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][C][M][C][ ][ ][ ][ ][ ][ ]
[ ][C][C][ ][C][ ][ ][ ][M][ ][ ]
[ ][C][C][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][C][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][C][ ][C][M][ ][ ][ ][ ][ ]
[ ][C][C][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][C][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][M][ ][C][ ][ ][ ][ ][ ][ ]
[ ][C][C][ ][C][ ][ ][M][ ][ ][ ]
[ ][C][C][ ][C][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(3 * 6, points)
    }

    @Test
    internal fun `borderlands handle rows`() {
        val points = Borderlands38.evaluate(
            Board.create(
                """
[ ][C][ ][ ][ ][ ][ ][ ][ ][C][ ]
[ ][C][ ][M][ ][ ][ ][ ][ ][C][ ]
[ ][C][ ][ ][ ][ ][ ][ ][M][C][ ]
[ ][C][ ][ ][ ][ ][ ][ ][ ][C][ ]
[ ][C][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[C][C][C][C][C][M][C][C][C][C][C]
[ ][C][ ][ ][ ][ ][ ][ ][ ][C][ ]
[ ][C][ ][ ][ ][ ][ ][ ][ ][C][ ]
[ ][C][M][ ][ ][ ][ ][ ][ ][C][ ]
[ ][C][ ][ ][ ][ ][ ][M][ ][C][ ]
[ ][C][ ][ ][ ][ ][ ][ ][ ][C][ ]
"""
            )
        )
        Assertions.assertEquals(2 * 6, points)
    }

    @Test
    internal fun `hideouts work`() {
        val points = Hideouts41.evaluate(
            Board.create(
                """
[ ][ ][W][ ][W][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][P][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][P][ ][M][ ][ ]
[ ][ ][ ][W][ ][ ][ ][P][ ][ ][ ]
[ ][ ][W][ ][W][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][W][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(3 * 1, points)
    }

    @Test
    internal fun `hideouts work 2`() {
        val points = Hideouts41.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][C][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][C][C][C]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][C][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][C]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][C][ ]
"""
            )
        )
        Assertions.assertEquals(1 * 1, points)
    }

    @Test
    internal fun `biggest square works`() {
        val points = LostDemesne39.evaluate(
            Board.create(
                """
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
[C][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(1 * 3, points)
    }

    @Test
    internal fun `biggest square works 2`() {
        val points = LostDemesne39.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][C][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][C][C][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(2 * 3, points)
    }

    @Test
    internal fun `biggest square works 3`() {
        val points = LostDemesne39.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][C][C][ ][ ][ ][ ][ ][ ]
[ ][ ][C][C][C][ ][ ][M][ ][ ][ ]
[ ][ ][C][C][C][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(3 * 3, points)
    }

    @Test
    internal fun `biggest square works 4`() {
        val points = LostDemesne39.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][C][C][ ][ ][ ][ ][ ][ ]
[ ][ ][C][C][ ][ ][ ][M][ ][ ][ ]
[ ][ ][C][C][C][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(2 * 3, points)
    }
}