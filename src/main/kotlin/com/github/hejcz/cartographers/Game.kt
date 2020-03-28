package com.github.hejcz.cartographers

import java.util.*

class GameImplementation(
    private val gameId: String = UUID.randomUUID().toString(),
    private var deck: List<Card> = cards().shuffled(),
    private var monstersDeck: List<Card> = monsters().shuffled(),
    private var scoreCards: Map<Season, ScoreCard> =
        listOf(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)
            .zip(randomScoreCards().shuffled()) { season, card -> season to card }
            .toMap(),
    private val options: GameOptions = GameOptions(),
    private val shuffler: (List<Card>) -> List<Card> = { cards -> cards.shuffled() }
) : Game {
    private var season: Season = Season.SPRING
    private var currentCardIndex: Int = 0
    private var pointsInRound: Int = 0
    private var players: List<Player> = emptyList()
    private var ruinsPicked: Boolean = false
    private var monsterDrawn: Boolean = false
    private val playersDone: MutableSet<String> = mutableSetOf()
    private var recentEvents: Events = InMemoryEvents(emptyList())
    private var gameEnded: Boolean = false
    private var started: Boolean = false

    override fun canJoin(nick: String): Boolean = !started || player(nick)?.left ?: false

    override fun join(nick: String): Game {
        recentEvents.clear()
        if (player(nick)?.left == true) {
            processReconnect(nick)
        } else {
            processFirstConnection(nick)
        }
        return this
    }

    private fun processFirstConnection(nick: String) {
        val player = Player(nick, if (options.advancedBoard) Board.createAdvanced() else Board.create())
        players = players + player
        recentEvents = InMemoryEvents(players.map { it.nick })
        recentEvents.add(nick, BoardEvent(player.board.allPoints(), player.board.ruins()))
    }

    private fun processReconnect(nick: String) {
        player(nick)?.left = false
        val player = player(nick)!!
        (setOf(
            newCardEvent(),
            goalsEvent(),
            BoardEvent(player.board.allPoints(), player.board.ruins()),
            CoinsEvent(player.coins)
        ) + player.summaries
            .mapIndexed { index, it -> ScoresEvent(toScore(it, Season.byIndex(index))) })
            .forEach { recentEvents.add(player.nick, it) }
    }

    override fun start(nick: String): Game {
        if (started) {
            recentEvents.add(nick, ErrorEvent(ErrorCode.GAME_IN_PROGRESS))
            return this
        }
        started = true
        recentEvents = InMemoryEvents(players.map { it.nick })
        cleanBeforeNextTurn()
        onNextCard()
        recentEvents.addAll(
            newCardEvent(),
            goalsEvent()
        )
        return this
    }

    override fun draw(nick: String, points: Set<Point>, terrain: Terrain): Game {
        recentEvents.clear()
        when {
            !started -> recentEvents.add(nick, ErrorEvent(ErrorCode.GAME_NOT_STARTED_YET))
            gameEnded -> recentEvents.add(nick, ErrorEvent(ErrorCode.GAME_FINISHED))
            nick in playersDone -> recentEvents.add(nick, ErrorEvent(ErrorCode.CANT_DRAW_TWICE))
            else -> update(nick, Shape.create(points), terrain)
        }
        return this
    }

    override fun leave(nick: String): Game {
        player(nick)?.left = true
        return this
    }

    // ruins card does not affect monsters
    private fun shouldDrawOnRuins() = ruinsPicked && !monsterDrawn

    private fun update(nick: String, shape: Shape, terrain: Terrain) {
        val player = player(nick) ?: throw RuntimeException("No player with id $nick")
        val currentCard = deck[currentCardIndex]

        val boardOwner = when {
            monsterDrawn -> playerMatching(player)
            else -> player
        }

        val board = boardOwner.board

        val error = validate(shape, board, currentCard, terrain)
        if (error != null) {
            recentEvents.add(nick, ErrorEvent(error))
            return
        }

        boardOwner.board = board.draw(shape, terrain)

        if (currentCard.givesCoin(shape)) {
            boardOwner.coins++
        }

        boardOwner.coins += countMountainsClosedWith(shape, board)
        playersDone.add(nick)

        recentEvents.add(nick, AcceptedShape(terrain, shape.toPoints(), player.coins))

        if (players.size != playersDone.size) {
            return
        }

        pointsInRound += deck[currentCardIndex].points()

        if (season.pointsInRound > pointsInRound) {
            cleanBeforeNextCard()
            onNextCard()
            recentEvents.addAll(newCardEvent())
            return
        }

        cleanBeforeNextTurn()
        calculateScore()
        players.map { it.nick to toScore(it.summaries.last(), season) }
            .forEach { (nick, summary) -> recentEvents.add(nick, ScoresEvent(summary)) }

        if (season == Season.WINTER) {
            endGame()
        } else {
            season = season.next()
            onNextCard()
            recentEvents.addAll(newCardEvent())
        }
    }

    private fun validate(shape: Shape, board: Board,
            currentCard: Card, terrain: Terrain): ErrorCode? {
        if (shape.isEmpty()) {
            return ErrorCode.EMPTY_SHAPE
        }

        if (shape.anyMatches { board.terrainAt(it) != Terrain.EMPTY }) {
            return ErrorCode.SHAPE_OUTSIDE_THE_MAP_OR_OVERLAPING
        }

        val is1x1SpecialCase = shape.size() == 1 &&
                (shouldDrawOnRuins() && !board.canDrawShapeOnRuins(currentCard.availableShapes())
                        || board.noPlaceToDraw(currentCard.availableShapes()))

        if (monsterDrawn) {
            if (terrain != Terrain.MONSTER) {
                return ErrorCode.INVALID_TERRAIN_TYPE
            }
        }

        if (!is1x1SpecialCase) {
            if (!currentCard.isValid(shape)) {
                return ErrorCode.INVALID_SHAPE
            }
            if (!currentCard.isValid(terrain)) {
                return ErrorCode.INVALID_TERRAIN_TYPE
            }
            if (shouldDrawOnRuins() && !shape.anyMatches { board.hasRuinsOn(it) }) {
                return ErrorCode.SHAPE_MUST_BE_ON_RUINS
            }
        }

        return null
    }

    private fun cleanBeforeNextTurn() {
        if (monstersDeck.isNotEmpty()) {
            val monsterCard = monstersDeck.random()
            monstersDeck = monstersDeck - monsterCard
            deck = deck + monsterCard
        }
        deck = shuffler(deck)
        currentCardIndex = 0
        pointsInRound = 0
        playersDone.clear()
        ruinsPicked = false
        monsterDrawn = false
    }

    private fun cleanBeforeNextCard() {
        if (deck[currentCardIndex] is MonsterCard) {
            deck = deck - deck[currentCardIndex]
            players.forEach { recentEvents.add(it.nick, BoardEvent(it.board.allPoints(), it.board.ruins())) }
        } else {
            currentCardIndex += 1
        }
        playersDone.clear()
        ruinsPicked = if (monsterDrawn) ruinsPicked else false
        monsterDrawn = false
    }

    private fun playerMatching(player: Player): Player = if (players.size == 1) {
        player
    } else {
        val idx = players.indexOf(player)
        when ((deck[currentCardIndex] as MonsterCard).direction()) {
            Direction.CLOCKWISE -> players[if (idx == players.size - 1) 0 else idx + 1]
            Direction.COUNTERCLOCKWISE -> players[ if (idx == 0) players.size - 1 else idx - 1]
        }
    }

    private fun countMountainsClosedWith(shape: Shape, board: Board): Int = shape.toPoints()
        .flatMap { board.adjacent(it) }
        .distinct()
        .filter { board.terrainAt(it) == Terrain.MOUNTAIN }
        .count { mountain -> board.adjacent(mountain).all { board.terrainAt(it) != Terrain.EMPTY } }

    private fun endGame() {
        val (winner, totalScore) = players.map { it to it.summaries.sumBy(RoundSummary::sum) }.maxBy { it.second }!!
        recentEvents.addAll(Results(winner.nick, totalScore))
        gameEnded = true
    }

    private fun onNextCard() {
        while (deck[currentCardIndex] is Ruins) {
            currentCardIndex++
            ruinsPicked = true
        }

        // swap boards
        val currentCard = deck[currentCardIndex]
        if (currentCard is MonsterCard && options.swapBoardsOnMonsters) {
            monsterDrawn = true

            val shiftedNicks: List<String> = when (currentCard.direction()) {
                Direction.CLOCKWISE -> listOf(players.last()) + players.dropLast(1)
                Direction.COUNTERCLOCKWISE -> players.drop(1) + players.take(1)
            }.map { it.nick }

            shiftedNicks.zip(players)
                .forEach { (drawingPlayerNick, boardOwner) ->
                    recentEvents.add(
                        drawingPlayerNick,
                        BoardEvent(boardOwner.board.allPoints(), boardOwner.board.ruins())
                    )
                }
        }
    }

    private fun calculateScore() {
        val quest1 = scoreCards.getValue(season)
        val quest2 = scoreCards.getValue(season.next())
        players.forEach {
            it.summaries = it.summaries + RoundSummary(
                quest1.evaluate(it.board), quest2.evaluate(it.board), it.coins, countMonsterPoints(it.board)
            )
        }
    }

    private fun goalsEvent(): GoalsEvent = GoalsEvent(
        scoreCardId(Season.SPRING), scoreCardId(Season.SUMMER), scoreCardId(Season.AUTUMN),
        scoreCardId(Season.WINTER)
    )

    private fun scoreCardId(season: Season) = scoreCards.getValue(season)::class.java.simpleName.takeLast(2)

    private fun player(nick: String) = players.find { it.nick == nick }

    override fun boardOf(nick: String): Board = player(nick)?.board!!

    override fun recentEvents(): Map<String, Set<Event>> = recentEvents.getAll()

    override fun isFinished(): Boolean = gameEnded

    private fun toScore(s: RoundSummary, season: Season) =
        Score(s.quest1Points, s.quest2Points, s.coinsPoints, s.monstersPenalty, season)

    private fun newCardEvent(): NewCardEvent {
        return NewCardEvent(
            deck[currentCardIndex].number(),
            shouldDrawOnRuins(),
            pointsInRound + deck[currentCardIndex].points(),
            season.pointsInRound
        )
    }

    override fun toString(): String {
        return "GameImplementation(gameId='$gameId', deck=$deck, monstersDeck=$monstersDeck, scoreCards=$scoreCards, shuffler=$shuffler, season=$season, currentCardIndex=$currentCardIndex, pointsInRound=$pointsInRound, players=$players, ruinsDrawn=$ruinsPicked, monsterDrawn=$monsterDrawn, playersDone=$playersDone, recentEvents=$recentEvents, gameEnded=$gameEnded, started=$started)"
    }

    companion object {
        private fun cards() = listOf(
            Ruins05, Ruins06, BigRiver07, Fields08, City09, ForgottenForest10, RuralStream11,
            Farm12, Orchard13, TreeFortress14, Fends15, FishermanVillage16, Cracks17
        )

        private fun monsters(): Set<MonsterCard> =
            setOf(GoblinsAttack01, BogeymanAssault02, CoboldsCharge03, GnollsInvasion04)

        private fun randomScoreCards(): List<ScoreCard> =
            listOf(
                setOf(GoldenBreadbasket32, FieldPuddle30, MagesValley31, VastEnbankment33).random(),
                setOf(HugeCity35, Colony34, FertilePlain36, Fortress37).random(),
                setOf(ForestTower28, MountainWoods29, Coppice27, ForestGuard26).random(),
                setOf(Borderlands38, LostDemesne39, TradingRoad40, Hideouts41).random()
            )

        fun countMonsterPoints(board: Board): Int = board.all { it == Terrain.MONSTER }
            .flatMap { xy -> board.adjacent(xy) }
            .toSet()
            .count { board.terrainAt(it) == Terrain.EMPTY }
    }
}

interface Game {
    fun join(nick: String): Game
    fun start(nick: String): Game
    fun draw(nick: String, points: Set<Point>, terrain: Terrain): Game
    fun boardOf(nick: String): Board
    fun leave(nick: String): Game
    fun canJoin(nick: String): Boolean
    fun recentEvents(): Map<String, Set<Event>>
    fun isFinished(): Boolean
}
