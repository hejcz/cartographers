package com.github.hejcz

import java.util.stream.IntStream
import kotlin.math.max

enum class Terrain(private val str: () -> String) {
    OUTSIDE_THE_MAP({ -> throw RuntimeException("cant print outside the map") }),
    EMPTY({ -> "[ ]" }),
    MOUNTAIN({ -> "[M]" }),
    FOREST({ -> "[F]" }),
    CITY({ -> "[C]" }),
    RUINS({ -> "[R]" }),
    PLAINS({ -> "[P]" }),
    WATER({ -> "[W]" }),
    MONSTER({ -> "[D]" });

    override fun toString(): String {
        return str()
    }
}

interface Board {
    fun draw(shape: Shape, terrain: Terrain): Board
    fun terrainAt(x: Int, y: Int): Terrain
    fun all(predicate: (Terrain) -> Boolean): Set<Pair<Int, Int>>
    fun biggestSquareLength(): Int
    fun countFullRowsAndColumns(): Int
    fun countLeftToBottomDiameters(): Int
    fun connectedTerrains(terrain: Terrain): Set<Set<Pair<Int, Int>>>

    companion object {
        fun create(): Board =
            MapBoard(
                mapOf(
                    Point(-1, 3) to Terrain.MOUNTAIN,
                    Point(-2, 8) to Terrain.MOUNTAIN,
                    Point(-5, 5) to Terrain.MOUNTAIN,
                    Point(-8, 2) to Terrain.MOUNTAIN,
                    Point(-9, 7) to Terrain.MOUNTAIN,
                    Point(-1, 5) to Terrain.RUINS,
                    Point(-2, 1) to Terrain.RUINS,
                    Point(-2, 9) to Terrain.RUINS,
                    Point(-8, 1) to Terrain.RUINS,
                    Point(-8, 9) to Terrain.RUINS,
                    Point(-9, 5) to Terrain.RUINS
                ).withDefault { Terrain.EMPTY }
            )

    }
}

data class Point(val x: Int, val y: Int) {
    private fun moveX(offset: Int) = Point(x + offset, y)
    private fun moveY(offset: Int) = Point(x + offset, y)

    fun adjacent(minX: Int, maxX: Int, minY: Int, maxY: Int) =
        listOf(
            moveX(1),
            moveX(-1),
            moveY(1),
            moveY(1).moveX(1),
            moveY(1).moveX(-1),
            moveY(-1),
            moveY(-1).moveX(1),
            moveY(-1).moveX(-1))
            .filter { it.x in minX..maxX && it.y in minY..maxY }
            .toSet()
}

// lets assume that map returns empty for unknown indices
class MapBoard(private val board: Map<Point, Terrain>) : Board {
    override fun draw(shape: Shape, terrain: Terrain): Board {
        val newBoard = board.toMutableMap()
        for ((x, y) in shape.toXYPoints()) {
            newBoard[Point(x, y)] = terrain
        }
        return MapBoard(newBoard.toMap().withDefault { Terrain.EMPTY })
    }

    override fun terrainAt(x: Int, y: Int): Terrain = terrainAt(Point(x, y))

    override fun all(predicate: (Terrain) -> Boolean): Set<Pair<Int, Int>> =
        board.filterValues(predicate).keys.map { it.x to it.y }.toSet()

    private fun allPoints(predicate: (Terrain) -> Boolean): Set<Point> =
        board.filterValues(predicate).keys

    override fun biggestSquareLength(): Int {
        var globalBest = 0
        for (x in 0 downTo -10) {
            for (y in 0..10) {
                val localBest = (1..11).takeWhile { x - it <= -10 && y + it <= 10 > isSquareOfSize(x, y, it) }
                    .lastOrNull() ?: 0
                globalBest = max(globalBest, localBest)
            }
        }
        return globalBest
    }

    override fun countFullRowsAndColumns(): Int =
        board.entries.groupBy { it.key.x }.count { it.value.all { e -> e.value != Terrain.EMPTY } } +
                board.entries.groupBy { it.key.y }.count { it.value.all { e -> e.value != Terrain.EMPTY } }

    override fun countLeftToBottomDiameters(): Int =
        (0 downTo -10).count { x -> (0..(10 + x)).all { y -> terrainAt(x, y) != Terrain.EMPTY } }

    override fun connectedTerrains(terrain: Terrain): Set<Set<Pair<Int, Int>>> {
        allPoints { it == terrain }
        return emptySet()
    }

    private fun terrainAt(p: Point): Terrain = when {
        p.x > 0 || p.x < -10 || p.y < 0 || p.y > 10 -> Terrain.OUTSIDE_THE_MAP
        else -> board.getValue(p)
    }

    // assuming that there is square staring at x,y of size: size - 1
    private fun isSquareOfSize(x: Int, y: Int, size: Int): Boolean = when (size) {
        1 -> terrainAt(x, y) != Terrain.EMPTY
        else -> {
            val offset = size - 1
            val row = x - offset
            val col = y + offset
            IntStream.range(0, size)
                .allMatch { terrainAt(row, col + it) != Terrain.EMPTY && terrainAt(row - it, col) != Terrain.EMPTY }
        }
    }

    override fun toString(): String =
        "\n" + (0 downTo -10).joinToString("\n") { x ->
            (0..10).joinToString("") { y ->
                this.terrainAt(x, y).toString()
            }
        } + "\n"
}

interface Shape {
    fun createAllVariations(): Set<Shape>
    fun isOutOfBounds(): Boolean
    fun isAnyPartOn(board: Board, terrain: Terrain): Boolean
    fun toXYPoints(): Set<Pair<Int, Int>>
    fun normalize(): Shape
    fun size(): Int

    companion object {
        fun create(points: Set<Pair<Int, Int>>): Shape = PointGroupShape(points)

        fun create(img: String): Shape =
            create(
                img.trimIndent()
                    .lines()
                    .asSequence()
                    .filter { it.isNotBlank() }
                    .map { it.trimEnd() }
                    .mapIndexed { rowIdx, it ->
                        it.mapIndexedNotNull { colIdx, c -> if (c == '[') (-rowIdx to colIdx / 3) else null }
                    }
                    .flatten()
                    .toSet()
            )
    }
}

data class PointGroupShape(
    private val points: Set<Pair<Int, Int>>
) : Shape {

    override fun createAllVariations(): Set<Shape> =
        setOf(
            this,
            moveTopLeftToZeroZero(points.map { -it.first to it.second }),
            moveTopLeftToZeroZero(points.map { -it.first to -it.second }),
            moveTopLeftToZeroZero(points.map { it.first to -it.second }),
            moveTopLeftToZeroZero(points.map { it.second to -it.first }),
            moveTopLeftToZeroZero(points.map { -it.second to -it.first }),
            moveTopLeftToZeroZero(points.map { -it.second to it.first }),
            moveTopLeftToZeroZero(points.map { it.second to it.first })
        )

    override fun isOutOfBounds(): Boolean = points.any { (x, y) -> x > 0 || x < -10 || y < 0 || y > 10 }

    override fun isAnyPartOn(board: Board, terrain: Terrain): Boolean =
        points.any { (x, y) -> board.terrainAt(x, y) == Terrain.MOUNTAIN }

    override fun toXYPoints(): Set<Pair<Int, Int>> = points

    override fun normalize(): Shape = moveTopLeftToZeroZero(points)

    override fun size(): Int = points.size

    companion object {
        private fun moveTopLeftToZeroZero(positions: Collection<Pair<Int, Int>>): Shape {
            val (leftX, topY) =
                positions.sortedWith(compareBy<Pair<Int, Int>> { it.second }.thenByDescending { it.first }).first()
            return PointGroupShape(positions.map { it.first - leftX to it.second - topY }.toSet())
        }

        fun create(points: Set<Pair<Int, Int>>) = moveTopLeftToZeroZero(points)
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

class Player(val id: String) {
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

class GameImplementation(
    private var deck: List<Card> = listOf(
        TreeFortress14,
        BigRiver7,
        ForgottenForest10,
        Orchard13,
        City9,
        Ruins,
        Ruins,
        RuralStream11,
        Cracks17,
        Farm12,
        Fends15,
        Fields08,
        FishermanVillage16
    ),
    private var monstersDeck: Set<Card> = setOf(
        GoblinsAttack01,
        BogeymanAssault02,
        CoboldsCharge03,
        GnollsInvasion04
    ),
    private var scoreCards: Map<Season, ScoreCard>,
    private val shuffler: (List<Card>) -> List<Card> = { cards -> cards.shuffled() }
) : Game {
    var season: Season = Season.SPRING
    var currentCardIndex: Int = 0
    var pointsInRound: Int = 0
    var players: List<Player> = emptyList()
    var playersDone: Int = 0
    var ruinsDrawn: Boolean = false
    var enemyDrawn: Boolean = false

    private fun cleanBeforeNextTurn() {
        val monsterCard = monstersDeck.random()
        monstersDeck = monstersDeck - monsterCard
        deck = shuffler(deck + monsterCard)
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

    fun update(id: String, shape: Shape, terrain: Terrain): Response {
        val player = player(id) ?: throw RuntimeException("No player with id $id")
        val currentCard = deck[currentCardIndex]
        if (!currentCard.isValid(shape)) {
            return Response(error = "invalid shape")
        }
        if (!currentCard.isValid(terrain)) {
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
        player.board = player.board.draw(shape, terrain)
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

    override fun join(nick: String): GameImplementation {
        players = players + Player(nick)
        return this
    }

    override fun start(): GameImplementation {
        return this
    }

    override fun draw(nick: String, points: Set<Pair<Int, Int>>, terrain: Terrain): GameImplementation {
        val (error, events) = update(nick, Shape.create(points), terrain)
        return this
    }

    private fun player(nick: String) = players.find { it.id == nick }

    override fun boardOf(nick: String): Board = player(nick)?.board!!
}

interface Game {
    fun join(nick: String): Game
    fun start(): Game
    fun draw(nick: String, points: Set<Pair<Int, Int>>, terrain: Terrain): Game
    fun boardOf(nick: String): Board
}

fun main() {
    (0 downTo -10).count { x -> (0..(10 + x)).all { y -> terrainAt(x, y) != Terrain.EMPTY } }
}

fun terrainAt(x: Int, y: Int): Any {
    println("Called with $x $y")
    return Terrain.MONSTER
}
