package com.mevron.rides.rider.auth

import java.lang.NumberFormatException

object AuthUtil {
    const val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    fun phoneFieldValidate(phone: String): Boolean {
        return  phone.isNotEmpty()
    }

    fun otpFieldValidate(otp: String): Boolean {
        val intParse = try {
            otp.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
        return  otp.isNotEmpty() && otp.length == 6 && intParse
    }

    fun validateEmail(email: String): Boolean {
        return email.matches(emailPattern.toRegex())
    }

    fun validateFullName(name: String): Boolean {
        val fullName = name.split(" ")
        if (fullName.size < 2){
            return false
        }
        if (fullName[1].replace(" ", "").length < 2){
            return false
        }
        return  name.contains(" ") && fullName.isNotEmpty() && fullName.size > 1
    }


}