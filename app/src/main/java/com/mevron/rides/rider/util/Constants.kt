package com.mevron.rides.rider.util

object Constants {
  //  const val BASE_URL = "https://mevron-rider.herokuapp.com/"
    const val BASE_URL = "http://staging.mevron.com:8081/"
    const val DATABASE_NAME = "alphally_db"
    const val SHARED_PREF_KEY = "alphally"
    const val TOKEN = "token"
    const val LOCATION_REQUEST_CODE = 4555

    fun isNewNumberType(number: String): Boolean {
        return if (number.isEmpty() || number.length < 4) {
            false
        }
        else {
            (
                    number.substring(0, 4) == "0904" ||
                            number.substring(0, 4) == "0901" ||
                            number.substring(0, 3) == "904" ||
                            number.substring(0, 3) == "901" ||
                            number.substring(0, 4) == "0913" ||
                            number.substring(0, 3) == "913")
        }
    }
}