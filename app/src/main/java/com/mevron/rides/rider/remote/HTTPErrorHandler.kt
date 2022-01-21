package com.mevron.rides.rider.remote

import com.google.gson.Gson
import com.mevron.rides.rider.remote.errors.Error
import com.mevron.rides.rider.remote.errors.ErrorModel
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

object HTTPErrorHandler {

    fun <T: Any?> handleErrorWithCode(response: Response<T>): ErrorModel?{
        return try{

            if(!response.isSuccessful){
                val error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java)
                error
            }else null
        }catch (ex: Exception){
            ex.printStackTrace()
      ErrorModel(error = Error("Unknown Error", "Failed"))
        }
    }

    fun httpFailWithCode(t: Exception): ErrorModel{
        return if(t is SocketTimeoutException || t is HttpException)
            ErrorModel(error = Error("Network Error", "Failed"))
        else
            ErrorModel(error = Error("Unknown Error", "Failed"))
    }
}