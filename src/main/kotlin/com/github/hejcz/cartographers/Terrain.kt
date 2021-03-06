package com.github.hejcz.cartographers

enum class Terrain(private val str: () -> String) {
    OUTSIDE_THE_MAP({ -> throw RuntimeException("cant print outside the map") }),
    EMPTY({ -> "[ ]" }),
    MOUNTAIN({ -> "[M]" }),
    FOREST({ -> "[F]" }),
    CITY({ -> "[C]" }),
    PLAINS({ -> "[P]" }),
    WATER({ -> "[W]" }),
    ABYSS({ -> "[A]" }),
    MONSTER({ -> "[D]" });

    override fun toString(): String {
        return str()
    }

    companion object {
        fun from(str: String) = when (str) {
            "[D]" -> MONSTER
            "[F]" -> FOREST
            "[C]" -> CITY
            "[P]" -> PLAINS
            "[W]" -> WATER
            "[M]" -> MOUNTAIN
            "[A]" -> ABYSS
            else -> throw RuntimeException("should not create terrain from $str")
        }
    }
}