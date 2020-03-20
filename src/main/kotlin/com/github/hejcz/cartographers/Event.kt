package com.github.hejcz.cartographers

enum class EventType {
    ACCEPTED_SHAPE,
    NEW_CARD,
    SCORE,
    ERROR
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
}

interface Event {
    val type: EventType
}

data class AcceptedShape(val terrain: Terrain, val points: Collection<Point>,
                         override val type: EventType = EventType.ACCEPTED_SHAPE) : Event

data class NewCardEvent(val card: String, val ruins: Boolean, override val type: EventType = EventType.NEW_CARD) : Event

data class ScoresEvent(val scores: Map<String, Int>, override val type: EventType = EventType.SCORE) : Event

data class ErrorEvent(val error: String, override val type: EventType = EventType.ERROR) : Event
