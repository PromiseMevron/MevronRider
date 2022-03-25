package com.mevron.rides.ridertest.remote.geolocation

import com.mevron.rides.ridertest.home.model.GeoDirectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface GeoAPIInterface {

    @GET
    fun getGeoDirections(@Url url: String): Call<GeoDirectionsResponse>
}