package com.github.hejcz.cartographers

enum class EventType {
    ACCEPTED_SHAPE,
    NEW_CARD,
    SCORE,
    RESULTS,
    ERROR,
    GOALS,
    BOARD,
    COINS
}

interface Events {
    fun addAll(vararg events: Event)
    fun of(nick: String): Set<Event>
    fun add(nick: String, event: Event)
    fun clear()
}

class InMemoryEvents(private val nicks: List<String>) : Events {
    private val empty: Map<String, Set<Event>> = nicks.map { it to emptySet<Event>() }.toMap()

    private var map = empty

    override fun addAll(vararg events: Event) {
        map = nicks.map { it to (map[it] ?: emptySet()) + events.toSet() }.toMap()
    }

    override fun of(nick: String): Set<Event> {
        return map[nick] ?: emptySet()
    }

    override fun add(nick: String, event: Event) {
        map = map + (nick to (map[nick] ?: emptySet()) + event)
    }

    override fun clear() {
        map = empty
    }

    override fun toString(): String {
        return "InMemoryEvents(map=$map)"
    }
}

interface Event {
    val type: EventType
    fun broadcast(): Boolean = false
}

data class AcceptedShape(val terrain: Terrain, val points: Collection<Point>, val totalCoins: Int,
                         override val type: EventType = EventType.ACCEPTED_SHAPE) : Event

data class NewCardEvent(val card: String, val ruins: Boolean,
    val currentTurnPoints: Int, val maxTurnPoints: Int, override val type: EventType = EventType.NEW_CARD) : Event {
    override fun broadcast(): Boolean = true
}

data class Score(val quest1: Int, val quest2: Int, val coins: Int, val monsters: Int, val season: Season)

data class ScoresEvent(val score: Score, override val type: EventType = EventType.SCORE) : Event {
    override fun broadcast(): Boolean = true
}

data class Results(val winner: String, override val type: EventType = EventType.RESULTS) : Event {
    override fun broadcast(): Boolean = true
}

data class ErrorEvent(val error: ErrorCode, override val type: EventType = EventType.ERROR) : Event

data class GoalsEvent(val spring: String, val summer: String, val autumn: String, val winter: String,
                      override val type: EventType = EventType.GOALS
) : Event {
    override fun broadcast(): Boolean = true
}

data class BoardElement(val x: Int, val y: Int, val terrain: Terrain)

data class BoardEvent(val board: Set<BoardElement>, override val type: EventType = EventType.BOARD) : Event

data class CoinsEvent(val coins: Int, override val type: EventType = EventType.COINS) : Event