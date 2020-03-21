package com.github.hejcz.cartographers

import com.github.hejcz.http.Nick
import java.util.*

data class RoundSummary(
    val quest1Points: Int,
    val quest2Points: Int,
    val coinsPoints: Int,
    val monstersPenalty: Int
) {
    fun sum(): Int = quest1Points + quest2Points + coinsPoints - monstersPenalty
}

class Player(val nick: String) {
    var board: Board =
        Board.create()
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

private fun cards() = listOf(
    Ruins05, Ruins06, BigRiver07, Fields08, City09, ForgottenForest10, RuralStream11,
    Farm12, Orchard13, TreeFortress14, Fends15, FishermanVillage16, Cracks17
)

private fun monsters(): Set<MonsterCard> =
    setOf(GoblinsAttack01, BogeymanAssault02, CoboldsCharge03, GnollsInvasion04)

private fun randomScoreCards(): List<ScoreCard> =
    listOf(
        setOf(ForestGuard26, Coppice27, ForestTower28, MountainWoods29).random(),
        setOf(Colony34, HugeCity35, FertilePlain36, Fortress37).random(),
        setOf(FieldPuddle30, MagesValley31, GoldenBreadbasket32, VastEnbankment33).random(),
        setOf(Borderlands38, LostDemesne39, TradingRoad40, Hideouts41).random()
    )

class GameImplementation(
    private val gameId: String = UUID.randomUUID().toString(),
    private var deck: List<Card> = cards().shuffled(),
    private var monstersDeck: List<Card> = monsters().shuffled(),
    private var scoreCards: Map<Season, ScoreCard> =
        listOf(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)
            .zip(randomScoreCards().shuffled()) { season, card -> season to card }
            .toMap(),
    private val shuffler: (List<Card>) -> List<Card> = { cards -> cards.shuffled() }
) : Game {
    private var season: Season = Season.SPRING
    private var currentCardIndex: Int = 0
    private var pointsInRound: Int = 0
    private var players: List<Player> = emptyList()
    private var ruinsDrawn: Boolean = false
    private var monsterDrawn: Boolean = false
    private val playersDone: MutableSet<String> = mutableSetOf()
    lateinit var recentEvents: Events
    private var gameEnded: Boolean = false
    private var started: Boolean = false

    override fun join(nick: String): Game {
        players = players + Player(nick)
        return this
    }

    override fun start(nick: String): Game {
        if (started) {
            recentEvents.add(nick, ErrorEvent("ALREADY_STARTED"))
            return this
        }
        started = true
        recentEvents = InMemoryEvents(players.map { it.nick })
        onNextCard()
        recentEvents.addAll(NewCardEvent(deck[currentCardIndex].number(), ruinsDrawn))
        return this
    }

    override fun draw(nick: String, points: Set<Pair<Int, Int>>, terrain: Terrain): Game {
        update(nick, Shape.create(points), terrain)
        return this
    }

    private fun player(nick: String) = players.find { it.nick == nick }

    override fun boardOf(nick: String): Board = player(nick)?.board!!

    override fun recentEvents(nick: String): Set<Event> = recentEvents.of(nick)

    private fun cleanBeforeNextTurn() {
        val monsterCard = monstersDeck.random()
        monstersDeck = monstersDeck - monsterCard
        deck = shuffler(deck + monsterCard)
        currentCardIndex = 0
        pointsInRound = 0
        playersDone.clear()
        ruinsDrawn = false
        monsterDrawn = false
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
        playersDone.clear()
        ruinsDrawn = false
        monsterDrawn = false
    }

    private fun update(nick: String, shape: Shape, terrain: Terrain) {
        if (nick in playersDone) {
            recentEvents.add(nick, ErrorEvent("already drawn"))
            return
        }
        if (gameEnded) {
            recentEvents.add(nick, ErrorEvent("game ended"))
            return
        }
        recentEvents.clear()
        val player = player(nick) ?: throw RuntimeException("No player with id $nick")
        val currentCard = deck[currentCardIndex]
        if (!currentCard.isValid(shape)
            && !(shape.size() == 1 && player.board.noPlaceToDraw(shape.createAllVariations()))
        ) {
            recentEvents.add(nick, ErrorEvent("invalid shape"))
            return
        }
        if (!currentCard.isValid(terrain)) {
            recentEvents.add(nick, ErrorEvent("invalid terrain"))
            return
        }
        if (shape.anyMatches { (x, y) -> player.board.terrainAt(x, y) == Terrain.OUTSIDE_THE_MAP }) {
            recentEvents.add(nick, ErrorEvent("shape outside the map"))
            return
        }
        if (shape.anyMatches { (x, y) -> player.board.terrainAt(x, y) != Terrain.EMPTY }) {
            recentEvents.add(nick, ErrorEvent("shape on taken point"))
            return
        }
        if (ruinsDrawn && !shape.anyMatches { (x, y) -> player.board.hasRuinsOn(x, y) }) {
            recentEvents.add(nick, ErrorEvent("shape must be on ruins"))
            return
        }
        player.board = player.board.draw(shape, terrain)
        if (currentCard.givesCoin(shape)) {
            player.coins++
        }
        playersDone.add(nick)
        recentEvents.add(nick, AcceptedShape(terrain, shape.toXYPoints().map { (x, y) -> Point(x, y) }))
        if (players.size == playersDone.size) {
            if (season.pointsInRound <= pointsInRound) {
                if (season == Season.WINTER) {
                    endGame()
                    return
                }
                cleanBeforeNextTurn()
                onNextCard()
                recentEvents.addAll(
                    NewCardEvent(deck[currentCardIndex].number(), ruinsDrawn),
                    ScoresEvent(players.map { it.nick to it.summaries.last().sum() }.toMap())
                )
            } else {
                cleanBeforeNextCard()
                onNextCard()
                recentEvents.addAll(
                    NewCardEvent(deck[currentCardIndex].number(), ruinsDrawn)
                )
            }
        }
    }

    private fun onNextCard() {
        while (deck[currentCardIndex] is Ruins) {
            currentCardIndex++
            ruinsDrawn = true
        }
        // TODO
        // 1. monsters
        // 2. coins for surrounding a mountain
        // 3. test other score cards
//        val currentCard = deck[currentCardIndex]
//        if (currentCard is MonsterCard) {
//            monsterDrawn = true
//
//            val shiftedPlayers = when (currentCard.direction()) {
//                Direction.CLOCKWISE -> listOf(players.last()) + players.dropLast(1)
//                Direction.COUNTERCLOCKWISE -> players.drop(1) + players.take(1)
//            }
//
//            shiftedPlayers.zip(players) { drawingP, boardOwner ->
//                recentEvents.replace(drawingP.nick, DrawMonsterEvent(boardOwner.board.toString()))
//            }
//        }
    }

    private fun endGame() {
        val idToTotalScore =
            players.map { it.nick to it.summaries.sumBy(RoundSummary::sum) }.toMap()
        recentEvents.addAll(ScoresEvent(idToTotalScore))
        gameEnded = true
    }
}

interface Game {
    fun join(nick: String): Game
    fun start(nick: String): Game
    fun draw(nick: String, points: Set<Pair<Int, Int>>, terrain: Terrain): Game
    fun boardOf(nick: String): Board
    fun recentEvents(nick: String): Set<Event>
}
