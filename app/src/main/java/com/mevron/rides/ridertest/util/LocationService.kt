package com.mevron.rides.ridertest.util

import android.Manifest
import android.R
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.mevron.rides.ridertest.home.model.AddressModel
import com.mevron.rides.ridertest.remote.LocationStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

const val REQUEST_CHECK_SETTINGS = 101
const val REQUEST_LOCATION_PERMISSION = 202
object LocationService{

    private val TAG = this::class.java.simpleName
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var task: Task<LocationSettingsResponse>
    var requestingLocation = false


    private val locationCallback = object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {

            //println("LocationResult: $result")
            //return from here if result is null
            p0 ?: return

            assignLocation(p0.lastLocation)
        }
    }

    fun initialize(context: Activity){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        locationRequest = LocationRequest.create().apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        task = client.checkLocationSettings(builder.build())

        checkLocationStatus(context)
    }

    fun checkLocationStatus(context: Activity){
        task.addOnSuccessListener {
            startLocationRequest(context)
        }
        task.addOnFailureListener{
            if(it is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    it.startResolutionForResult(context, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun startLocationRequest(context: Activity){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            requestingLocation = true
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback, Looper.myLooper()!!
            )
        }else{
            requestPermissions(context)
        }
    }

    fun requestPermissions(activity: Activity) {
        val shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            )

        fun requestPermission(){
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
            )
        }

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            LauncherUtil.showSnackbarIndefinite(
                activity, "Enable Mevron to access your location", "Show",
                ::requestPermission, activity.findViewById(R.id.content)
            )
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            requestPermission()
        }
    }

    /*  fun stopLocationUpdate(context: Activity){
          context.stopService(Intent(context, ForegroundLocationService::class.java))
          fusedLocationProviderClient.removeLocationUpdates(locationCallback)
          requestingLocation = false
      }*/

    private fun assignLocation(location: Location?){
        if(location == null)
            return
        //println("Location: $location")
        LocationSingleton.setLocation(location)
    }


}

object LocationSingleton{
    private var location: Location? = null
    //var context: Activity? = null
    private val livedataValue = MutableLiveData<LocationStatus>()

   fun setContext(context: Activity){
       if (getContext() == null){
        //   getContext() = context
       }
   }

    fun getContext(): Activity?{
        return  null
    }


    fun getLocation() = Transformations.distinctUntilChanged(livedataValue)

    fun setLocation(location: Location?){
        this.location = location

        if (LocationSingleton.location != null){
            livedataValue.postValue(LocationStatus.Success(LocationSingleton.location))
        }
        else{
            //check if context is null
            if(getContext() != null){
                //check if location permission is granted
                if (ActivityCompat.checkSelfPermission(
                        getContext()!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED) {

                    //permission is not granted, request permission
                    LocationService.requestPermissions(getContext()!!)
                    livedataValue.postValue(LocationStatus.RequestPermission)

                } else {

                    //permission is granted request location again
                    LocationService.checkLocationStatus(getContext()!!)
                    livedataValue.postValue(LocationStatus.RequestLocation)

                }
            }else{
                livedataValue.postValue(LocationStatus.Error())
                //return error cause location is null and context is null so we cant request location
                //on a null context
            }
        }
    }

    fun getSingleLocation(): Location?{
        return location
    }

    private fun getLocationObject(): LiveData<LocationStatus> {

        if (location != null){
            livedataValue.postValue(LocationStatus.Success(location))
            return livedataValue
        }
        else{
            //check if context is null
            if(getContext() != null){
                //check if location permission is granted
                if (ActivityCompat.checkSelfPermission(
                        getContext()!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED) {

                    //permission is not granted, request permission
                    LocationService.requestPermissions(getContext()!!)
                    livedataValue.postValue(LocationStatus.RequestPermission)
                    return livedataValue
                } else {

                    //permission is granted request location again
                    LocationService.checkLocationStatus(getContext()!!)
                    livedataValue.postValue(LocationStatus.RequestLocation)
                    return livedataValue
                }
            }else{
                livedataValue.postValue(LocationStatus.Error())
                //return error cause location is null and context is null so we cant request location
                //on a null context
                return livedataValue
            }
        }
    }

    fun getAddressFromLocation(context: Context, location: Location?): AddressModel? {

        return try {
            if (location != null) {
                val geoCoder = Geocoder(context, Locale.getDefault())

                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                val address = if (addresses.isNotEmpty())
                    addresses[0].getAddressLine(0)
                else ""

                AddressModel(
                    address = address,
                    long = location.longitude,
                    lat = location.latitude
                )
            }else null
        } catch (ex: Exception){
            ex.printStackTrace()
            null
        }
    }

    fun getLocationFromAddress(context: Context?, strAddress: String?, action: (data: List<AddressModel>) -> Unit){
        CoroutineScope(Dispatchers.Main).launch(AppErrorHandler.handler) {
            val coder = Geocoder(context)
            val returnValue = mutableListOf<AddressModel>()

            val address: List<Address>? = withContext(Dispatchers.IO) {
                coder.getFromLocationName(strAddress, 5)
            }
            address?.forEach{
                returnValue.add(
                    AddressModel(
                        address = it.getAddressLine(0),
                        lat = it.latitude, long = it.longitude
                    ))
            }

            action(returnValue)
        }
    }
}

