package com.github.hejcz

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
    fun all(terrain: Terrain): Set<Pair<Int, Int>>

    companion object {
        fun create(): Board =
            MapBoard(
                mapOf(
                    (-1 to 3) to Terrain.MOUNTAIN,
                    (-2 to 8) to Terrain.MOUNTAIN,
                    (-5 to 5) to Terrain.MOUNTAIN,
                    (-8 to 2) to Terrain.MOUNTAIN,
                    (-9 to 7) to Terrain.MOUNTAIN,
                    (-1 to 5) to Terrain.RUINS,
                    (-2 to 1) to Terrain.RUINS,
                    (-2 to 9) to Terrain.RUINS,
                    (-8 to 1) to Terrain.RUINS,
                    (-8 to 9) to Terrain.RUINS,
                    (-9 to 5) to Terrain.RUINS
                ).withDefault { Terrain.EMPTY }
            )

        fun create2(): Board =
            ArrayBoard(
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

// lets assume that map returns empty for unknown indices
class MapBoard(private val board: Map<Pair<Int, Int>, Terrain>) : Board {
    override fun draw(shape: Shape, terrain: Terrain): Board {
        val newBoard = board.toMutableMap()
        for ((x, y) in shape.toXYPoints()) {
            newBoard[x to y] = terrain
        }
        return MapBoard(newBoard.toMap().withDefault { Terrain.EMPTY })
    }

    override fun terrainAt(x: Int, y: Int): Terrain = when {
        x > 0 || x < -10 || y < 0 || y > 10 -> Terrain.OUTSIDE_THE_MAP
        else -> board.getValue(x to y)
    }

    override fun all(terrain: Terrain): Set<Pair<Int, Int>> {
        throw NotImplementedError()
    }

    override fun toString(): String =
        "\n" + (0 downTo -10).joinToString("\n") { x -> (0..10).joinToString("") { y -> this.terrainAt(x, y).toString() } } + "\n"
}

class ArrayBoard(private val board: Array<Array<Terrain>>) : Board {
    override fun draw(shape: Shape, terrain: Terrain): Board {
        for ((x, y) in shape.toXYPoints()) {
            board[x + 7][y + 7] = Terrain.FOREST
        }
        return this
    }

    override fun terrainAt(x: Int, y: Int): Terrain = board[x][y]

    override fun all(terrain: Terrain): Set<Pair<Int, Int>> {
        throw NotImplementedError()
    }
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
                        it.mapIndexedNotNull { colIdx, c -> if (c == '[') (-rowIdx to colIdx/3) else null } }
                    .flatten()
                    .toSet()
            )
    }
}

data class PointGroupShape(
    private val points: Set<Pair<Int, Int>>) : Shape {

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
    val s = Shape.create(
        """
                  [ ]
            [ ][ ][ ]
            [ ][ ]
            """
    )
    println(s)
}