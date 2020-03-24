package com.github.hejcz.http

import com.github.hejcz.cartographers.*
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
                    sendEvents()
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
            sendEvents()
        }
    }

    fun draw(nick: Nick, data: DrawData) {
        val xyPoints = data.points.map { it.x to it.y }.toSet()
        synchronized(lock) {
            game = game.draw(nick.nick, xyPoints, data.terrain)
            println(game)
            sendEvents()
        }
    }

    private fun sendEvents() {
        val events = game.recentEvents()
        for ((n, event) in events) {
            callbacks[Nick(n)]?.invoke(event)
        }
    }

    fun leave(nick: Nick) {
        synchronized(lock) {
            game.leave(nick.nick)
            callbacks.remove(nick)
        }
    }
}