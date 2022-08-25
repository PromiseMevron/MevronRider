package com.mevron.rides.rider.shared.ui.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.util.Constants
import java.util.Locale

class LocationProcessor {

    private fun getAddressFromLocation(
        context: Context,
        location: LatLng?,
        onAddressObtainedCallback: (LocationModel) -> Unit
    ) {

        try {
            if (location != null) {
                val geoCoder = Geocoder(context, Locale.getDefault())

                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                val address = if (addresses.isNotEmpty())
                    addresses[0].getAddressLine(0)
                else ""
                onAddressObtainedCallback(
                    LocationModel(
                        location.latitude,
                        location.longitude,
                        address
                    )
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    fun animateToCurrentPosition(
        activity: AppCompatActivity,
        googleMap: GoogleMap?,
        callback: (LocationModel) -> Unit,
        onLocationRequestFailed: () -> Unit
    ) {
        //  Toast.makeText(context, "12121", Toast.LENGTH_SHORT).show()
        getLocationProvider(activity)?.lastLocation?.addOnSuccessListener {
            //  Toast.makeText(context, "22", Toast.LENGTH_LONG).show()
            val location = it
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)

                getAddressFromLocation(activity, currentLocation, callback)
                val cameraPosition = CameraPosition.Builder()
                    .bearing(0.toFloat())
                    .target(currentLocation)
                    .zoom(18.5.toFloat())
                    .build()
                googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                //  getAddress(context, location.latitude, location.longitude, pickupAddressTextField)
            } else {
                onLocationRequestFailed()
            }
        }?.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun getLocationProvider(activity: AppCompatActivity): FusedLocationProviderClient? {
        return LocationServices.getFusedLocationProviderClient(activity)
    }

    fun requestLocationPermission(context: Fragment) {
       context.requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), Constants.LOCATION_REQUEST_CODE
        )
    }

    fun checkLocationPermission(context: Context?, onSuccess: () -> Unit = {}, onPermissionRequired: () -> Unit) {
        if (context?.let {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED) {
            onPermissionRequired()
        } else {
            onSuccess()
        }
    }
}