package com.github.hejcz.cartographers

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

    @Test
    internal fun `biggest square works 5`() {
        val points = LostDemesne39.evaluate(
            Board.create(
                """
[C][C][C][C][C][C][C][C][C][C][C]
[C][C][C][M][C][C][C][C][C][C][C]
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
        )
        Assertions.assertEquals(11 * 3, points)
    }

    @Test
    internal fun `forest tower 1`() {
        val points = ForestTower28.evaluate(
            Board.create(
                """
[F][ ][F][M][ ][ ][ ][ ][ ][ ][ ]
[F][F][F][C][ ][ ][ ][ ][M][ ][ ]
[M][C][C][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(2 * 1, points)
    }

    @Test
    internal fun `forest tower 2`() {
        val points = ForestTower28.evaluate(
            Board.create(
                """
[F][F][F][F][ ][ ][ ][ ][ ][ ][ ]
[F][F][F][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][F][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(4 * 1, points)
    }

    @Test
    internal fun `huge city 1`() {
        val points = HugeCity35.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][C][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][C][C][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][C][C][C][ ][ ][ ]
[ ][ ][M][ ][ ][C][C][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(5 * 1, points)
    }

    @Test
    internal fun `huge city 2`() {
        val points = HugeCity35.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][C][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][C][C][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][C][C][C][C][ ][ ][ ]
[ ][ ][M][ ][C][C][C][ ][ ][ ][ ]
[ ][ ][ ][ ][C][C][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(9 * 1, points)
    }

    @Test
    internal fun `huge city 3 - cant be near mountain`() {
        val points = HugeCity35.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][C][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][C][C][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][C][C][C][C][ ][ ][ ]
[ ][ ][M][ ][C][C][C][ ][ ][ ][ ]
[ ][ ][ ][ ][C][C][C][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(3 * 1, points)
    }

    @Test
    internal fun `huge city 4 - cant be near mountain`() {
        val points = HugeCity35.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][C][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][C][C][ ][ ][ ]
[ ][ ][ ][ ][ ][M][C][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][C][C][C][C][ ][ ][ ]
[ ][ ][M][ ][C][C][C][ ][ ][ ][ ]
[ ][ ][ ][ ][C][C][C][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(0 * 1, points)
    }

    @Test
    internal fun `golden breadbasket 1`() {
        val points = GoldenBreadbasket32.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][W][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][P][W][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(3 * 1 + 2 * 1, points)
    }

    @Test
    internal fun `golden breadbasket 2`() {
        val points = GoldenBreadbasket32.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][W][ ][M][ ][P][ ][ ][ ][ ][ ]
[ ][P][W][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(3 * 2 + 2 * 1, points)
    }

    @Test
    internal fun `golden breadbasket 3`() {
        val points = GoldenBreadbasket32.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][W][ ][ ][ ][ ][ ]
[ ][W][ ][M][W][P][W][ ][ ][ ][ ]
[ ][P][W][ ][ ][W][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(3 * 2 + 6 * 1, points)
    }

    @Test
    internal fun `golden breadbasket 4`() {
        val points = GoldenBreadbasket32.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][W][ ][ ][ ][ ][ ]
[ ][W][ ][M][W][P][W][ ][ ][ ][ ]
[ ][P][W][ ][ ][W][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][W][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][W][P][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(3 * 2 + 6 * 1, points)
    }

    @Test
    internal fun `mountain woods 1`() {
        val points = MountainWoods29.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(0 * 3, points)
    }

    @Test
    internal fun `mountain woods 2`() {
        val points = MountainWoods29.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][F][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(2 * 3, points)
    }

    @Test
    internal fun `mountain woods 3`() {
        val points = MountainWoods29.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][F][M][ ][ ][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][F][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(3 * 3, points)
    }

    @Test
    internal fun `mountain woods 4`() {
        val points = MountainWoods29.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][F][M][ ][ ]
[ ][ ][ ][F][ ][ ][ ][F][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][F][ ][ ][ ]
[ ][ ][ ][F][F][M][ ][F][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][F][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][F][ ][ ][ ]
[ ][ ][M][F][F][F][ ][F][ ][ ][ ]
[ ][ ][ ][ ][ ][F][F][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][F][F][F][ ][ ]
"""
            )
        )
        Assertions.assertEquals(5 * 3, points)
    }

    @Test
    internal fun `mountain woods 5`() {
        val points = MountainWoods29.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][F][F][F][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][F][F][M][ ][ ]
[ ][ ][ ][F][ ][ ][ ][F][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][F][ ][ ][ ]
[ ][ ][ ][F][F][M][ ][F][ ][ ][ ]
[ ][ ][ ][F][ ][ ][ ][F][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][F][ ][ ][ ]
[ ][ ][M][F][F][F][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][F][F][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][F][F][F][ ][ ]
"""
            )
        )
        Assertions.assertEquals(4 * 3, points)
    }

    @Test
    internal fun `mountain woods 6`() {
        val points = MountainWoods29.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][P][P][P][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][P][P][M][ ][ ]
[ ][ ][ ][P][ ][ ][ ][P][ ][ ][ ]
[ ][ ][ ][P][ ][ ][ ][P][ ][ ][ ]
[ ][ ][ ][P][P][M][ ][P][ ][ ][ ]
[ ][ ][ ][P][ ][ ][ ][P][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][P][ ][ ][ ]
[ ][ ][M][P][P][P][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][P][P][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][P][P][P][ ][ ]
"""
            )
        )
        Assertions.assertEquals(0 * 3, points)
    }

    @Test
    internal fun `colony 1`() {
        val points = Colony34.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][C][C][C][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][C][C][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(0 * 6, points)
    }

    @Test
    internal fun `colony 2`() {
        val points = Colony34.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][C][C][C][ ][ ][ ][ ][ ][ ][ ]
[ ][C][C][C][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(1 * 6, points)
    }

    @Test
    internal fun `colony 3`() {
        val points = Colony34.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][M][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][C][C][C][C][ ][ ][ ][ ][ ][ ]
[ ][C][C][C][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(1 * 6, points)
    }

    @Test
    internal fun `colony 4`() {
        val points = Colony34.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][C][C][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][C][C][ ][ ][M][ ][ ]
[ ][ ][ ][ ][C][C][ ][ ][ ][ ][ ]
[ ][C][C][C][ ][ ][ ][ ][ ][ ][ ]
[ ][C][C][C][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(2 * 6, points)
    }

    @Test
    internal fun `colony 5`() {
        val points = Colony34.evaluate(
            Board.create(
                """
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][C][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][C][C][ ][ ][M][ ][ ]
[ ][ ][ ][ ][C][C][ ][ ][ ][ ][ ]
[ ][C][C][C][ ][ ][ ][ ][ ][ ][ ]
[ ][C][C][C][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][M][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
"""
            )
        )
        Assertions.assertEquals(1 * 6, points)
    }

}