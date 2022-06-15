package com.mevron.rides.rider.savedplaces.data.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.mockError
import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.remote.model.Success
import com.mevron.rides.rider.savedplaces.data.model.*
import com.mevron.rides.rider.savedplaces.data.network.AddressAPI
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import retrofit2.Response

class AddressRepositoryTest {
    private val api: AddressAPI = mockk()
    private val repository = AddressRepository(api)

    @Test
    fun `when AddressRepository#getSavedAddresses is invoked - sends api request to get address`(): Unit =
        runBlocking {
            val data = GetAddressData(
                type = "ss",
                name = "ll",
                uuid = "22",
                latitude = "22",
                longitude = "22",
                address = "weew"
            )
            val listData = listOf(data)
            val responseBody = GetSavedAddress(
                success = GetAddSuccess(
                    addData = listData,
                    message = "testMessage",
                    status = "testStatus"
                )
            )
            val response: Response<GetSavedAddress> =
                Response.success(responseBody)
            coEvery { api.getAddress() }.coAnswers { response }
            val result = repository.getSavedAddresses()
            coVerify { api.getAddress() }
            assert(result is DomainModel.Success)
        }


    @Test
    fun `when AddressRepository#getSavedAddresses fails - returns error`(): Unit =
        runBlocking {

            val response: Response<GetSavedAddress> = mockError()

            coEvery { api.getAddress() }.coAnswers { response }

            val result = repository.getSavedAddresses()

            assert(result is DomainModel.Error)
        }

    @Test
    fun `when AddressRepository#addAnAddress is invoked - sends api request to save address`(): Unit =
        runBlocking {
            val request = SaveAddressRequest(
                address = "aa",
                latitude = "aaa",
                longitude = "sss",
                name = "sss",
                type = "sss"
            )

            val responseBody = GeneralResponse(
                success = Success(status = "ww", message = "eeee")
            )
            val response: Response<GeneralResponse> =
                Response.success(responseBody)
            coEvery { api.saveAddress(any()) }.coAnswers { response }
            val result = repository.addAnAddress(request)
            coVerify { api.saveAddress(request) }
            assert(result is DomainModel.Success)
        }

    @Test
    fun `when AddressRepository#addAnAddress fails - returns error`(): Unit =
        runBlocking {

            val response: Response<GeneralResponse> = mockError()

            coEvery { api.saveAddress(any()) }.coAnswers { response }
            val request: SaveAddressRequest = mockk()
            val result = repository.addAnAddress(request)

            assert(result is DomainModel.Error)
        }

    @Test
    fun `when AddressRepository#updateAddress is invoked - sends api request to update address`(): Unit =
        runBlocking {
            val request = UpdateAddress(
                address = "aa",
                latitude = "aaa",
                longitude = "sss",
                name = "sss",
                type = "sss"
            )

            val responseBody = GeneralResponse(
                success = Success(status = "ww", message = "eeee")
            )
            val response: Response<GeneralResponse> =
                Response.success(responseBody)
            coEvery { api.updateAddress("aa", any()) }.coAnswers { response }
            val result = repository.updateAnAddress("aa", request)
            coVerify { api.updateAddress("aa", request) }
            assert(result is DomainModel.Success)
        }

    @Test
    fun `when AddressRepository#updateAddress fails - returns error`(): Unit =
        runBlocking {

            val response: Response<GeneralResponse> = mockError()

            coEvery { api.updateAddress("aa", any()) }.coAnswers { response }
            val request: UpdateAddress = mockk()
            val result = repository.updateAnAddress("aa", request)

            assert(result is DomainModel.Error)
        }
}