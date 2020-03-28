package com.github.hejcz.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.hejcz.cartographers.Event
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.sendBlocking

interface PlayerChannel {
    fun isActive(): Boolean
    fun send(events: Set<Event>)
}

class WsChannel(private val outgoing: SendChannel<Frame>, private val eventMapper: ObjectMapper) : PlayerChannel {
    @ExperimentalCoroutinesApi
    override fun isActive() = !outgoing.isClosedForSend

    override fun send(events: Set<Event>) {
        outgoing.sendBlocking(Frame.Text(eventMapper.writeValueAsString(events)))
    }
}