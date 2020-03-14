package com.github.hejcz

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
        BigRiver07,
        ForgottenForest10,
        Orchard13,
        City09,
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
    private var season: Season = Season.SPRING
    private var currentCardIndex: Int = 0
    private var pointsInRound: Int = 0
    private var players: List<Player> = emptyList()
    private var playersDone: Int = 0
    private var ruinsDrawn: Boolean = false
    private var enemyDrawn: Boolean = false
    private val recentEvents: Events = InMemoryEvents()

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
        val quest1 = scoreCards.getValue(season)
        val quest2 = scoreCards.getValue(season.next())
        players.forEach {
            it.summaries = it.summaries + RoundSummary(
                quest1.evaluate(it.board), quest2.evaluate(it.board), it.coins, it.board.countMonsterPoints()
            )
        }
    }

    private fun cleanBeforeNextCard() {
        pointsInRound += deck[currentCardIndex].points()
        currentCardIndex += 1
        playersDone = 0
        ruinsDrawn = false
        enemyDrawn = false
    }

    fun update(nick: String, shape: Shape, terrain: Terrain) {
        val player = player(nick) ?: throw RuntimeException("No player with id $nick")
        val currentCard = deck[currentCardIndex]
        if (!currentCard.isValid(shape)) {
            recentEvents.replace(nick, ErrorEvent("invalid shape"))
            return
        }
        if (!currentCard.isValid(terrain)) {
            recentEvents.replace(nick, ErrorEvent("invalid terrain"))
            return
        }
        if (shape.isOutOfBounds()) {
            recentEvents.replace(nick, ErrorEvent("shape outside the map"))
            return
        }
        if (shape.anyMatches { (x, y) -> player.board.terrainAt(x, y) != Terrain.EMPTY }) {
            recentEvents.replace(nick, ErrorEvent("shape on taken point"))
            return
        }
        if (ruinsDrawn && !shape.anyMatches { (x, y) -> player.board.hasRuinsOn(x, y) }) {
            recentEvents.replace(nick, ErrorEvent("shape must be on ruins"))
            return
        }
        player.board = player.board.draw(shape, terrain)
        if (currentCard.givesCoin(shape)) {
            player.coins++
        }
        ++playersDone
        if (players.size == playersDone) {
            if (season.pointsInRound <= pointsInRound) {
                if (season == Season.WINTER) {
                    endGame()
                    return
                }
                cleanBeforeNextTurn()
                recentEvents.replaceAll(
                    NewCardEvent(deck[currentCardIndex].number()),
                    ScoresEvent(players.map { it.id to it.summaries.last().sum() }.toMap())
                )
            } else {
                cleanBeforeNextCard()
                cleanBeforeNextTurn()
                recentEvents.replaceAll(
                    NewCardEvent(deck[currentCardIndex].number())
                )
            }
        }
    }

    private fun endGame() {
        val idToTotalScore =
            players.map { it.id to it.summaries.sumBy(RoundSummary::sum) }.toMap()
        val maxScore = idToTotalScore.values.max() ?: 0
        recentEvents.replaceAll(ScoresEvent(idToTotalScore))
    }

    override fun join(nick: String): GameImplementation {
        players = players + Player(nick)
        return this
    }

    override fun start(): GameImplementation {
        return this
    }

    override fun draw(nick: String, points: Set<Pair<Int, Int>>, terrain: Terrain): GameImplementation {
        update(nick, Shape.create(points), terrain)
        return this
    }

    private fun player(nick: String) = players.find { it.id == nick }

    override fun boardOf(nick: String): Board = player(nick)?.board!!

    override fun recentEvents(nick: String): List<Event> = recentEvents.of(nick)
}

interface Game {
    fun join(nick: String): Game
    fun start(): Game
    fun draw(nick: String, points: Set<Pair<Int, Int>>, terrain: Terrain): Game
    fun boardOf(nick: String): Board
    fun recentEvents(nick: String): List<Event>
}

fun main() {
    println((1..11).takeWhile { -8 - it >= -11 && 2 + it <= 11 })
}

fun terrainAt(x: Int, y: Int): Any {
    println("Called with $x $y")
    return Terrain.MONSTER
}
