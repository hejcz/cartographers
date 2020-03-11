package com.github.hejcz

import java.lang.RuntimeException
import java.util.*

enum class Terrain {
    EMPTY,
    MOUNTAIN,
    FOREST,
    RUINS
}

interface Board {
    fun draw(shape: Shape): Board
    fun terrainAt(x: Int, y: Int): Terrain
    fun iterate(fn: (x: Int, y: Int, terrain: Terrain) -> Unit)

    companion object {
        fun create(): Board = ArrayBoard(
            arrayOf(
                Array(11) { Terrain.EMPTY },
                Array(11) { column -> if (column == 3) Terrain.MOUNTAIN else Terrain.EMPTY },
                Array(11) { column -> if (column == 8) Terrain.MOUNTAIN else Terrain.EMPTY },
                Array(11) { Terrain.EMPTY },
                Array(11) { Terrain.EMPTY },
                Array(11) { column -> if (column == 5) Terrain.MOUNTAIN else Terrain.EMPTY },
                Array(11) { Terrain.EMPTY },
                Array(11) { Terrain.EMPTY },
                Array(11) { column -> if (column == 2) Terrain.MOUNTAIN else Terrain.EMPTY },
                Array(11) { column -> if (column == 7) Terrain.MOUNTAIN else Terrain.EMPTY },
                Array(11) { Terrain.EMPTY }
            )
        )
    }
}

class ArrayBoard(private val board: Array<Array<Terrain>>) : Board {
    override fun draw(shape: Shape): Board {
        for ((x, y) in shape.toXYPoints()) {
            board[x + 7][y + 7] = Terrain.FOREST
        }
        return this
    }

    override fun terrainAt(x: Int, y: Int): Terrain = board[x][y]

    override fun iterate(fn: (x: Int, y: Int, mountain: Terrain) -> Unit) {
        board.forEachIndexed { rid, col ->
            col.forEachIndexed { cid, terrain ->
                fn(rid, cid, terrain)
            }
        }
    }
}

interface Shape {
    fun createAllVariations(): Set<Shape>
    fun isOutOfBounds(): Boolean
    fun isAnyPartOn(board: Board, terrain: Terrain): Boolean
    fun toXYPoints(): Set<Pair<Int, Int>>
    fun normalize(): Shape

    companion object {
        fun create(points: Set<Pair<Int, Int>>): Shape = PointGroupShape(points)
    }
}

data class PointGroupShape(val points: Set<Pair<Int, Int>>) : Shape {
    override fun createAllVariations(): Set<Shape> =
        setOf(
            this,
            moveTopLeftToZeroZero(this.points.map { -it.first to it.second }),
            moveTopLeftToZeroZero(this.points.map { -it.first to -it.second }),
            moveTopLeftToZeroZero(this.points.map { it.first to -it.second }),
            moveTopLeftToZeroZero(this.points.map { it.second to -it.first }),
            moveTopLeftToZeroZero(this.points.map { -it.second to -it.first }),
            moveTopLeftToZeroZero(this.points.map { -it.second to it.first }),
            moveTopLeftToZeroZero(this.points.map { it.second to it.first })
        )

    override fun isOutOfBounds(): Boolean = points.any { (x, y) -> x < 0 || y > 10 }

    override fun isAnyPartOn(board: Board, terrain: Terrain): Boolean =
        points.any { (x, y) -> board.terrainAt(x, y) == Terrain.MOUNTAIN }

    override fun toXYPoints(): Set<Pair<Int, Int>> = points

    override fun normalize(): Shape = moveTopLeftToZeroZero(points)

    companion object {
            private fun moveTopLeftToZeroZero(positions: Collection<Pair<Int, Int>>): Shape {
                val (leftX, topY) =
                    positions.sortedWith(compareBy<Pair<Int, Int>> { it.first }.thenBy { it.second }).first()
                return PointGroupShape(positions.map { it.first - leftX to it.second - topY }.toSet())
            }
        }
}

data class RoundSummary(
    val quest1Points: Int,
    val quest2Points: Int,
    val coinsPoints: Int,
    val monstersPenalty: Int
) {
    fun sum(): Int = quest1Points + quest2Points + coinsPoints - monstersPenalty
}

class Player {
    val id: UUID = UUID.randomUUID()
    var board: Board = Board.create()
    var coins = 0
    var summaries = listOf<RoundSummary>()
}

data class Response(
    val error: String? = null,
    val events: Any? = null
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
        players.forEach {
            it.summaries = it.summaries + RoundSummary(
                quest1.evaluate(it.board), quest2.evaluate(it.board), it.coins, it.board.countMonsterPoints()
            )
        }
    }

    fun update(id: UUID, shape: Shape): Response {
        val player = players.find { it.id == id } ?: throw RuntimeException("No player with id $id")
        val currentCard = deck[currentCardIndex]
        if (!currentCard.isValid(shape)) {
            return Response(error = "invalid shape")
        }
        if (shape.isOutOfBounds()) {
            return Response(error = "shape outside the map")
        }
        if (shape.isAnyPartOn(player.board, Terrain.MOUNTAIN)) {
            return Response(error = "shape on mountain")
        }
        if (ruinsDrawn && !shape.isAnyPartOn(player.board, Terrain.RUINS)) {
            return Response(error = "shape must be on ruins")
        }
        player.board.draw(shape)
        if (currentCard.givesCoin(shape)) {
            player.coins++
        }
        if (players.size == playersDone) {
            if (season.pointsInRound <= pointsInRound) {
                if (season == Season.WINTER) {
                    return endGame()
                }
                cleanBeforeNextTurn()
                return Response(
                    events = mapOf(
                        "scores" to players.map { it.id to it.summaries.last().sum() }.toMap(),
                        "nextCard" to deck[currentCardIndex]
                    )
                )
            } else {
                cleanBeforeNextCard()
                return Response(
                    events = mapOf(
                        "nextCard" to deck[currentCardIndex]
                    )
                )
            }
        }

        return Response()
    }

    private fun endGame(): Response {
        val idToTotalScore =
            players.map { it.id to it.summaries.sumBy(RoundSummary::sum) }.toMap()
        val maxScore = idToTotalScore.values.max() ?: 0
        return Response(
            events = mapOf(
                "winners" to idToTotalScore.filterValues { it == maxScore }.keys,
                "scores" to idToTotalScore
            )
        )
    }
}

fun main() {
    val b = Board.create()
    printBoard(b)
}

fun printBoard(b: Board) {
    b.iterate {x, y, terrain ->
        if (y == 0 && x != 0) {
            println()
        }
        print(
            when (terrain) {
                Terrain.MOUNTAIN -> "[M]"
                Terrain.FOREST -> "[F]"
                Terrain.EMPTY -> "[ ]"
                Terrain.RUINS -> "[R]"
            }
        )
    }
    println()
}
