package com.mevron.rides.rider.socket.data

import android.util.Log
import com.google.gson.Gson
import com.mevron.rides.rider.data.DriverLocationRepository
import com.mevron.rides.rider.domain.IDriverLocationRepository
import com.mevron.rides.rider.domain.IOpenBookedStateRepository
import com.mevron.rides.rider.domain.ITripStateRepository
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.home.model.DriverLocationModel
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import com.mevron.rides.rider.socket.domain.*
import com.mevron.rides.rider.socket.domain.models.*
import com.mevron.rides.rider.util.Constants
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

private const val URL = "http://staging.mevron.com:8086"

private const val TAG = "SocketManager"

class SocketManager(
    private val preferenceRepository: IPreferenceRepository,
    private val tripStateRepository: ITripStateRepository,
    private val openBookedStateRepository: IOpenBookedStateRepository,
    private val driverLocationRepository: IDriverLocationRepository
) : ISocketManager {

    private var socket: Socket? = null

    override fun socketInstance(): Socket? = socket

    override fun connect(next: () -> Unit): Boolean {
        try {
            socket = IO.socket(URL)
            Log.d(TAG, "Connecting socket")

        } catch (e: URISyntaxException) {
            Log.e(TAG, "Error Connecting socket $e")
            return false
        }

        observeConnectedEvent(next)

        socket?.open()
        return true
    }

    private fun observeConnectedEvent(next: () -> Unit) {
        Log.d(TAG, "searching drivers uyuy 2")
        socket?.on(EVENT_STARTED) {
            Log.d(TAG, "connected $it")
            val uuid = preferenceRepository.getStringForKey(Constants.UUID)
            socket?.emit(CONNECTED, "{\"uuid\":\"${uuid}\", \"type\": \"rider\"}")
            // socket?.emit(CONNECTED, Connected.toData(arrayOf("uuid", "type"), arrayOf(uuid, type)))
           // observeTripStatusEvent()
            observeSearchingDriverEvent()
            observeNearByDrivers()
            observeStateMachine()
            observeDriverLocation()
           print ("121212122 ${socket?.connected()}")
            Log.d("SOCKET", "The socket bool is 1 ${socket?.connected()}")
            next()
        }

    }

    private fun observeSearchingDriverEvent() {
        socket?.on(SEARCH_DRIVERS) {
            Log.d(TAG, "searching drivers uyuy 1")
            if (it.isNotEmpty()) {
                try {
                    val data = it[0].toString()
                    val gson = Gson()
                    val driverSearchData = gson.fromJson(data, DriverSearchData::class.java)
                    Log.d(TAG, "searching drivers $driverSearchData")
                    tripStateRepository.setTripState(TripState.DriverSearchState(driverSearchData))
                } catch (error: Throwable) {
                    Log.d(TAG, "searching drivers error")
                    Log.e(TAG, "Error parsing searching drivers $error")
                }
            }else{
                Log.d(TAG, "searching drivers empty")

            }
        }
    }

    private fun observeNearByDrivers() {
        socket?.on(SEARCH_DRIVERS) {
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
        socket?.on(TRIP_STATE_MACHINE) {
            try {
                if (it.isNotEmpty()) {
                    val data = it[0].toString()
                    val gson = Gson()
                    val stateMachineData = gson.fromJson(data, StateMachineSocketResponse::class.java)
                    tripStateRepository.setTripState(TripState.StateMachineState(stateMachineData))
                    Log.d(TAG, "StateMachine: $stateMachineData")
                }
            } catch (error: Throwable) {
                Log.e(TAG, "Error observing state machine $error")
            }
        }
    }

    private fun observeDriverLocation() {
        socket?.on(TRIP_LOCATION) {
            try {
                if (it.isNotEmpty()) {
                    val data = it[0].toString()
                    val gson = Gson()
                    val locationData = gson.fromJson(data, DriverLocationModel::class.java)
                    driverLocationRepository.settDriverState(locationData)
                    Log.d(TAG, "driverLocation: $locationData")
                }
            } catch (error: Throwable) {
                Log.e(TAG, "Error observing driverLocation $error")
            }
        }
    }

    private fun observeTripStatusEvent() {
        socket?.on(TRIP_STATUS) {
            if (it.isNotEmpty()) {
                try {
                 //   val data = it[0].toString()
                   // val gson = Gson()
                  //  val tripStatus = gson.fromJson(data, TripStatus::class.java)
                    Log.d(TAG, "Trip Status $it")
                    openBookedStateRepository.setTripState(true)
                  //  tripStateRepository.setTripState(TripState.RideAccepted)
                } catch (error: Throwable) {
                    Log.e(TAG, "Error observingTripStatus $error")
                }
            }
        }
    }

    override fun <T> emitEvent(event: SocketEvent, data: T) {
        val gson = Gson()
        socket?.emit(event.name, gson.toJson(data))
    }

    override fun disconnect(): Boolean {
        Log.d(TAG, "disconnected sockeket")
        if (socket?.isActive == true) {
            socket?.disconnect()
            return true
        }

        return false
    }
}
