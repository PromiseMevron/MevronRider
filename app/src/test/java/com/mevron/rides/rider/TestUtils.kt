package com.mevron.rides.rider

import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.remote.model.Success
import io.mockk.every
import io.mockk.mockk
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

fun <T> mockError(): Response<T> {
    val body: ResponseBody = mockk()
    val mediaType: MediaType = mockk()
    every { body.contentType() }.returns(mediaType)
    every { body.contentLength() }.returns(2)

    return Response.error(401, body)
}

fun mockDefaultResponse(): Response<GeneralResponse> {
    val body = GeneralResponse(
        success = Success(message = "test", status = "SUCCESS")
    )
    val response: Response<GeneralResponse> = mockk {
        every { body() }.returns(body)
        every { isSuccessful }.returns(true)
    }

    return response
}