package com.github.hejcz.http

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.hejcz.cartographers.ErrorCode
import com.github.hejcz.cartographers.GameOptions
import com.github.hejcz.cartographers.Point
import com.github.hejcz.cartographers.Terrain
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.SendChannel
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

data class Command(val type: String, val data: JsonNode)
@JsonIgnoreProperties(ignoreUnknown = true)
data class JoinGameData(val nick: String?, val gid: String?, val reconnectToken: String?)
data class CreateGameData(val nick: String, val gid: String, val options: Map<String, String>)
data class DrawData(val points: Array<Point>, val terrain: Terrain)
data class JoinedData(val nick: String, val roomId: String, val reconnectToken: String)

data class PlayerInfo(val nick: String, val room: Room)

class App {
    companion object {
        private val mapper = ObjectMapper().registerModule(KotlinModule())
        private val newGameLock = ReentrantLock()
        private val wsToInfo = ConcurrentHashMap<SendChannel<Frame>, PlayerInfo>()
        private val reconnectTokenToInfo = ConcurrentHashMap<String, PlayerInfo>()
        private val gidToRoom = ConcurrentHashMap<String, Room>()

        @JvmStatic
        fun main(args: Array<String>) {
            embeddedServer(Netty, System.getProperty("SERVER_PORT", "8080").toInt()) {
                install(WebSockets) {
                    pingPeriodMillis = 3000
                    timeoutMillis = 15000
                }
                routing {
                    get("/") {
                        call.respondRedirect("/game/index.html", true)
                    }
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
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }.start(wait = true)

        }

        private suspend fun DefaultWebSocketServerSession.handle(command: Command) {
            when (command.type) {
                "create" -> {
                    val (nick, gid, opts) = mapper.readValue<CreateGameData>(command.data.toString())
                    if (nick.isBlank()) {
                        sendError(ErrorCode.NICK_CANT_BE_EMPTY)
                        return
                    }
                    if (gid.isBlank()) {
                        sendError(ErrorCode.ROOM_ID_CANT_BE_EMPTY)
                        return
                    }
                    val room = gidToRoom[gid]
                    if (room != null) {
                        sendError(ErrorCode.ROOM_EXISTS)
                        return
                    }
                    newGameLock.lock()
                    val roomAfterLock = gidToRoom[gid]
                    if (roomAfterLock != null) {
                        newGameLock.unlock()
                        sendError(ErrorCode.ROOM_EXISTS)
                        return
                    }
                    val newRoom = Room(gid, GameOptions(
                        swapBoardsOnMonsters = opts["swap"] == "true",
                        advancedBoard = opts["advanced"] == "true",
                        rectangularShape = opts["rectangular"] == "true"
                    ))
                    WsChannel(outgoing, mapper)
                    newRoom.join(Nick(nick), WsChannel(outgoing, mapper))
                    gidToRoom[gid] = newRoom
                    newGameLock.unlock()
                    val token = UUID.randomUUID().toString()
                    val info = PlayerInfo(nick, newRoom)
                    reconnectTokenToInfo[token] = info
                    wsToInfo[outgoing] = info
                    sendEvent("CREATE_SUCCESS", mapper.writeValueAsString(JoinedData(nick, gid, token)))
                }
                "join" -> {
                    val (nick, gid, reconnectToken) = mapper.readValue<JoinGameData>(command.data.toString())
                    if (reconnectToken != null) {
                        val info = reconnectTokenToInfo[reconnectToken]
                        if (info != null) {
                            if (info.room.join(Nick(info.nick), WsChannel(outgoing, mapper), true)) {
                                wsToInfo[outgoing] = PlayerInfo(info.nick, info.room)
                                sendEvent("JOIN_SUCCESS", mapper.writeValueAsString(JoinedData(info.nick, info.room.gid, reconnectToken)))
                            }
                        } else {
                            sendError(ErrorCode.CANT_JOIN)
                        }
                        return
                    }
                    if (nick.isNullOrBlank()) {
                        sendError(ErrorCode.NICK_CANT_BE_EMPTY)
                        return
                    }
                    if (gid.isNullOrBlank()) {
                        sendError(ErrorCode.ROOM_ID_CANT_BE_EMPTY)
                        return
                    }
                    if (wsToInfo[outgoing] != null) {
                        sendError(ErrorCode.ALREADY_IN_GAME)
                        return
                    }
                    val room = gidToRoom[gid]
                    if (room != null) {
                        // TODO
                        if (room.join(Nick(nick), WsChannel(outgoing, mapper))) {
                            val token = UUID.randomUUID().toString()
                            val info = PlayerInfo(nick, room)
                            reconnectTokenToInfo[token] = info
                            wsToInfo[outgoing] = info
                            sendEvent("JOIN_SUCCESS", mapper.writeValueAsString(JoinedData(nick, gid, token)))
                        }
                        return
                    }
                    sendError(ErrorCode.NO_SUCH_ROOM)
                }
                "ping" -> {}
                else -> {
                    val info = wsToInfo[outgoing]
                    if (info == null) {
                        sendError(ErrorCode.GAME_NOT_FOUND)
                        return
                    }
                    when (command.type) {
                        "start" -> info.room.start(Nick(info.nick))
                        "draw" -> info.room.draw(Nick(info.nick), mapper.readValue(command.data.toString()))
                        "undo" -> info.room.undo(Nick(info.nick))
                        else -> println("$info $command")
                    }
                }
            }
        }
    }

}

private suspend fun DefaultWebSocketServerSession.sendError(error: ErrorCode) {
    outgoing.send(Frame.Text("""[{"type": "ERROR", "error": "$error"}]"""))
}

private suspend fun DefaultWebSocketServerSession.sendEvent(type: String, data: String) {
    outgoing.send(Frame.Text("""[{"type": "$type", "data": $data}]"""))
}

