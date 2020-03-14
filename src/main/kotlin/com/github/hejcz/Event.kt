package com.github.hejcz

interface Events {
    fun replaceAll(vararg event: Event)
    fun of(nick: String): List<Event>
    fun replace(nick: String, event: Event)
}

class InMemoryEvents : Events {
    override fun replaceAll(vararg event: Event) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun of(nick: String): List<Event> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun replace(nick: String, event: Event) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

interface Event

data class NewCardEvent(val card: String) : Event

data class ScoresEvent(val scores: Map<String, Int>) : Event

data class ErrorEvent(val error: String) : Event