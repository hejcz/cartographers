package com.github.hejcz.http

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.hejcz.cartographers.*
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

data class Command(val type: String, val data: JsonNode)
data class JoinGameData(val nick: String, val gid: String)
data class DrawData(val points: Array<Point>, val terrain: Terrain)

data class PlayerInfo(val nick: String, val room: Room)

class App {
    companion object {
        private val mapper = ObjectMapper().registerModule(KotlinModule())
        private val newGameLock = ReentrantLock()
        private val wsToInfo = ConcurrentHashMap<SendChannel<Frame>, PlayerInfo>()
        private val gidToRoom = ConcurrentHashMap<String, Room>()

        @JvmStatic
        fun main(args: Array<String>) {
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
                                    is Frame.Text -> handle(mapper.readValue(frame.readText()))
                                }
                            } catch (ex: Throwable) {
                                ex.printStackTrace()
                                throw ex
                            }
                        }
                    }
                }
            }.start(wait = true)

        }

        private suspend fun DefaultWebSocketServerSession.handle(command: Command) {
            when (command.type) {
                "join" -> {
                    val (nick, gid) = mapper.readValue<JoinGameData>(command.data.toString())
                    if (wsToInfo[outgoing] != null) {
                        sendError("ALREADY_IN_GAME")
                    }
                    val room = gidToRoom[gid]
                    if (room != null) {
                        // TODO
                        room.join(Nick(nick)) { runBlocking { outgoing.send(Frame.Text(mapper.writeValueAsString(it))) } }
                        wsToInfo[outgoing] = PlayerInfo(nick, room)
                        sendEvent("JOIN_SUCCESS")
                        return
                    }
                    newGameLock.lock()
                    val roomAfterLock = gidToRoom[gid]
                    if (roomAfterLock != null) {
                        newGameLock.unlock()
                        // TODO
                        roomAfterLock.join(Nick(nick)) { runBlocking { outgoing.send(Frame.Text(mapper.writeValueAsString(it))) } }
                        wsToInfo[outgoing] = PlayerInfo(nick, roomAfterLock)
                        sendEvent("JOIN_SUCCESS")
                        return
                    }
                    val newRoom = Room(gid)
                    // TODO
                    newRoom.join(Nick(nick)) { runBlocking { outgoing.send(Frame.Text(mapper.writeValueAsString(it))) } }
                    gidToRoom[gid] = newRoom
                    newGameLock.unlock()
                    wsToInfo[outgoing] = PlayerInfo(nick, newRoom)
                    sendEvent("CREATE_SUCCESS")
                }
                "start" -> {
                    val info = wsToInfo[outgoing]
                    if (info == null) {
                        sendError("game not found")
                        return
                    }
                    info.room.start(Nick(info.nick))
                }
                "draw" -> {
                    val info = wsToInfo[outgoing]
                    if (info == null) {
                        sendError("game not found")
                        return
                    }
                    info.room.draw(Nick(info.nick), mapper.readValue(command.data.toString()))
                }
            }
        }
    }

}

private suspend fun DefaultWebSocketServerSession.sendError(error: String) {
    outgoing.send(Frame.Text("""[{"type": "error", "error": "$error"}]"""))
}

private suspend fun DefaultWebSocketServerSession.sendEvent(type: String) {
    outgoing.send(Frame.Text("""[{"type": "$type"}]"""))
}

