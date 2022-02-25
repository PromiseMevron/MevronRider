package com.mevron.rides.rider.remote.socket

import io.socket.client.IO
import io.socket.client.Socket

import java.net.URISyntaxException

object SocketHandler {

    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket(uiid: String, lat: String, lng: String) {
        //0e66aea8-569f-4adc-953e-27f65eec4e7e
        try {
// "http://10.0.2.2:3000" is the network your Android emulator must use to join the localhost network on your computer
// "http://localhost:3000/" will not work
// If you want to use your physical phone you could use the your ip address plus :3000
// This will allow your Android Emulator and physical device at your home to connect to the server
            val mOptions = IO.Options()
            mOptions.query = "uuid=$uiid"
            mOptions.query = "lat=$lat"
            mOptions.query = "long=$lng"
            mSocket = IO.socket("http://staging.mevron.com:8081/?uuid=${uiid}&lat=${lat}&long=${lng}")
        } catch (e: URISyntaxException) {

        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }
}


/*/?uuid=${uiid}&lat=${lat}&long=${lng}*/