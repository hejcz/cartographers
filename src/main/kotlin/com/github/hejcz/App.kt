package com.github.hejcz

import java.lang.RuntimeException
import java.util.*

enum class Terrain {
    EMPTY,
    MOUNTAIN,
    FOREST,
    RUINS
}

typealias Board = Array<Array<Terrain>>
typealias Shape = Set<Pair<Int, Int>>

class BoardFactory {
    companion object {
        fun create(): Board = arrayOf(
            Array(11) { _ -> Terrain.EMPTY },
            Array(11) { column -> if (column == 3) Terrain.MOUNTAIN else Terrain.EMPTY },
            Array(11) { column -> if (column == 8) Terrain.MOUNTAIN else Terrain.EMPTY },
            Array(11) { _ -> Terrain.EMPTY },
            Array(11) { _ -> Terrain.EMPTY },
            Array(11) { column -> if (column == 5) Terrain.MOUNTAIN else Terrain.EMPTY },
            Array(11) { _ -> Terrain.EMPTY },
            Array(11) { _ -> Terrain.EMPTY },
            Array(11) { column -> if (column == 2) Terrain.MOUNTAIN else Terrain.EMPTY },
            Array(11) { column -> if (column == 7) Terrain.MOUNTAIN else Terrain.EMPTY },
            Array(11) { _ -> Terrain.EMPTY }
        )

        fun update(board: Board, shape: Shape) {
            for ((x, y) in shape) {
                board[x + 7][y + 7] = Terrain.FOREST
            }
        }
    }
}

interface ScoreCard {
    fun evaluate(board: Board): Int
}

interface Card {
    // positions are sorted from left to right and from top to bottom
    fun isValid(positions: Shape): Boolean

    fun points(): Int
}

// Nadrzewna osada
object Card14 : Card {
    private val availableShapes =
        ShapeFactory.makeVariationsOf(setOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 3 to 1))

    override fun isValid(positions: Shape) =
        ShapeFactory.moveTopLeftToZeroZero(positions.toList()) in availableShapes

    override fun points(): Int = 2
}

class ShapeFactory {
    companion object {
        fun makeVariationsOf(positions: Shape): Set<Shape> =
            setOf(
                positions,
                moveTopLeftToZeroZero(positions.map { -it.first to it.second }),
                moveTopLeftToZeroZero(positions.map { -it.first to -it.second }),
                moveTopLeftToZeroZero(positions.map { it.first to -it.second }),
                moveTopLeftToZeroZero(positions.map { it.second to -it.first }),
                moveTopLeftToZeroZero(positions.map { -it.second to -it.first }),
                moveTopLeftToZeroZero(positions.map { -it.second to it.first }),
                moveTopLeftToZeroZero(positions.map { it.second to it.first })
            )

        fun moveTopLeftToZeroZero(positions: List<Pair<Int, Int>>): Set<Pair<Int, Int>> {
            val (leftX, topY) =
                positions.sortedWith(compareBy<Pair<Int, Int>> { it.first }.thenBy { it.second }).first()
            return positions.map { it.first - leftX to it.second - topY }.toSet()
        }
    }
}

data class RoundSummary(
    val quest1Points: Int,
    val quest2Points: Int,
    val coinsPoints: Int,
    val monstersPenalty: Int
)

class Player {
    val id: UUID = UUID.randomUUID()
    var board: Board = BoardFactory.create()
    var coins = 0
    var summaries = listOf<RoundSummary>()
}

data class Response(
    val error: String? = null
)

enum class Season(val pointsInRound: Int) {
    SPRING(8),
    SUMMER(8),
    AUTUMN(7),
    WINTER(6)
}

fun Season.next(): Season {
    return when (this) {
        Season.SPRING -> Season.SUMMER
        Season.SUMMER -> Season.AUTUMN
        Season.AUTUMN -> Season.WINTER
        Season.WINTER -> Season.SPRING
    }
}

fun Board.countMonsterPoints(): Int = 0

class Game {
    var season: Season = Season.SPRING
    var deck: List<Card> = listOf(Card14)
    var monstersDeck: Set<Card> = setOf()
    var currentCardIndex: Int = 0
    var pointsInRound: Int = 0
    var players: List<Player> = emptyList()
    var playersDone: Int = 0
    var ruinsDrawn: Boolean = false
    var enemyDrawn: Boolean = false
    var scoreCards: Map<Season, ScoreCard> = emptyMap()

    private fun cleanBeforeNextTurn() {
        val monsterCard = monstersDeck.random()
        monstersDeck = monstersDeck - monsterCard
        deck = (deck + monsterCard).shuffled()
        currentCardIndex = 0
        pointsInRound = 0
        playersDone = 0
        ruinsDrawn = false
        enemyDrawn = false
        season = season.next()
    }

    private fun cleanBeforeNextCard() {
        currentCardIndex += 1
        pointsInRound += deck[currentCardIndex].points()
        playersDone = 0
        ruinsDrawn = false
        enemyDrawn = false
        val quest1 = scoreCards.getValue(season)
        val quest2 = scoreCards.getValue(season.next())
        players.forEach { it.summaries = it.summaries + RoundSummary(
            quest1.evaluate(it.board), quest2.evaluate(it.board), it.coins, it.board.countMonsterPoints()) }
    }

    private fun endGame() {
    }

    fun update(id: UUID, shape: Shape): Response {
        val player = players.find { it.id == id } ?: throw RuntimeException("No player with id $id")
        val card = deck[currentCardIndex]
        if (!card.isValid(shape)) {
            return Response(error = "invalid shape")
        }
        if (shape.any { (x, y) -> x < 0 || y > 10 }) {
            return Response(error = "shape outside the map")
        }
        if (shape.any { (x, y) -> player.board[x][y] == Terrain.MOUNTAIN }) {
            return Response(error = "shape on mountain")
        }
        if (ruinsDrawn and shape.none { (x, y) -> player.board[x][y] == Terrain.RUINS }) {
            return Response(error = "shape must be on ruins")
        }
        BoardFactory.update(player.board, shape)

        if (players.size == playersDone) {
            if (season.pointsInRound <= pointsInRound) {
                if (season == Season.WINTER) {
                    endGame()
                }
                cleanBeforeNextTurn()
            } else {
                cleanBeforeNextCard()
            }
        }

        return Response()
    }
}

fun main() {
    val b = BoardFactory.create()

    for (shape in ShapeFactory.makeVariationsOf(setOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 3 to 1))) {
        println(Card14.isValid(shape.map { it.first + 7 to it.second * 2 }.toSet()))
    }
}

fun printBoard(b: Board) {
    for (row in b) {
        for (cell in row) {
            print(
                when (cell) {
                    Terrain.MOUNTAIN -> "[M]"
                    Terrain.FOREST -> "[F]"
                    Terrain.EMPTY -> "[ ]"
                    Terrain.RUINS -> "[R]"
                }
            )
        }
        println()
    }
    println()
}
