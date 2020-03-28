package com.github.hejcz.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
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
import kotlinx.coroutines.channels.sendBlocking
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

data class Command(val type: String, val data: JsonNode)
data class JoinGameData(val nick: String, val gid: String)
data class CreateGameData(val nick: String, val gid: String, val options: Map<String, String>)
data class DrawData(val points: Array<Point>, val terrain: Terrain)
data class JoinedData(val nick: String, val roomId: String)

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
                        handle(Command("leave", NullNode.getInstance()))
                    }
                }
            }.start(wait = true)

        }

        private suspend fun DefaultWebSocketServerSession.handle(command: Command) {
            when (command.type) {
                "leave" -> {
                    val info = wsToInfo[outgoing]
                    if (info == null) {
                        sendError(ErrorCode.GAME_NOT_FOUND)
                        return
                    }
                    info.room.leave(Nick(info.nick))
                    wsToInfo.remove(outgoing)
                }
                "create" -> {
                    val (nick, gid, opts) = mapper.readValue<CreateGameData>(command.data.toString())
                    if (wsToInfo[outgoing] != null) {
                        sendError(ErrorCode.ALREADY_IN_GAME)
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
                        advancedBoard = opts["advanced"] == "true"
                    ))
                    WsChannel(outgoing, mapper)
                    newRoom.join(Nick(nick), WsChannel(outgoing, mapper))
                    gidToRoom[gid] = newRoom
                    newGameLock.unlock()
                    wsToInfo[outgoing] = PlayerInfo(nick, newRoom)
                    sendEvent("CREATE_SUCCESS", mapper.writeValueAsString(JoinedData(nick, gid)))
                }
                "rooms" -> {
                    if (wsToInfo[outgoing] != null) {
                        sendError(ErrorCode.ALREADY_IN_GAME)
                        return
                    }
                    sendEvent("ROOMS", mapper.writeValueAsString(gidToRoom.keys))
                }
                "join" -> {
                    val (nick, gid) = mapper.readValue<JoinGameData>(command.data.toString())
                    if (wsToInfo[outgoing] != null) {
                        sendError(ErrorCode.ALREADY_IN_GAME)
                        return
                    }
                    val room = gidToRoom[gid]
                    if (room != null) {
                        // TODO
                        if (room.join(Nick(nick), WsChannel(outgoing, mapper))) {
                            wsToInfo[outgoing] = PlayerInfo(nick, room)
                            sendEvent("JOIN_SUCCESS", mapper.writeValueAsString(JoinedData(nick, gid)))
                        }
                        return
                    }
                    sendError(ErrorCode.NO_SUCH_ROOM)
                }
                "start" -> {
                    val info = wsToInfo[outgoing]
                    if (info == null) {
                        sendError(ErrorCode.GAME_NOT_FOUND)
                        return
                    }
                    info.room.start(Nick(info.nick))
                }
                "draw" -> {
                    val info = wsToInfo[outgoing]
                    if (info == null) {
                        sendError(ErrorCode.GAME_NOT_FOUND)
                        return
                    }
                    info.room.draw(Nick(info.nick), mapper.readValue(command.data.toString()))
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

