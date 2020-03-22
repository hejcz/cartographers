package com.github.hejcz.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.hejcz.cartographers.Point
import com.github.hejcz.cartographers.Terrain
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
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.runBlocking
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
                        resources("assets")
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
                            }
                        }
                    }
                }
            }.start(wait = true)

        }

        private suspend fun DefaultWebSocketServerSession.handle(command: Command) {
            when (command.type) {
                "leave" -> {
                    val info = wsToInfo[outgoing]
                    if (info == null) {
                        sendError("game not found")
                        return
                    }
                    newGameLock.lock()
                    wsToInfo.filterValues { it.room == info.room }.onEach { wsToInfo.remove(it.key) }
                    gidToRoom.filterValues { it == info.room }.onEach { gidToRoom.remove(it.key) }
                    newGameLock.unlock()
                }
                "create" -> {
                    val (nick, gid) = mapper.readValue<JoinGameData>(command.data.toString())
                    if (wsToInfo[outgoing] != null) {
                        sendError("ALREADY_IN_GAME")
                        return
                    }
                    val room = gidToRoom[gid]
                    if (room != null) {
                        sendError("ROOM_EXISTS")
                        return
                    }
                    newGameLock.lock()
                    val roomAfterLock = gidToRoom[gid]
                    if (roomAfterLock != null) {
                        newGameLock.unlock()
                        sendError("ROOM_EXISTS")
                        return
                    }
                    val newRoom = Room(gid)
                    newRoom.join(Nick(nick)) { outgoing.sendBlocking(Frame.Text(mapper.writeValueAsString(it))) }
                    gidToRoom[gid] = newRoom
                    newGameLock.unlock()
                    wsToInfo[outgoing] = PlayerInfo(nick, newRoom)
                    sendEvent("CREATE_SUCCESS", mapper.writeValueAsString(nick))
                }
                "rooms" -> {
                    if (wsToInfo[outgoing] != null) {
                        sendError("ALREADY_IN_GAME")
                        return
                    }
                    sendEvent("rooms", mapper.writeValueAsString(gidToRoom.keys))
                }
                "join" -> {
                    val (nick, gid) = mapper.readValue<JoinGameData>(command.data.toString())
                    if (wsToInfo[outgoing] != null) {
                        sendError("ALREADY_IN_GAME")
                        return
                    }
                    val room = gidToRoom[gid]
                    if (room != null) {
                        // TODO
                        room.join(Nick(nick)) { outgoing.sendBlocking(Frame.Text(mapper.writeValueAsString(it))) }
                        wsToInfo[outgoing] = PlayerInfo(nick, room)
                        sendEvent("JOIN_SUCCESS", mapper.writeValueAsString(nick))
                        return
                    }
                    sendError("NO_SUCH_ROOM")
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

private suspend fun DefaultWebSocketServerSession.sendEvent(type: String, data: String) {
    outgoing.send(Frame.Text("""[{"type": "$type", "data": $data}]"""))
}

