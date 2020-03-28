package com.github.hejcz.http

import com.github.hejcz.cartographers.*
import java.time.Instant
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.fixedRateTimer

inline class Nick(val nick: String)

/**
 * This class is thread safe.
 */
class Room(gid: String, gameOptions: GameOptions) {
    private var lock: Lock = ReentrantLock()

    private var game: Game = GameImplementation(gid, options = gameOptions)

    private val callbacks: MutableMap<Nick, PlayerChannel> = mutableMapOf()

    private val kicker: Timer = fixedRateTimer("kicker", true, Date.from(Instant.now()), 2000) {
        synchronized(lock) {
            if (game.isFinished()) {
                cancelKicker()
            } else {
                callbacks.entries.removeIf {
                    val toKickOut = !it.value.isActive()
                    if (toKickOut) {
                        game.leave(it.key.nick)
                        println("Kicking out ${it.key}")
                    }
                    toKickOut
                }
            }
        }
    }

    private fun cancelKicker() {
        kicker.cancel()
    }

    fun join(nick: Nick, channel: PlayerChannel): Boolean = synchronized(lock) {
            when {
                nick in callbacks -> {
                    channel.send(setOf(ErrorEvent(ErrorCode.NICK_TAKEN)))
                    false
                }
                game.canJoin(nick.nick) -> {
                    game = game.join(nick.nick)
                    callbacks[nick] = channel
                    sendEvents()
                    true
                }
                else -> {
                    channel.send(setOf(ErrorEvent(ErrorCode.CANT_JOIN)))
                    false
                }
            }
    }

    fun start(nick: Nick) = synchronized(lock) {
        game = game.start(nick.nick)
        println(game)
        sendEvents()
    }

    fun draw(nick: Nick, data: DrawData) {
        val points = data.points.toSet()
        synchronized(lock) {
            game = game.draw(nick.nick, points, data.terrain)
            println(game)
            sendEvents()
        }
    }

    private fun sendEvents() {
        val allEvents = game.recentEvents()
        for ((n, playerEvents) in allEvents) {
            callbacks[Nick(n)]?.send(playerEvents)
        }
    }

    fun undo(nick: Nick) = synchronized(lock) {
        game = game.undo(nick.nick)
        sendEvents()
    }
}