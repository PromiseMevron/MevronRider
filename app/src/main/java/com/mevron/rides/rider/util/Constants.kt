package com.mevron.rides.rider.util

object Constants {
    //  const val BASE_URL = "https://mevron-rider.herokuapp.com/"
    const val BASE_URL = "http://staging.mevron.com:8083/"
    const val SHARED_PREF_KEY = "com.mevron.rides.riderMeveronApp.PREF_NAME"
    const val TOKEN = "TOKEN"
    const val REFERRAL = "REFERRAL"
    const val REFERRAL_STATUS = "REFERRAL_STATUS"
    const val UUID = "UUID"
    const val PROFILE = "PROFILE"
    const val FIRST_NAME = "FIRST_NAME"
    const val LAST_NAME = "LAST_NAME"
    const val EMAIL = "EMAIL"
    const val LOCATION_REQUEST_CODE = 4555
    const val PHONE_NUMBER = "PHONE_NUMBER"
    val CAR = "CAR"
    val ThePaymentModel = "PaymentModel"
    val LocationModelPick = "LocationModelPick"
    val LocationModelDrop = "LocationModelDrop"

    fun isNewNumberType(number: String): Boolean {
        return if (number.isEmpty() || number.length < 4) {
            false
        } else {
            number.substring(0, 4) == "0904" ||
                    number.substring(0, 4) == "0901" ||
                    number.substring(0, 3) == "904" ||
                    number.substring(0, 3) == "901" ||
                    number.substring(0, 4) == "0913" ||
                    number.substring(0, 3) == "913"
        }
    }
}