package com.mevron.rides.rider.socket.domain

import io.socket.client.Socket

interface ISocketManager {

    fun socketInstance(): Socket?
    fun connect(next: () -> Unit) : Boolean
    fun disconnect(): Boolean
    fun<T> emitEvent(event: SocketEvent, data: T)
}