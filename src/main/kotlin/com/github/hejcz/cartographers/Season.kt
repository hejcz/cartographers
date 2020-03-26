package com.github.hejcz.cartographers

enum class Season(val pointsInRound: Int) {
    SPRING(8),
    SUMMER(8),
    AUTUMN(7),
    WINTER(6);

    companion object {
        fun byIndex(index: Int): Season = when (index) {
            0 -> SPRING
            1 -> SUMMER
            2 -> AUTUMN
            3 -> WINTER
            else -> throw RuntimeException("Illegal index for season $index")
        }
    }
}

fun Season.next(): Season {
    return when (this) {
        Season.SPRING -> Season.SUMMER
        Season.SUMMER -> Season.AUTUMN
        Season.AUTUMN -> Season.WINTER
        Season.WINTER -> Season.SPRING
    }
}