package com.mevron.rides.rider.util

import android.content.Context
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun View.hideKeyboard() {
    val hideAction = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    hideAction.hideSoftInputFromWindow(windowToken, 0)
}


fun EditText.getString(): String{
    return this.text.toString()
}