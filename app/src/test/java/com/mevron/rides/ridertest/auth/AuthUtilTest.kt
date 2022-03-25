package com.mevron.rides.ridertest.auth

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AuthUtilTest {

   /* @Test
    fun getEmailPattern() {

    }*/


    @Test
    fun phoneFieldValidate() {
        val result = AuthUtil.phoneFieldValidate("")
        assertThat(result).isFalse()
    }

    @Test
    fun phoneFieldValidateFilled() {
        val result = AuthUtil.phoneFieldValidate("12333333333")
        assertThat(result).isTrue()
    }

    @Test
    fun otpFieldValidate() {
        val otp = AuthUtil.otpFieldValidate("123456")
        assertThat(otp).isTrue()
    }

    @Test
    fun otpFieldValidateContainString() {
        val otp = AuthUtil.otpFieldValidate("123r56")
        assertThat(otp).isFalse()
    }

    @Test
    fun otpFieldValidateIncomplete() {
        val otp = AuthUtil.otpFieldValidate("12356")
        assertThat(otp).isFalse()
    }

    @Test
    fun validateEmail() {
        val ema = AuthUtil.validateEmail("asss@aaa.com")
        assertThat(ema).isTrue()
    }

    @Test
    fun validateEmailEmpty() {
        val ema = AuthUtil.validateEmail("")
        assertThat(ema).isFalse()
    }

    @Test
    fun validateEmailWrong() {
        val ema = AuthUtil.validateEmail("sssss")
        assertThat(ema).isFalse()
    }

    @Test
    fun validateFullName() {
        val nam = AuthUtil.validateFullName("prro dddd")
        assertThat(nam).isTrue()
    }

    @Test
    fun validateFullNameOnlyFirst() {
        val nam = AuthUtil.validateFullName("prro")
        assertThat(nam).isFalse()
    }

    @Test
    fun validateFullNameEmpty() {
        val nam = AuthUtil.validateFullName("")
        assertThat(nam).isFalse()
    }

    @Test
    fun validateFullNameEmptySpace() {
        val nam = AuthUtil.validateFullName(" ")
        assertThat(nam).isTrue()
    }
}