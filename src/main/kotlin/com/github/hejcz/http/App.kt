package com.github.hejcz.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.hejcz.cartographers.Game
import com.github.hejcz.cartographers.GameImplementation
import com.github.hejcz.cartographers.Point
import com.github.hejcz.cartographers.Terrain
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.content.*
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

data class Command(val type: String, val data: JsonNode)
data class JoinGameData(val nick: String, val gid: String)
data class DrawData(val points: Array<Point>, val terrain: Terrain)

inline class GameId(val gid: String)

fun main() {
    val mapper = ObjectMapper().registerModule(KotlinModule())

    val newGameLock = ReentrantLock()
    val gameIdToGame = ConcurrentHashMap<GameId, Game>()
    val gameUpdateLocks = ConcurrentHashMap<GameId, Lock>()
    val wsToGameId = ConcurrentHashMap<SendChannel<Frame>, GameId>()
    val wsToNickname = ConcurrentHashMap<SendChannel<Frame>, String>()

    fun withGameCreationLock(action: () -> Unit) {
        try {
            newGameLock.lock()
            action()
        } finally {
            newGameLock.unlock()
        }
    }

    fun withGameLock(gid: GameId, action: () -> Unit) {
        try {
            gameUpdateLocks[gid]?.lock()
            action()
        } finally {
            gameUpdateLocks[gid]?.unlock()
        }
    }

    embeddedServer(Netty, System.getProperty("SERVER_PORT", "8080").toInt()) {
        install(WebSockets)
        routing {
            static("game") {
                resources("css")
                resources("js")
                resource("index.html")
            }
            webSocket("/api") {
                for (frame in incoming) {
                    try {
                        when (frame) {
                            is Frame.Text -> {
                                val command = mapper.readValue<Command>(frame.readText())
                                when (command.type) {
                                    "join" -> {
                                        withGameCreationLock {
                                            runBlocking {
                                                val (nick, gid) = mapper.readValue<JoinGameData>(command.data.toString())
                                                val currentGameId = wsToGameId[outgoing]
                                                when {
                                                    currentGameId != null -> sendError("You are already in game ${currentGameId.gid}")
                                                    else -> {
                                                        val gameId = GameId(gid)
                                                        val existingGame = gameIdToGame[gameId]
                                                        wsToGameId[outgoing] = gameId
                                                        wsToNickname[outgoing] = nick
                                                        if (existingGame != null) {
                                                            withGameLock(gameId) {
                                                                gameIdToGame[gameId]?.join(nick)
                                                            }
                                                            sendEvent("JOIN_SUCCESS")
                                                        } else {
                                                            gameIdToGame[gameId] = GameImplementation().join(nick)
                                                            gameUpdateLocks[gameId] = ReentrantLock()
                                                            sendEvent("CREATE_SUCCESS")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    "start" -> {
                                        wsToGameId[outgoing]?.let { gid ->
                                            withGameLock(gid) {
                                                gameIdToGame[gid]?.start()?.let { newGame: Game ->
                                                    gameIdToGame[gid] = newGame
                                                    runBlocking {
                                                        wsToGameId.filterValues { gameId -> gameId == gid }
                                                            .forEach { (channel, _) ->
                                                                wsToNickname[channel]?.let { nick ->
                                                                    channel.send(
                                                                        Frame.Text(
                                                                            mapper.writeValueAsString(
                                                                                newGame.recentEvents(
                                                                                    nick
                                                                                )
                                                                            )
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                    }
                                                }
                                            }
                                        } ?: kotlin.run { sendError("game not found") }
                                    }
                                    "draw" -> {
                                        val (points, terrain) = mapper.readValue<DrawData>(command.data.toString())
                                        val coords = points.map { it.x to it.y }.toSet()
                                        val nick = wsToNickname[outgoing]
                                        if (nick == null) {
                                            sendError("game not found")
                                        } else {
                                            wsToGameId[outgoing]?.let { gid ->
                                                withGameLock(gid) {
                                                    gameIdToGame[gid]?.draw(nick, coords, terrain)?.let {
                                                        gameIdToGame[gid] = it
                                                        runBlocking {
                                                            outgoing.send(
                                                                Frame.Text(
                                                                    mapper.writeValueAsString(
                                                                        it.recentEvents(
                                                                            nick
                                                                        )
                                                                    )
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            } ?: kotlin.run { sendError("no game found") }
                                        }
                                    }
                                    else -> throw RuntimeException("unknown command ${command.type}")
                                }
                            }
                        }
                    } catch (ex: Throwable) {
                        ex.printStackTrace()
                    }
                }
            }
        }
    }.start(wait = true)
}

private suspend fun DefaultWebSocketServerSession.sendError(error: String) {
    outgoing.send(Frame.Text("""{"type": "error", "errors": [$error]}"""))
}

private suspend fun DefaultWebSocketServerSession.sendEvent(type: String) {
    outgoing.send(Frame.Text("""{"type": "$type"}"""))
}

