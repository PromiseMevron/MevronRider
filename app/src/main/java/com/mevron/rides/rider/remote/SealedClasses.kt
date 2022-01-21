package com.mevron.rides.rider.remote

import com.mevron.rides.rider.remote.errors.ErrorModel

sealed class GenericStatus<out T>{

    data class Success<T>(val data: T?): GenericStatus<T>()
    data class Error(val error: ErrorModel?): GenericStatus<Nothing>()
    data class Unaunthenticated(val error: ErrorModel?): GenericStatus<Nothing>()

}

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T): Resource<T> = Resource(status = Status.SUCCESS, data = data, message = null)

        fun  error(data: ErrorModel?): Resource<ErrorModel> =
            Resource(status = Status.ERROR, data = data, message = null)

        fun <T> loading(data: T?): Resource<T> = Resource(status = Status.LOADING, data = data, message = null)

        fun <T> unauthenticated(data: T?) : Resource<T> = Resource(status = Status.UNAUTHENTICATED,data = null, message = "session expired, please do sign in")
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING,
    UNAUTHENTICATED
}