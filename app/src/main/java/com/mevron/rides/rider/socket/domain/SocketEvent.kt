package com.mevron.rides.rider.socket.domain

/**
 *
const val NEARBY_DRIVERS = "nearby_drivers"
const val SEARCH_DRIVERS = "search_drivers"
const val START_RIDE = "start_ride"
const val END_RIDE = "end_ride"
const val TRIP_LOCATION= "trip_location"
const val STATE_MANAGER = "state_manager"
const val RIDE_RATING = "rider_state_machine"
const val RIDER_RATING = "rider_rating"
 */
abstract class SocketEvent {

    abstract val name: String

    fun <T : Any, U : Any> toData(keys: Array<T>, values: Array<U>): Map<T, U> {
        val map = mutableMapOf<T, U>()

        for (index in keys.indices) {
            map[keys[index]] = values[index]
        }

        return map.toMap()
    }
}

object Connected : SocketEvent() {
    override val name: String = CONNECTED
}

data class ConnectedData(
    val uuid: String,
    val type: String
)


