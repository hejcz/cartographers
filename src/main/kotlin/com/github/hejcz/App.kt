package com.github.hejcz

enum class Terrain(private val str: () -> String) {
    OUTSIDE_THE_MAP({ -> throw RuntimeException("cant print outside the map") }),
    EMPTY({ -> "[ ]" }),
    MOUNTAIN({ -> "[M]" }),
    FOREST({ -> "[F]" }),
    CITY({ -> "[C]" }),
    PLAINS({ -> "[P]" }),
    WATER({ -> "[W]" }),
    MONSTER({ -> "[D]" });

    override fun toString(): String {
        return str()
    }

    companion object {
        fun from(str: String) = when(str) {
            "[D]" -> MONSTER
            "[F]" -> FOREST
            "[C]" -> CITY
            "[P]" -> PLAINS
            "[W]" -> WATER
            "[M]" -> MOUNTAIN
            else -> throw RuntimeException("should not create terrain from $str")
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

fun Board.countMonsterPoints(): Int = all { it == Terrain.MONSTER }
    .flatMap { xy -> adjacent(xy) }
    .toSet()
    .count { terrainAt(it.first, it.second) == Terrain.EMPTY }

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
    private var scoreCards: Map<Season, ScoreCard> =
        listOf(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)
            .zip(
                listOf(
                    setOf(
                        ForestTower28,
                        ForestGuard26,
                        Coppice27,
                        MountainWoods29
                    ).random(),
                    setOf(
                        HugeCity35,
                        Fortress37,
                        Colony34,
                        FertilePlain36
                    ).random(),
                    setOf(
                        FieldPuddle30,
                        MagesValley30,
                        VastEnbankment33,
                        GoldenBreadbasket
                    ).random(),
                    setOf(
                        Hideouts41,
                        LostDemesne39,
                        Borderlands38,
                        TradingRoad40
                    ).random()
                ).shuffled()
            ) { season, card -> season to card }.toMap(),
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
        if (shape.anyMatches { (x, y) -> player.board.terrainAt(x, y) == Terrain.MOUNTAIN }) {
            return Response(error = "shape on mountain")
        }
        if (ruinsDrawn && !shape.anyMatches { (x, y) -> player.board.hasRuinsOn(x, y) }) {
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
    println((1..11).takeWhile { -8 - it >= -11 && 2 + it <= 11 })
}

fun terrainAt(x: Int, y: Int): Any {
    println("Called with $x $y")
    return Terrain.MONSTER
}
