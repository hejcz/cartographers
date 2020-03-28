package com.github.hejcz.cartographers

class Board11x11(private val board: Map<Point, Terrain>, private val ruins: Set<Point>)
    : Board by RectangularBoard(board, ruins, 11, 11)