package com.mevron.rides.rider.supportpages.data.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.mockError
import com.mevron.rides.rider.supportpages.data.model.*
import com.mevron.rides.rider.supportpages.data.network.SupportAPI
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import retrofit2.Response

class SupportRepositoryTest {

    private val api: SupportAPI = mockk()
    private val repository = SupportRepository(api)

    @Test
    fun `when SupportRepository#getNotification is invoked - sends api request to get address`(): Unit =
        runBlocking {
            val body = NotificationBody(id = 1, title = "", description = "kk", createAt = "ww")
            val data = NotificationData(
                current_page = 1,
                last_page = 1,
                per_page = 1,
                to = 1,
                result = listOf(body),
                from = 1
            )
            val responseBody = NotificationResponse(
                success = NotificationSuccess(
                    notificationData = data,
                    message = "testMessage",
                    status = "testStatus"
                )
            )
            val response: Response<NotificationResponse> =
                Response.success(responseBody)
            coEvery { api.getNotifications() }.coAnswers { response }
            val result = repository.getNotifications()
            coVerify { api.getNotifications() }
            assert(result is DomainModel.Success)
        }

    @Test
    fun `when SupportRepository#getPromo is invoked - sends api request to get address`(): Unit =
        runBlocking {
            val data = PromoData(
                id = 1, description = "kk", date = "s", discount = "ss", remain = "ww"
            )
            val responseBody = PromoResponse(
                success = PromoSuccess(
                    promoData = listOf(data),
                    message = "testMessage",
                    status = "testStatus"
                )
            )
            val response: Response<PromoResponse> =
                Response.success(responseBody)
            coEvery { api.getPromo() }.coAnswers { response }
            val result = repository.getPromos()
            coVerify { api.getPromo() }
            assert(result is DomainModel.Success)
        }

    @Test
    fun `when SupportRepository#getPromo fails - returns error`(): Unit =
        runBlocking {
            val response: Response<PromoResponse> = mockError()
            coEvery { api.getPromo() }.coAnswers { response }
            val result = repository.getPromos()
            assert(result is DomainModel.Error)
        }

    @Test
    fun `when SupportRepository#getNotification fails - returns error`(): Unit =
        runBlocking {
            val response: Response<NotificationResponse> = mockError()
            coEvery { api.getNotifications() }.coAnswers { response }
            val result = repository.getNotifications()
            assert(result is DomainModel.Error)
        }
}