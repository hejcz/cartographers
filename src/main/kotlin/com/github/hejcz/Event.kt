package com.github.hejcz

interface Events {
    fun replaceAll(vararg events: Event)
    fun of(nick: String): Set<Event>
    fun replace(nick: String, event: Event)
}

class InMemoryEvents(private val nicks: List<String>) : Events {
    private var map = nicks.map { it to emptySet<Event>() }.toMap()

    override fun replaceAll(vararg events: Event) {
        map = nicks.map { it to events.toSet() }.toMap()
    }

    override fun of(nick: String): Set<Event> {
        return map[nick] ?: emptySet()
    }

    override fun replace(nick: String, event: Event) {
        map = map + (nick to setOf(event))
    }
}

interface Event

data class NewCardEvent(val card: String) : Event

data class ScoresEvent(val scores: Map<String, Int>) : Event

data class ErrorEvent(val error: String) : Event

data class DrawMonsterEvent(val board: String) : Event