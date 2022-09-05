package com.mevron.rides.rider.util

object Constants {
    const val TRIP_ID: String = "TRIP_ID"
    const val BASE_URL = "https://sandbox.mevron.com/"
  //  const val BASE_URL = "http://staging.mevron.com:8083/"
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
   val MAX_VALUE = "CAR"
   val MIN_VALUE = "CAR"
    val ThePaymentModel = "PaymentModel"
    val PICK_UP_LAT = "PICK_UP_LAT"
    val DROP_OFF_LAT = "DROP_OFF_LAT"
    val PICK_UP_LNG = "PICK_UP_LNG"
    val DROP_OFF_LNG = "DROP_OFF_LNG"
    val PICK_UP_ADD = "PICK_UP_ADD"
    val DROP_OFF_ADD = "DROP_OFF_ADD"
    val HOME = "home"
    val SUPPORT_NUMBER = "SUPPORT_NUMBER"
    val WORK = "work"
    val OTHER = "others"


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