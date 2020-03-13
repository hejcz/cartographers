package com.github.hejcz

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BasicStuffTest {
    private val defaultMap = """[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][R][ ][ ][ ][ ][ ]
[ ][R][ ][ ][ ][ ][ ][ ][M][R][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][R][M][ ][ ][ ][ ][ ][ ][R][ ]
[ ][ ][ ][ ][ ][R][ ][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]"""

    @Test
    fun `test default board`() {
        Assertions.assertEquals(Board.create().toString(), defaultMap)
    }

    @Test
    internal fun `apply shape to board`() {
        val game = (GameImplementation(listOf(TreeFortress14), emptySet(), emptyMap()) { cards -> cards} as Game)
            .join("julian")
            .start()
            .draw("julian", setOf(-9 to 6, -8 to 6, -8 to 7, -7 to 7, -6 to 7), Terrain.FOREST)
        Assertions.assertEquals(
            game.boardOf("julian").toString(),
            """[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][R][ ][ ][ ][ ][ ]
[ ][R][ ][ ][ ][ ][ ][ ][M][R][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][F][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][F][ ][ ][ ]
[ ][R][M][ ][ ][ ][F][F][ ][R][ ]
[ ][ ][ ][ ][ ][R][F][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]""",
        "shape was added to board")
    }

    @Test
    internal fun `apply shape with different terrain to board`() {
        val game = (GameImplementation(listOf(TreeFortress14), emptySet(), emptyMap()) { cards -> cards} as Game)
            .join("julian")
            .start()
            .draw("julian", setOf(-9 to 6, -8 to 6, -8 to 7, -7 to 7, -6 to 7), Terrain.CITY)
        Assertions.assertEquals(
            game.boardOf("julian").toString(),
            """[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][M][ ][R][ ][ ][ ][ ][ ]
[ ][R][ ][ ][ ][ ][ ][ ][M][R][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][M][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][C][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][C][ ][ ][ ]
[ ][R][M][ ][ ][ ][C][C][ ][R][ ]
[ ][ ][ ][ ][ ][R][C][M][ ][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]""",
            "shape was added to board")
    }

    @Test
    internal fun `terrain is validated`() {
        val game = (GameImplementation(listOf(TreeFortress14), emptySet(), emptyMap()) { cards -> cards} as Game)
            .join("julian")
            .start()
            .draw("julian", setOf(-9 to 6, -8 to 6, -8 to 7, -7 to 7, -6 to 7), Terrain.WATER)
        Assertions.assertEquals(
            game.boardOf("julian").toString(),
            defaultMap,
            "shape was not added to board cause tree fortress accepts city and forest only")
    }
}