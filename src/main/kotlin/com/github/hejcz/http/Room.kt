package com.github.hejcz.http

import com.github.hejcz.cartographers.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

inline class Nick(val nick: String)

/**
 * This class is thread safe.
 */
class Room(gid: String) {
    private var lock: Lock = ReentrantLock()
    private var game: Game = GameImplementation(gid)
    private val callbacks: MutableMap<Nick, (Set<Event>) -> Unit> = mutableMapOf()

    fun join(nick: Nick, sendToSingle: (Set<Event>) -> Unit): Boolean = synchronized(lock) {
            when {
                game.canJoin(nick.nick) -> {
                    game = game.join(nick.nick)
                    callbacks[nick] = sendToSingle
                    sendToSingle(game.recentEvents(nick.nick))
                    true
                }
                nick in callbacks -> {
                    sendToSingle(setOf(ErrorEvent(ErrorCode.NICK_TAKEN)))
                    false
                }
                else -> {
                    sendToSingle(setOf(ErrorEvent(ErrorCode.CANT_JOIN)))
                    false
                }
            }
    }

    fun start(nick: Nick) {
        synchronized(lock) {
            game = game.start(nick.nick)
            handleEvents(nick)
        }
    }

    fun draw(nick: Nick, data: DrawData) {
        val xyPoints = data.points.map { it.x to it.y }.toSet()
        synchronized(lock) {
            game = game.draw(nick.nick, xyPoints, data.terrain)
            println(game)
            println(game.boardOf(nick.nick))
            handleEvents(nick)
        }
    }

    private fun handleEvents(nick: Nick) {
        val events = game.recentEvents(nick.nick)
        val (allPlayersEvents, otherEvents) = events.partition { it.broadcast() }
        if (allPlayersEvents.isNotEmpty()) {
            callbacks.forEach { it.value(allPlayersEvents.toSet()) }
        }
        if (otherEvents.isNotEmpty()) {
            (callbacks.getValue(nick))(otherEvents.toSet())
        }
    }

    fun leave(nick: Nick) {
        synchronized(lock) {
            game.leave(nick.nick)
            callbacks.remove(nick)
        }
    }
}