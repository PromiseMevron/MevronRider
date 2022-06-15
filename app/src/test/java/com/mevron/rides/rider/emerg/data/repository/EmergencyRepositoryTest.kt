package com.mevron.rides.rider.emerg.data.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.emerg.data.model.AddContactRequest
import com.mevron.rides.rider.emerg.data.model.GetContactSuccess
import com.mevron.rides.rider.emerg.data.model.GetContactsResponse
import com.mevron.rides.rider.emerg.data.model.UpdateEmergencyContact
import com.mevron.rides.rider.emerg.data.network.EmergencyAPI
import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.remote.model.Success
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import retrofit2.Response

class EmergencyRepositoryTest {

    private val api: EmergencyAPI = mockk()
    private val repository = EmergencyRepository(api)

    @Test
    fun `when EmergencyRepository#getContact is invoked - sends api request to get address`(): Unit =
        runBlocking {
            val data = GetContactsResponse(
                success = GetContactSuccess(
                    message = "",
                    status = "",
                    contactData = mutableListOf()
                )
            )
            val response: Response<GetContactsResponse> =
                Response.success(data)
            coEvery { api.getEmergency() }.coAnswers { response }
            val result = repository.getEmergencyContact()
            coVerify { api.getEmergency() }
            assert(result is DomainModel.Success)
        }

    @Test
    fun `when EmergencyRepository#updateContact is invoked - sends api request to uodate address`(): Unit =
        runBlocking {
            val req = UpdateEmergencyContact(mutableListOf(), name = "", phoneNumber = "")
            val data = GeneralResponse(success = Success(message = "", status = ""))
            val response: Response<GeneralResponse> =
                Response.success(data)
            coEvery { api.updateEmergency(id = "aa", data = any()) }.coAnswers { response }
            val result = repository.updateEmergencyContact(id = "aa", data = req)
            coVerify { api.updateEmergency(id = "aa", data = req) }
            assert(result is DomainModel.Success)
        }

    @Test
    fun `when EmergencyRepository#saveContact is invoked - sends api request to save address`(): Unit =
        runBlocking {
            val req = AddContactRequest(sets = mutableListOf())
            val data = GeneralResponse(success = Success(message = "", status = ""))
            val response: Response<GeneralResponse> =
                Response.success(data)
            coEvery { api.saveEmergency(data = any()) }.coAnswers { response }
            val result = repository.saveEmergencyContact(data = req)
            coVerify { api.saveEmergency(data = req) }
            assert(result is DomainModel.Success)
        }


    @Test
    fun `when EmergencyRepository#deleteContact is invoked - sends api request to dekete address`(): Unit =
        runBlocking {
            val data = GeneralResponse(success = Success(message = "", status = ""))
            val response: Response<GeneralResponse> =
                Response.success(data)
            coEvery { api.deleteEmergency(id = any()) }.coAnswers { response }
            val result = repository.deleEmergencyContact(id = "aa")
            coVerify { api.deleteEmergency(id = "aa") }
            assert(result is DomainModel.Success)
        }

}