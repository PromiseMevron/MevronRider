package com.mevron.rides.rider.socket.data

import android.util.Log
import com.google.gson.Gson
import com.mevron.rides.rider.domain.ITripStateRepository
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import com.mevron.rides.rider.socket.domain.CONNECTED
import com.mevron.rides.rider.socket.domain.Connected
import com.mevron.rides.rider.socket.domain.EVENT_STARTED
import com.mevron.rides.rider.socket.domain.ISocketManager
import com.mevron.rides.rider.socket.domain.SEARCH_DRIVERS
import com.mevron.rides.rider.socket.domain.SocketEvent
import com.mevron.rides.rider.socket.domain.TRIP_STATE_MACHINE
import com.mevron.rides.rider.socket.domain.TRIP_STATUS
import com.mevron.rides.rider.socket.domain.models.DriverSearchData
import com.mevron.rides.rider.socket.domain.models.NearByDriversData
import com.mevron.rides.rider.socket.domain.models.StateMachineData
import com.mevron.rides.rider.socket.domain.models.TripStatus
import com.mevron.rides.rider.util.Constants
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

private const val URL = "http://staging.mevron.com:8086"

private const val TAG = "SocketManager"

class SocketManager(
    private val preferenceRepository: IPreferenceRepository,
    private val tripStateRepository: ITripStateRepository
) : ISocketManager {

    private lateinit var socket: Socket

    override fun connect(): Boolean {
        try {
            socket = IO.socket(URL)
            Log.d(TAG, "Connecting socket")
        } catch (e: URISyntaxException) {
            Log.e(TAG, "Error Connecting socket $e")
            return false
        }

        observeConnectedEvent()
        observeTripStatusEvent()
        observeSearchingDriverEvent()
        observeNearByDrivers()
        observeStateMachine()

        socket.open()
        return true
    }

    private fun observeConnectedEvent() {
        socket.on(EVENT_STARTED) {
            Log.d(TAG, "connected")
            val uuid = preferenceRepository.getStringForKey(Constants.UUID)
            val type = "rider"
            socket.emit(CONNECTED, Connected.toData(arrayOf("uuid", "type"), arrayOf(uuid, type)))
        }
    }

    private fun observeSearchingDriverEvent() {
        socket.on(SEARCH_DRIVERS) {
            if (it.isNotEmpty()) {
                try {
                    val data = it[0].toString()
                    val gson = Gson()
                    val driverSearchData = gson.fromJson(data, DriverSearchData::class.java)
                    Log.d(TAG, "searching drivers $driverSearchData")
                    tripStateRepository.setTripState(TripState.DriverSearchState(driverSearchData))
                } catch (error: Throwable) {
                    Log.e(TAG, "Error parsing searching drivers $error")
                }
            }
        }
    }

    private fun observeNearByDrivers() {
        socket.on(SEARCH_DRIVERS) {
            if (it.isNotEmpty()) {
                try {
                    val data = it[0].toString()
                    val gson = Gson()
                    val nearByDriversData = gson.fromJson(data, NearByDriversData::class.java)
                    tripStateRepository.setTripState(TripState.NearByDriversState(nearByDriversData))
                    Log.d(TAG, "Nearby Drivers $nearByDriversData")
                } catch (error: Throwable) {
                    Log.e(TAG, "Error observing nearby drivers $error")
                }
            }
        }
    }

    private fun observeStateMachine() {
        socket.on(TRIP_STATE_MACHINE) {
            try {
                if (it.isNotEmpty()) {
                    val data = it[0].toString()
                    val gson = Gson()
                    val stateMachineData = gson.fromJson(data, StateMachineData::class.java)
                    tripStateRepository.setTripState(TripState.StateMachineState(stateMachineData))
                    Log.d(TAG, "StateMachine: $stateMachineData")
                }
            } catch (error: Throwable) {
                Log.e(TAG, "Error observing state machine $error")
            }
        }
    }

    private fun observeTripStatusEvent() {
        socket.on(TRIP_STATUS) {
            if (it.isNotEmpty()) {
                try {
                    val data = it[0].toString()
                    val gson = Gson()
                    val tripStatus = gson.fromJson(data, TripStatus::class.java)
                    Log.d(TAG, "Trip Status $tripStatus")
                    tripStateRepository.setTripState(TripState.TripStatusState(tripStatus))
                } catch (error: Throwable) {
                    Log.e(TAG, "Error observingTripStatus $error")
                }
            }
        }
    }

    override fun <T> emitEvent(event: SocketEvent, data: T) {
        val gson = Gson()
        socket.emit(event.name, gson.toJson(data))
    }

    override fun disconnect(): Boolean {
        if (socket.isActive) {
            socket.disconnect()
            return true
        }

        return false
    }
}