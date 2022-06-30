package com.mevron.rides.rider.socket.domain

interface ISocketManager {

    fun connect() : Boolean
    fun disconnect(): Boolean
    fun<T> emitEvent(event: SocketEvent, data: T)
}